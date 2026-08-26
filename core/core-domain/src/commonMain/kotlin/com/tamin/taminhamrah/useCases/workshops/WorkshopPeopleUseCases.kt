package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationResultDN
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationDN
import com.tamin.taminhamrah.model.workshop.NewMemberIsNewDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberQuery
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberQuery
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderQuery
import com.tamin.taminhamrah.repository.WorkShopsRepository

/** کارکنان of one workshop. */
class GetWorkshopMembersUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(query: WorkshopMemberQuery): PagedListDN<WorkshopMemberDN> =
        repository.getWorkshopMembers(query)
}

/** ذینفعان of one workshop. */
class GetWorkshopStackHoldersUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(query: WorkshopStackHolderQuery): PagedListDN<WorkshopStackHolderDN> =
        repository.getWorkshopStackHolders(query)
}

/** استعلام بدهی کارگاه — one record, not a list. */
class GetWorkshopDebtInquiryUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(workshopId: String, branchCode: String): WorkshopDebtInquiryDN =
        repository.getWorkshopDebtInquiry(workshopId, branchCode)
}

/** نام نویسی غیر حضوری بیمه شده — registrations drafted or submitted for this workshop. */
class GetRecentlyAddedMembersUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(query: WorkshopNewMemberQuery): PagedListDN<WorkshopNewMemberDN> =
        repository.getRecentlyAddedMembers(query)
}

/**
 * Confirms a drafted registration and returns its tracking code.
 *
 * Whether a row *may* be confirmed is [WorkshopNewMemberDN.canConfirm]; the caller checks that and
 * tells the user, because "already submitted" is a message, not an exception.
 */
class ConfirmRecentlyAddedMemberUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(requestId: Long): String =
        repository.confirmRecentlyAddedMember(requestId)
}

class DeleteRecentlyAddedMemberUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(personalId: Long) = repository.deleteRecentlyAddedMember(personalId)
}

/**
 * Whether this national id is someone the organisation has never registered.
 *
 * Asked before a create so an existing person is edited rather than added a second time — the
 * service answers with their `personalId` when it already knows them.
 */
class CheckNewMemberIsNewUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(nationalId: String): NewMemberIsNewDN =
        repository.checkNewMemberIsNew(nationalId)
}

/** ثبت نام‌نویسی غیرحضوری — creates the registration the three-step form filled in. */
class CreateNewMemberRegistrationUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(
        request: NewMemberRegistrationDN,
    ): NewMemberRegistrationResultDN = repository.createNewMemberRegistration(request)
}
