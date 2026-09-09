package com.tamin.taminhamrah.model.payment

/**
 * The payments this app has to be able to make, each as a ticket the mock gateway will answer.
 *
 * One entry per "pay" endpoint in the services the KMP app is being ported from — see
 * `docs/vault/Payments.md`. They exist so the shared payment flow can be exercised end to end
 * before the features that will issue these tickets are built, and so that each of them can be
 * demonstrated with a plausible amount and reason rather than one generic figure.
 *
 * [mockAmount] and [mockDescription] stand in for what the gateway would answer about a real
 * ticket. They are simulated server payload, not app copy: nothing here is ever rendered in a
 * release build, which is why they are not `strings.xml` entries.
 */
enum class PaymentMockScenario(
    val mockAmount: Long,
    val mockDescription: String,
    val verifierKey: PaymentVerifierKey,
    val verifierReference: String = "",
) {
    /**
     * بدهی کارگاه، پرداخت غیرحضوری — `debit-online-payment/pay-normal-debit`.
     * The gateway's own answer settles it; there is no separate confirmation call.
     */
    WORKSHOP_DEBT(
        mockAmount = 48_500_000L,
        mockDescription = "بدهی کارگاه ۱۲۳۴۵۶۷ - شعبه ۱۲",
        verifierKey = PaymentVerifierKey.NONE,
    ),

    /** قسط بدهی کارفرما — `debit-installment-payment/pay/{debtNumber}`, then `pay-check`. */
    DEBT_INSTALLMENT(
        mockAmount = 12_750_000L,
        mockDescription = "قسط سوم بدهی تقسیط‌شده کارگاه",
        verifierKey = PaymentVerifierKey.WORKSHOP_DEBIT_INSTALLMENT,
        verifierReference = "DBT-99001",
    ),

    /** حق‌بیمه ساختمان — issues its own sheet, then pays through the instalment endpoints. */
    CONSTRUCTION_PREMIUM(
        mockAmount = 31_200_000L,
        mockDescription = "حق‌بیمه ساختمان - برگ پرداخت ۴۴۰۲",
        verifierKey = PaymentVerifierKey.WORKSHOP_DEBIT_INSTALLMENT,
        verifierReference = "BLD-44021",
    ),

    /** حق‌بیمه کارگران ساختمانی — `workers/payDebit`, then `workers/inpectTicket`. */
    CONSTRUCTION_WORKERS(
        mockAmount = 4_980_000L,
        mockDescription = "حق‌بیمه کارگران ساختمانی - دوره تیر ۱۴۰۴",
        verifierKey = PaymentVerifierKey.CONSTRUCTION_WORKERS,
        verifierReference = "WRK-TOKEN-7781",
    ),

    /** بیمه صاحبان حرف و مشاغل آزاد — `sep/online-payment-mobile`, systemType `03`. */
    FREELANCE_INSURANCE(
        mockAmount = 8_640_000L,
        mockDescription = "حق‌بیمه مشاغل آزاد - مرداد ۱۴۰۴",
        verifierKey = PaymentVerifierKey.SPECIAL_INSURED,
        verifierReference = "03",
    ),

    /** بیمه اختیاری — same endpoint family, its own system type. */
    OPTIONAL_INSURANCE(
        mockAmount = 7_310_000L,
        mockDescription = "حق‌بیمه اختیاری - مرداد ۱۴۰۴",
        verifierKey = PaymentVerifierKey.SPECIAL_INSURED,
        verifierReference = "03",
    ),

    /** بیمه دانشجویی — same endpoint family again. */
    STUDENT_INSURANCE(
        mockAmount = 2_150_000L,
        mockDescription = "حق‌بیمه دانشجویی - نیم‌سال دوم",
        verifierKey = PaymentVerifierKey.SPECIAL_INSURED,
        verifierReference = "03",
    ),

    /**
     * The gateway takes the money and the owning service then refuses to confirm it.
     *
     * Not a service in its own right — it is the one outcome the other scenarios cannot produce,
     * and the one the result screen most needs to get right: the user has paid, so offering them
     * the payment button again would be wrong. Answered by the debug-only verifier registered for
     * [PaymentVerifierKey.MOCK_UNCONFIRMED].
     */
    PAID_BUT_UNCONFIRMED(
        mockAmount = 5_000_000L,
        mockDescription = "پرداخت موفق با تأیید ناموفق سرویس",
        verifierKey = PaymentVerifierKey.MOCK_UNCONFIRMED,
    );

    /**
     * A ticket for one run of this scenario.
     *
     * [nonce] must differ between runs. The gateway — real or mock — treats a ticket as spent once
     * it has been through it, so a fixed ticket previews as already-paid the second time round.
     */
    fun ticket(nonce: String): String = "$TICKET_PREFIX$name$TICKET_SEPARATOR$nonce"

    fun toPaymentRequest(nonce: String): PaymentRequestDN = PaymentRequestDN(
        ticket = ticket(nonce),
        verifierKey = verifierKey,
        verifierReference = verifierReference,
    )

    companion object {
        private const val TICKET_PREFIX = "mock:"
        private const val TICKET_SEPARATOR = ":"

        /**
         * The scenario a mock ticket was made for, or null when [ticket] is not one of ours.
         *
         * Encoding the scenario in the ticket rather than holding it in memory is what lets the
         * fake gateway answer correctly after a process death — the same property the real flow
         * relies on, since the user leaves the app to pay.
         */
        fun fromTicket(ticket: String): PaymentMockScenario? {
            if (!ticket.startsWith(TICKET_PREFIX)) return null
            val name = ticket.removePrefix(TICKET_PREFIX).substringBefore(TICKET_SEPARATOR)
            return entries.firstOrNull { it.name == name }
        }
    }
}
