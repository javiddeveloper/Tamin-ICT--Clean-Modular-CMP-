package com.tamin.taminhamrah.feature.workshops.di

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.AssignerContractDN
import com.tamin.taminhamrah.model.workshop.AssignerContractQuery
import com.tamin.taminhamrah.model.workshop.AssignerPartyDN
import com.tamin.taminhamrah.model.workshop.BaseDocumentDN
import com.tamin.taminhamrah.model.workshop.BaseDocumentKind
import com.tamin.taminhamrah.model.workshop.ComputationalBaseDN
import com.tamin.taminhamrah.model.workshop.ComputationalBaseQuery
import com.tamin.taminhamrah.repository.WorkShopsRepository

/**
 * TEMPORARY — delete this file, its flag and its two bindings before handover.
 *
 * The test account holds no پیمان it is the واگذارنده of: both
 * `assignersContracts-request-issuance-invoices38` calls answer `{"total":"0","list":[]}` at 200,
 * so جزئیات پیمان, مبانی محاسباتی and جزئیات مبنا cannot be reached on device from live data. This
 * stands the three reads up over in-memory values so those screens can be looked at.
 *
 * It replaces only **this feature's three use cases**, by delegating every other call straight
 * through to the real repository — the app-wide `WorkShopsRepository` binding is untouched, so
 * nothing else in the app changes behaviour while the flag is on.
 */
const val USE_ASSIGNER_CONTRACTS_MOCK = true

/** Everything but the three واگذارندگان reads goes to the real repository. */
internal class MockAssignerContractsRepository(
    private val real: WorkShopsRepository,
) : WorkShopsRepository by real {

    override suspend fun getAssignerContracts(
        query: AssignerContractQuery,
    ): PagedListDN<AssignerContractDN> = PagedListDN(MockContracts, total = MockContracts.size)

    override suspend fun getComputationalBases(
        query: ComputationalBaseQuery,
    ): PagedListDN<ComputationalBaseDN> =
        // The second seeded پیمان has no bases, so the empty state is reachable too.
        if (query.contractRow == "2") {
            PagedListDN(emptyList(), total = 0)
        } else {
            PagedListDN(MockBases, total = MockBases.size)
        }

    override suspend fun getComputationalBasePdf(documentId: String): PdfDownloadDN =
        PdfDownloadDN(pdf = null)
}

private val MockAssigner = AssignerPartyDN(
    workshopId = "0968210170",
    workshopName = "آموزشگاه کامپیوتر توکلی",
    nationalId = "4231098876",
    address = "تهران، خیابان انقلاب، پلاک ۱۲",
    branchCode = "0010",
    branchName = "شعبه ۱۰ تهران",
)

private val MockContracts = listOf(
    AssignerContractDN(
        contractRow = "1",
        contractSequence = "3",
        contractNumber = "44122",
        contractDate = "14010210",
        contractSubject = "خدمات نظافت و پشتیبانی",
        assigner = MockAssigner,
        employer = AssignerPartyDN(
            workshopId = "9028212822",
            workshopName = "دبستان کارن ۲ مجتبی غلامیان",
            nationalId = "1022334455",
            address = "بجنورد، خیابان طالقانی، کوچهٔ ۱۲، پلاک ۴",
            branchCode = "6310",
            branchName = "شعبه ۲ بجنورد",
        ),
    ),
    AssignerContractDN(
        contractRow = "2",
        contractSequence = "4",
        contractNumber = "44880",
        contractDate = "14020601",
        contractSubject = "پیمان ساختمانی (مکانیکی و دستی)",
        assigner = MockAssigner,
        employer = AssignerPartyDN(
            workshopId = "9028212822",
            workshopName = "شرکت راه‌سازی البرز شرق",
            nationalId = "1022334455",
            branchCode = "6310",
            branchName = "شعبه ۲ بجنورد",
        ),
    ),
)

private val MockBases = listOf(
    ComputationalBaseDN(
        letterNumber = "12044",
        sendDate = 1_646_000_000_000L,
        amount = 84_000_000L,
        documents = listOf(
            BaseDocumentDN("img-1", BaseDocumentKind.IMAGE, categoryCode = "1"),
            BaseDocumentDN("pdf-1", BaseDocumentKind.PDF, categoryCode = "4"),
        ),
    ),
    ComputationalBaseDN(
        letterNumber = "12190",
        sendDate = 1_650_000_000_000L,
        amount = 91_500_000L,
        documents = listOf(BaseDocumentDN("img-2", BaseDocumentKind.IMAGE, categoryCode = "3")),
    ),
)
