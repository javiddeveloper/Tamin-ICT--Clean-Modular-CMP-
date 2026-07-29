package com.tamin.taminhamrah.data.remote.models.services.violations

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.data.remote.models.ListDataModel

class ViolationResponse : ListDataModel<Violation>()
class ProvinceResponse : ListDataModel<Province>()

data class SendReportResponse(var data: Violation?) : BaseResponseNew()

data class ViolationUploadResponse(
    val id: Int,
    val officialViolationsEntity: Province? = null,
    val documentFile: String?,
    val documentType: String?,
    val documentName: String?,
    val createdBy: String?,
    val creationTime: Long = 0,
    val lastModifiedBy: String?,
    val lastModificationTime: String?,
) : BaseResponseNew()

data class ViolationRequest(
    val fullName: String = "",
    val fatherName: String = "",
    val nationalCode: String = "",
    val reportSubject: String = "",
    val provinceId: ProvinceId,
    val violationPlace: String = "",
    val violationTime: Long = 0,
    val referReason: String = "",
    val trackingMobileNumber: String = "",
    val reportDescriptions: String = "",
    val haveDocuments: String = "0"
)

data class ProvinceId(val id: String)

//Note same as officialViolationsEntity
data class Violation(
    val id: Int,
    val issueTracking: String?,

    val fullName: String?,
    val fatherName: String?,
    val nationalCode: String?,
    val reportSubject: String?,
    val provinceId: Province? = null,
    val violationPlace: String?,
    val violationTime: Long,
    val referReason: String?,
    val trackingMobileNumber: String?,
    val reportDescriptions: String?,
    val haveDocuments: String?,
    val status: String?,
    val viewedUser: String?,
    val viewedTime: String?,
    val expertDescriptions: String?,
    val createdBy: String?,
    val creationTime: Long,
    val lastModifiedBy: String?,
    val lastModificationTime: Long,
    val statusTitle: String?,
    val documentStatusTitle: String?
)

data class Province(
    val id: String,
    val title: String,
    val status: String,
    val statusStDate: String
)

/** NOTE It is don -> getMyReportsOV()
GET: https://ov.tamin.ir/api/reports/my-reports?page=1&start=0&limit=10&filter=%5B%5D&sort=%5B%5D
ACTIONS: Show selected item
Show and upload documents
Edit Report
Accept and send
DELETE : https://ov.tamin.ir/api/reports/delete/{2840}
 */

/** NOTE It is don -> getProvinceListForOV()
GET : https://ov.tamin.ir/api/province/get-all?page=1&start=0&limit=10&filter=%5B%5D&sort=%5B%5D
{
"status": 200,
"family": "SUCCESSFUL",
"reason": "OK",
"data": {
"total": 35,
"list": [
{
"id": "32",
"title": "البرز",
"status": "1",
"statusStDate": "20090301"
},
{
"id": "07",
"title": " تهران بزرگ",
"status": "2",
"statusStDate": "19970321"
},
{
"id": "14",
"title": "شهرستانهاي استان تهران",
"status": "1",
"statusStDate": "19970321"
},
{
"id": "23",
"title": "گيلان",
"status": "1",
"statusStDate": "19970321"
},
{
"id": "25",
"title": "مازندران",
"status": "1",
"statusStDate": "19970321"
},
{
"id": "10",
"title": "خوزستان",
"status": "1",
"statusStDate": "19970321"
},
{
"id": "20",
"title": "کرمانشاه",
"status": "1",
"statusStDate": "19970321"
},
{
"id": "01",
"title": "آذربايجان شرقي",
"status": "1",
"statusStDate": "19970321"
},
{
"id": "02",
"title": "آذربايجان غربي",
"status": "1",
"statusStDate": "19970321"
},
{
"id": "09",
"title": "خراسان رضوِي",
"status": "1",
"statusStDate": "19970321"
}
] }}
for get insurance list */

/** NOTE It is do -> sendReportOV()
POST: https://ov.tamin.ir/api/reports/save
BODY :
{   "fullName":"مرتضي بيات"
,"fatherName":"محمد علی"
,"nationalCode":"4284422642"
,"reportSubject":"ترس"
,"provinceId":{ "id":"14" }
,"violationPlace":"ناکجا اباد"
,"violationTime":"2023-06-24T20:30:00.000Z"
,"referReason":"ندام کاری"
,"trackingMobileNumber":"09190000000"
,"reportDescriptions":"منسند دارموو ت و نداری"
,"haveDocuments":"1"
}
RESPONSE:
{
"status": 200,
"family": "SUCCESSFUL",
"reason": "OK",
"data": {
"id": 2840,
"issueTracking": "2038",
"fullName": "مرتضي بيات",
"fatherName": "محمد علی",
"nationalCode": "4284422642",
"reportSubject": "ترس",
"provinceId": {
"id": "14",
"title": "شهرستانهاي استان تهران",
"status": "1",
"statusStDate": "19970321"
},
"violationPlace": "ناکجا اباد",
"violationTime": 1687638600000,
"referReason": "ندام کاری",
"trackingMobileNumber": "09190000000",
"reportDescriptions": "منسند دارموو ت و نداری",
"status": "0",
"statusTitle": "موقت",
"documentStatusTitle": "دارای مستندات",
"viewedUser": null,
"viewedTime": null,
"expertDescriptions": null,
"haveDocuments": "1",
"createdBy": "4284422642",
"creationTime": 1687678156852,
"lastModifiedBy": null,
"lastModificationTime": null
}
 */

/** NOTE It is do -> uploadDocumentOV()
POST : https://ov.tamin.ir/api/report-documents/upload
BODY : (as form data) file: (binary)
type: .jpg, .png
name: توضیحات خاص
ovId: 2840
RESPONSE: {
"status": 200,
"family": "SUCCESSFUL",
"reason": "OK",
"data": {
"id": 2841,
"officialViolationsEntity": {
"id": 2840,
"issueTracking": "2038",
"fullName": "مرتضي بيات",
"fatherName": "محمد علي",
"nationalCode": "4284422642",
"reportSubject": "ترس",
"provinceId": {
"id": "14",
"title": "شهرستانهاي استان تهران",
"status": "1",
"statusStDate": "19970321"
},
"violationPlace": "ناکجا اباد",
"violationTime": 1687638600000,
"referReason": "ندام کاري",
"trackingMobileNumber": "09190000000",
"reportDescriptions": "منسند دارموو ت و نداري",
"status": "0",
"viewedUser": null,
"viewedTime": null,
"expertDescriptions": null,
"haveDocuments": "1",
"createdBy": "4284422642",
"creationTime": 1687678156852,
"lastModifiedBy": null,
"lastModificationTime": null
},
"documentFile": "Base64 document content",
"documentType": ".png",
"documentName": "توضیحات خاص",
"createdBy": "4284422642",
"creationTime": 1687678759325,
"lastModifiedBy": null,
"lastModificationTime": null
}
}
 */

/** NOTE It is doing -> getAllDocumentOV()
GET: https://ov.tamin.ir/api/report-documents/get-all/2840?page=1&start=0&limit=10&filter=%5B%5D&sort=%5B%5D
RESPONSE: Like (https://ov.tamin.ir/api/report-documents/uploa) response
ACTIONS:
    Download  : GET    -> https://ov.tamin.ir/api/report-documents/download-file/{2841}
                RESPONSE: documents content
    Delete    : DELETE -> https://ov.tamin.ir/api/report-documents/delete/{2841}
                RESPONSE :{"status": 200,"family": "SUCCESSFUL","reason": "OK","data": null}

*/