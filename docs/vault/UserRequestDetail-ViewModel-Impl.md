# User Request Detail

Port of legacy `ShowRequestInfoViewModel` + type-specific fragments onto the existing `feature:userRequest` module.

The list screen already uses the new `requests` API. Detail keeps that header call (`GET requests/{id}`) and then loads the same type-specific eservices endpoints the old ViewModel called, keyed by `referenceId` + `requestType`.

```
feature:userRequest
    UserRequestDetailViewModel uses GetUserRequestDetailUseCase + GetShowRequestInfoUseCase
core-domain
    UserRequestRepository.getUserRequestDetail / getShowRequestInfo / downloadUserRequestDocument
core-data
    UserRequestRepositoryImpl + ShowRequestInfoMapper (DTO → DN)
core-network
    UserRequestApiService + UserRequestRemoteDataSource(+Impl)
```

## Endpoints (same paths as my-tamin-droid)

| Trigger | Condition | Requests |
| --- | --- | --- |
| `GET requests/{id}` | always on open | header (id, refCode, status, title, `refrenceid`) |
| `GET shortterm-request/getProcessData/{referenceId}` + `GET .../getShorttermRequestLoadData/{referenceId}` | types 10 / 11 / 12 | parallel, then merge |
| `GET StpBaseinfo/ShorttermBarTypes` + `GET StpBaseinfo/ShorttermBarChild` | type 11 only | pregnancy status/type lookup |
| `GET debit-objection/objection-request/{objectionNumber}` | type 26 | Article 16 |
| `GET wage-assignment/request/{requestId}` | type 22 | deferred installment |
| `GET historyprotest-services/getprotestresult/{referenceId}` | type 8 | follow-up objection |
| `GET upload-image/{guid}/0/0` | document download | `DownloadUserRequestDocumentUseCase` |

`referenceId` comes from `UserRequestDN.referenceId` (`refrenceid`); the list `refCode` is the fallback.

Type-specific payloads map into `UserRequestDetailsDN` / `UserRequestDetailsPR` so the detail screen stays one composable that branches on `requestTypeId`.
