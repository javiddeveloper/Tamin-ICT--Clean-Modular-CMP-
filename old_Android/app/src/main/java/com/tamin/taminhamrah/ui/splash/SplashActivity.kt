package com.tamin.taminhamrah.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import com.tamin.taminhamrah.databinding.ActivitySplashBinding
import com.tamin.taminhamrah.ui.base.ContainerBaseActivity
import dagger.hilt.android.AndroidEntryPoint


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ContainerBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    /*   private fun isDeviceSecure(): Boolean? {
           val keyguardManager :KeyguardManager? =  getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
           return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
               keyguardManager?.isDeviceSecure
           } else {
               keyguardManager?.isKeyguardSecure
           }
       }

       @TargetApi(Build.VERSION_CODES.M)
       private fun setSystemScreenLock() {
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
               val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
               if (keyguardManager.isDeviceSecure) {
                   BiometricUtil.showUnlockDialog(
                       null,
                       this,
                       true,
                       ID_ENABLE_SYSTEM_LOCK,
                       PreferenceService.LockingMech_SYSTEM
                   )
               } else {
                   val snackbar: Snackbar =
                       Snackbar.make(fragmentView, R.string.no_lockscreen_set, Snackbar.LENGTH_LONG)
                   snackbar.setAction(R.string.configure, object : OnClickListener() {
                       fun onClick(v: View?) {
                           startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                       }
                   })
                   snackbar.show()
               }
           }
       }*/
}