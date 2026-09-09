---
tags: [architecture, howto]
---

# Payments

Every payment in the app goes through one shared flow: **`:feature:payment`**.

A feature never opens the gateway itself, never builds a gateway URL, and never asks the user who
is paying. It calls its own `pay*` endpoint, gets a **ticket**, and hands that ticket over.

```kotlin
val result = payWorkshopDebitUseCase(request)   // the feature's own endpoint
if (result.isPayable) {
    navController.navigateToPayment(result.toPaymentRequest())
}
```

That is the whole integration.

## Why it is split this way

The legacy app (`old_android`) already centralised the gateway screens — `PaymentFragment`,
`TFHFragment`, `PaymentResultFragment` — but the centralisation leaked: `PaymentViewModel`
contained a `when` over `EnumInsuranceType`, read a debt serial number out of DataStore for
employer debt, and grew a second method (`updateWorkersPaymentStatus`) for construction workers.
The shared payment code knew about every feature that used it.

Here the split is **mechanism vs. business**:

| Belongs to `:feature:payment` (one copy) | Belongs to the feature (one per service) |
|---|---|
| Ticket preview, amount, expiry countdown | Calculating the amount, listing instalments, discounts |
| Who is paying (4 payer types + validation) | The `pay*` request body — no two are alike |
| Exchanging a ticket for a gateway URL | The post-payment confirmation call |
| Cancelling / releasing a ticket | What "settled" means for that debt |
| The result screen and receipt | Where the user goes afterwards |

Counted against the legacy endpoint inventory: **4 gateway calls are shared**, **4 endpoints issue
tickets**, and roughly **40 endpoints are per-feature business** that never touch this module.

## The pieces

| Layer | File |
|---|---|
| Models, payer types, statuses | `core-domain/model/payment/PayerType.kt`, `PaymentDN.kt` |
| Repository + verifier contracts | `core-domain/repository/payment/PaymentGatewayRepository.kt` |
| Use cases | `core-domain/useCases/payment/PaymentUseCases.kt` |
| Gateway API (TFH) | `core-network/apiService/payment/PaymentGatewayApiService.kt` |
| Data sources (real / fake / selector) | `core-network/dataSource/paymentSource/` |
| Mapper + repository impl | `core-data/data/mapper/PaymentMapper.kt`, `data/repository/payment/` |
| Screens and routes | `feature/payment/` |

The gateway lives on its own host (`BaseUrlKey.TFH` → `NetworkConstants.TFH_BASE_URL`) behind its
own Ktor client and Ktorfit instance, `tfhHttpClient` / `tfhKtorfit` — so it is overridable from
Developer Options like every other base URL ([[Networking]]).

## The flow

```
feature: pay*  ──ticket──▶  navigateToPayment(PaymentRequestDN)
                                  │
                    PaymentRoute.Checkout
                      GET ticket/current-user/{ticket}   → amount, reason, time left
                      user picks payer type + identifier
                      POST payment-link/{ticket}         → gateway URL
                      ExternalAppLauncher.openUrl(...)   → leaves the app
                                  │
                    PaymentRoute.Result   (Checkout is popped)
                      re-checks on ON_RESUME, and on the mytamin://payment_callback deep link
                      GET ticket/current-user/{ticket}   → paid? failed? expired?
                      PaymentVerifier for the route's key → did the *service* agree?
```

Checkout is popped from the back stack the moment the gateway opens: coming back must land on the
result, and back from the result must never re-offer a ticket that is already spent.

## Post-payment confirmation — `PaymentVerifier`

The gateway only knows whether money moved. Whether the *debt* is settled is a question each
service answers on its own endpoint, and the answer has to be obtainable from a **cold start** —
the user comes back from a browser and the screen that started the payment may be gone. So the
route carries a `PaymentVerifierKey` and a single `verifierReference` string, not a lambda.

To add one:

1. Add a value to `PaymentVerifierKey` (`core-domain/model/payment/PaymentDN.kt`), documenting
   which endpoint it stands for and what `reference` means for it.
2. Implement `PaymentVerifier` in the feature that owns the debt.
3. Register it in that feature's Koin module: `single<PaymentVerifier>(named("...")) { … }`.
   The qualifier only has to be unique; `VerifyPaymentUseCase` collects every registered verifier
   with Koin's `getAll()`, which is what lets a feature contribute one without core-domain or any
   shared list knowing the feature exists.
4. Pass the key and reference in the `PaymentRequestDN`.

A key with no verifier registered resolves to `NotRequired` rather than throwing, so a feature
whose verifier has not been written yet still gets a working payment screen.
`PaymentVerifierWiringTest` covers both the empty and the several-features case.

### The outcome the user is shown

`PaymentOutcomeDN` keeps the two answers apart on purpose:

