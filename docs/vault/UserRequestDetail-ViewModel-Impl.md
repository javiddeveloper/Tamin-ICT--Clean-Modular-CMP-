# UserRequestDetailViewModel Implementation

This document describes the complete implementation of the `UserRequestDetailViewModel` based on the legacy `ShowRequestInfoViewModel` spec, following the Tamin KMP Clean Architecture.

## Architecture Layers

```
core-network
    └─ DTOs: UserRequestDetailDTO.kt
    └─ RemoteDataSource: UserRequestDetailRemoteDataSource.kt

core-domain
    └─ Repository Interface: UserRequestDetailRepository.kt
    └─ Domain Models: UserRequestDetailDN.kt

core-data
    └─ Repository Implementation: OldUserRequestDetailRepositoryImpl.kt
    └─ Mapper: userRequestDetail.kt

core-ui
    └─ Presentation Models: UserRequestDetailPR.kt

feature:userRequest
    └─ Contract: UserRequestDetailContract.kt
    └─ ViewModel: UserRequestDetailViewModel2.kt
```

## API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | /shortterm-request/getProcessData/{referenceId} | Get short-term request status |
| GET | /shortterm-request/getShorttermRequestLoadData/{referenceId} | Get short-term request load data |
| GET | /StpBaseinfo/ShorttermBarTypes | Get pregnancy status types |
| GET | /StpBaseinfo/ShorttermBarChild | Get pregnancy child types |
| GET | /debit-objection/objection-request/{objectionNumber} | Get Article 16 objection info |
| GET | /wage-assignment/request/{requestId} | Get deferred installment info |
| GET | /historyprotest-services/getprotestresult/{referenceId} | Get follow-up objection history |
| GET | /upload-image/{guid}/0/0 | Download attached documents |
| GET | /assets/data/objection-type.json | Get objection type list |

## Features Implemented

### 1. Short-Term Request Details
- Full request status information
- Load data with orthotics, illness, pregnancy, and consequential data
- File list with document types

### 2. Pregnancy Details Enrichment
- Pregnancy status codes (Normal, Caesarean)
- Pregnancy type codes (First, Second, etc.)
- Merged into pregnancy objects per legacy spec

### 3. Article 16 Objection Information
- Objection descriptions and photos
- Document titles from objection type list

### 4. Deferred Installment Certificate
- Borrower information
- Bank details and installment information
- Loan amounts and guarantee values

### 5. Follow-Up Objection History
- Historical objections for non-existing history
- Status, answer, and result descriptions
- Following dates and branch information

### 6. Document Download
- Per-document parallel download
- Base64 or URL content
- Document type mapping

## Data Models

### DTO (Data Transfer Object) - core-network
All models extending `BaseDTO<T>` with server JSON structure

### DN (Domain Model) - core-domain
Pure business logic models, independent of server and UI

### PR (Presentation Model) - core-ui
Ready-to-render models with display strings

### Mapper
`mapToDetail()` transforms DTO → DN → PR following the architecture

## ViewModel State

```kotlin
data class UserRequestDetailState(
    val isLoading: Boolean = false,
    val request: UserRequestDetailPR? = null,
    val error: String? = null,
    val requestReferenceId: String? = null,
    val requestType: Int? = null,
    val objectionNumber: Long? = null,
    val deferredInstallmentId: String? = null
)
```

## Intent Handling

The ViewModel handles multiple intents:
- `LoadDetail`: Load base request data
- `LoadPregnancyDetails`: Load pregnancy-specific data
- `LoadArticle16Details`: Load Article 16 objection data
- `LoadDeferredInstallmentDetails`: Load installment certificate data
- `LoadFollowUpObjectionHistory`: Load objection follow-up history
- `DownloadImage`: Download individual document

## Error Handling

- All requests wrapped in try-catch
- Error messages surface via PR state
- Network errors and 404s handled gracefully
- Empty states supported

## RequestTypeEnumClass

| Type | ServiceId |
|------|-----------|
| ILL_DAY | 10 |
| ORTHOTICS_PROSTHESIS | 12 |
| ARTICLE16 | 26 |
| PREGNANCY | 11 |
| DEFERRED_INSTALLMENT_CERTIFICATE | 22 |
| MEDICAL_COMMISSION | 27 |

## Notes

- All requests use `Authorization: <accessToken>` header
- List endpoints return `{ total, list }` under `data`
- Object endpoints return data directly under `data`
- Pregnancy enrichment only runs when `requestType == PREGNANCING.serviceId`
- Documents downloaded per GUID in parallel
- Objection type list cached locally after first fetch