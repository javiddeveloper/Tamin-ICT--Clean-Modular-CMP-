package com.tamin.taminhamrah.util

import android.content.Context
import android.content.pm.PackageManager
import com.tamin.taminhamrah.core.domain.BuildConfig
import org.koin.core.context.GlobalContext

actual object AppConfig {
    actual val isDebug: Boolean = BuildConfig.DEBUG

    actual val versionName: String
        get() {
            val context = GlobalContext.get().get<Context>()
            return try {
                val packageInfo = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    context.packageManager.getPackageInfo(
                        context.packageName,
                        PackageManager.PackageInfoFlags.of(0)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    context.packageManager.getPackageInfo(context.packageName, 0)
                }
                packageInfo.versionName ?: "0.0.0"
            } catch (_: Exception) {
                "0.0.0"
            }
        }
}