- **`isFullySuccessful`** — the gateway took the money *and* the service confirmed.
- **`isPaidButUnconfirmed`** — the money left the account but the service would not confirm.
  This must never be shown as a plain failure: telling a user who has already paid to pay again is
  the worst thing this screen can do. They get the reference number and are pointed at a branch.

`PaymentStatus.VERIFYING` counts as settled, matching the legacy client — the payment has gone
through and only the gateway's own bookkeeping is outstanding.

## Mock gateway (Developer Options)

The real gateway is regularly unavailable to developers — the test bank is down, or the ticket
service refuses non-production callers — and a payment screen that cannot be opened cannot be
built. **Developer Options → «شبیه‌سازی درگاه پرداخت»** puts a fake in front of it:

| Mode | Behaviour |
|---|---|
| غیرفعال | the real gateway |
| پاسخ موفق | the ticket previews as unpaid, then reports `SUCCESSFUL` with a reference number |
| پاسخ ناموفق | the ticket previews normally, then reports `FAILED` with nothing taken |

`FakePaymentGatewayRemoteDataSource` answers the *whole* flow, not one call — a fake that always
said "successful" would never exercise the result screen's failure branch, which is the branch
that ships broken. The link it hands back is the app's own `mytamin://payment_callback` deep link,
so "go and pay" re-enters the app exactly as a real return does, with no browser in the loop.

### The payment sandbox

**Developer Options → «سناریوهای پرداخت آزمایشی»** lists every payment the app has to be able to
make — workshop debt, employer instalment, construction premium, construction workers, freelance /
optional / student insurance — each runnable end to end against the mock gateway. The screen lives
in `feature/payment/ui/sandbox/`; the catalogue itself is `PaymentMockScenario` in core-domain.

It exists because the features that will issue these tickets are still being ported. Without it the
shared flow could only be exercised by whichever feature happened to be finished first.

Each scenario carries its own mock amount and reason, plus **the verifier key of the service that
will really own it** — so wiring a real `PaymentVerifier` later needs no change to the sandbox.

Two details worth keeping:

- **Every run gets a fresh ticket** (`mock:<SCENARIO>:<random>`). A gateway treats a ticket as spent
  once it has been through it, so a fixed ticket previews as already-paid — the screen then opens
  expired with `۰۰:۰۰` on the clock and no way to pay. That was a real bug here.
- **The scenario travels in the ticket, not in memory.** The mock gateway parses it back out, which
  is what lets it answer correctly after a process death — the same property the real flow depends
  on, since the user leaves the app to pay.

The last entry, «پرداخت موفق، تأیید ناموفق سرویس», is the one outcome the others cannot produce:
money taken, service refusing to confirm. It routes to `PaymentVerifierKey.MOCK_UNCONFIRMED`, which
`UnconfirmedMockPaymentVerifier` (registered in `paymentModule`) always fails. No production code
sends that key, so it can never shadow a real verifier.

Wiring: `PaymentGatewayRemoteDataSourceSelector` (registered in `RemoteModule`) reads the mode
**per call**, so switching it takes effect on the next payment rather than after an app restart —
unlike the base-URL overrides, which are baked into `HttpClient` singletons at startup.

⚠️ Debug builds only. `DeveloperOptionsRepositoryImpl.getPaymentMockMode()` returns `DISABLED`
outside `AppConfig.isDebug` whatever is stored, exactly like `getEffectiveBaseUrl`.

## Returning from the gateway

One deep link host for the whole app: `mytamin://payment_callback?ticket=…`, declared in
`androidApp/src/main/AndroidManifest.xml` and routed by `MainActivity` into
`PaymentReturnNotifier`. The legacy app had `callback`, `callback_freelance`, `callback_optional`,
`callback_fraction` and `workers_payment_callback`, all doing the same thing.

The result screen also re-checks on `ON_RESUME`, so a missed deep link costs a moment rather than
a wrong answer — the notifier only makes the common case immediate.

## Rules worth keeping

- **Never build a gateway URL outside this module.** `WorkshopMapper` used to concatenate
  `https://tfh.tamin.ir/view/#/payment/` onto a ticket, which put the gateway's hostname somewhere
  no base-URL override could reach. `DebitPaymentDN` now carries `paymentTicket`.
- **Always release an abandoned ticket.** Backing out or running out of time calls
  `cancel-by-user/{ticket}`; a ticket left open blocks the next attempt on the same debt until the
  gateway times it out.
- **`PayerType` is an enum with the gateway's own numbers.** Never compare `personType` as a
  string or a raw int.

## Ported from

`old_android`: `ui/home/services/employer/payment/` (`PaymentFragment`, `TFHFragment`,
`PaymentResultFragment`, `PaymentResultDialogFragment`, `PaymentUserType`) and the TFH endpoints in
`data/remote/services/ServicesService.kt`. See [[Reference-old-android]].

Related: [[Networking]] · [[Navigation]] · [[Dependency-Injection]] · [[Adding-a-Feature]]
