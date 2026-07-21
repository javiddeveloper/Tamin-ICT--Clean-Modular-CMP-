
import android.net.Uri
import android.os.Parcelable
import androidx.exifinterface.media.ExifInterface
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContentAttachmentData(
        val size: Long = 0,
        val duration: Long? = 0,
        val date: Long = 0,
        val height: Long? = 0,
        val width: Long? = 0,
        val exifOrientation: Int = ExifInterface.ORIENTATION_UNDEFINED,
        val name: String? = null,
        val queryUri: Uri,
        val mimeType: String?
) : Parcelable
