package com.tamin.taminhamrah

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun openUrl(url: String) {
    // Note: This requires a way to get Context. In Koin, we can inject it.
    // For simplicity here, we'll use a placeholder or handle it in MainActivity.
}
