# Documentation: Menu System and Business Logic Mapping

This document describes how the menu system is implemented in TaminHamrah and how it maps to different business domains.

## 1. Data Source: `menu.json`
The `app/src/main/assets/menu.json` file serves as the primary configuration for the services displayed on the Dashboard.

### Key Fields in `menu.json`:
- **`name`**: The display title of the service.
- **`id`**: Unique identifier used for navigation mapping.
- **`type`**: Categorizes the service:
    - `1`: Insured (بیمه شده)
    - `2`: Pensioner (مستمری بگیر)
    - `3`: Employer (کارفرما)
- **`active`**: Boolean flag to enable/disable the service in the UI.
- **`showRole`**: List of roles allowed to see this service:
    - `1`: Insured (بیمه شده)
    - `2`: Pensioner (مستمری بگیر)
    - `3`: Employer (کارفرما)
- **`sorting`**: Determines the display order in the grid.
- **`hiddenForVersions`**: List of app version codes where this service should be hidden.

---

## 2. Technical Implementation

### Data Loading
- **`PreferenceManager.getServices()`**: Reads `menu.json` from assets and parses it into `ServiceResponseModelNew`.
- **`ServicesViewModel.getServiceForDashboard()`**: Triggers the load from repository.
- **`DashboardFragment.onDashboardServicesResponse()`**: Filters the list based on:
    - Selected user mode (`mViewModel.getSelectedModeValue()`).
    - Service `active` status.
    - Version exclusion (`hiddenForVersions`).

### Navigation Mapping
Navigation is handled in `Utility.getPageIdByModelId(id: Int?)`. It maps the service `id` to a Navigation Component destination ID (e.g., `R.id.action_service_to_identity_info`).

---

## 3. Business Logic by Category

### A. Insured Services (`type: 1`)
These services are for active insured members to manage their records and benefits.
- **Identity & Records**:
    - `id: 1` (Identity Info), `id: 6` (All History), `id: 7` (Wage & History), `id: 8` (Combined Records).
- **Requests & Benefits**:
    - `id: 14` (Marriage Gift), `id: 16` (Pregnancy Allowance), `id: 17` (Illness Compensation), `id: 18` (Funeral Grant).
- **Insurance Contracts**:
    - `id: 34` (Student), `id: 36` (Housewives), `id: 37` (Optional), `id: 33` (Freelance).
- **Medical & Health**:
    - `id: 25` (Treatment Eligibility), `id: 26` (Electronic Prescriptions).

### B. Pensioner Services (`type: 2`)
Services tailored for retired members or survivors receiving pensions.
- **Financial**:
    - `id: 105` (Pay Slip/Payroll), `id: 106` (Edict/Hokm), `id: 107` (Wage Certificate).
- **Inquiries**:
    - `id: 104` (Pension Status), `id: 108` (Deferred Installments).
- **Survivor & Disability**:
    - `id: 110` (Girl Survivor Commitment), `id: 112` (Survivor Pension), `id: 113` (Disability Pension).

### C. Employer Services (`type: 3`)
Tools for workshop owners and legal representatives.
- **Workshop Management**:
    - `id: 1001` (Workshops List), `id: 1004` (Employer Info Completion), `id: 1008` (Workshop Inspections).
- **Financial & Debt**:
    - `id: 1009` (Debt Management/Installments), `id: 1006` (Debt Objection Follow-up).
- **Legal Representatives**:
    - `id: 1005` (Representative Management).

---

## 4. User Mode & Role Enforcement
In `DashboardFragment.onItemClick`, the app checks if the user's current status (Active Insured, Pensioner, or Anonymous) matches the `showRole` requirement of the service before allowing navigation. If a user is "Temporary" or "Anonymous", they might be prompted to complete their profile or login.
