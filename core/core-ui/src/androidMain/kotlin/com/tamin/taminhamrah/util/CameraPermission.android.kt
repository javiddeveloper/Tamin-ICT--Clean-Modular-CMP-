package com.tamin.taminhamrah.util


import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun rememberCameraPermission(): CameraPermission {
    val context = LocalContext.current

    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var pendingCallback by remember {
        mutableStateOf<((Boolean) -> Unit)?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        granted = isGranted
        pendingCallback?.invoke(isGranted)
        pendingCallback = null
    }

    // Re-check permission whenever this composable enters composition.
    DisposableEffect(Unit) {
        granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        onDispose {
            pendingCallback = null
        }
    }

    return remember(launcher) {
        object : CameraPermission {

            override val granted: Boolean
                get() = granted

            override fun request(onResult: (Boolean) -> Unit) {
                if (granted) {
                    onResult(true)
                    return
                }

                pendingCallback = onResult
                launcher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}
