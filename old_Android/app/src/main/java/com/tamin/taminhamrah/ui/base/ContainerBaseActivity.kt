package com.tamin.taminhamrah.ui.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.gson.Gson
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import com.tamin.taminhamrah.enums.EnumUserMode
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import timber.log.Timber


abstract class ContainerBaseActivity : AppCompatActivity() {


    //  lateinit var baseActivityBinding: ActivityBaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        PreferenceManager(this, Gson()).getUserMode()?.let { setCurrentTheme(it) }
        super.onCreate(savedInstanceState)
    }

    fun showAlertDialog(
        type: MessageOfRequestDialogFragment.MessageType,
        desc: String,
        dialogClickListener: DialogClickInterface.onClickListener? = null
    ) {
        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = createBundle(type, desc)
        if (dialogClickListener != null)
            dialog.setDialogClickListener(dialogClickListener)
        dialog.show(supportFragmentManager, "Alert Dialog MessageOfRequest")
    }

    fun createBundle(
        type: MessageOfRequestDialogFragment.MessageType,
        desc: String,
        btnCancel: Boolean = false,
        titleConfirm: String? = null,
        titleId: Int? = 0,
        dismissType: MessageOfRequestDialogFragment.DismissType? = MessageOfRequestDialogFragment.DismissType.NORMAL
    ): Bundle {
        val bundle = Bundle()
        bundle.putSerializable(Constants.DIALOG_MESSAGE_TYPE, type)
        bundle.putString(Constants.DIALOG_DESC, desc)
        bundle.putBoolean(Constants.CANCEL_BUTTON, btnCancel)
        bundle.putString(Constants.TITLE_CONFIRM_BUTTON, titleConfirm)
        bundle.putSerializable(Constants.DISMISS_TYPE_DIALOG, dismissType)
        titleId?.let { bundle.putInt(Constants.DIALOG_TITLE, it) }
        return bundle
    }

    /*override fun attachBaseContext(context: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context))
    }*/

    fun setCurrentTheme(userMode: String) {
        when (userMode) {
            EnumUserMode.MODE_EMPLOYER.methodName -> {
                setTheme(R.style.AppTheme_EmployerTheme)
            }

            EnumUserMode.MODE_INSURED.methodName -> {
                setTheme(R.style.AppTheme_InsuredTheme)
            }
            EnumUserMode.MODE_PENSIONER.methodName -> {
                setTheme(R.style.AppTheme_InsuredTheme)
            }
        }
    }


    private fun hideSystemUI(view: View) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, view).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    /* private fun hideSystemUI() {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
             window.setDecorFitsSystemWindows(false)
             window.insetsController?.let {
                 it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                 it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
             }
         } else {
             @Suppress("DEPRECATION")
             window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                     or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                     or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                     or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                     // Hide the nav bar and status bar
                     or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                     or View.SYSTEM_UI_FLAG_FULLSCREEN)
         }
     }*/

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).show(WindowInsetsCompat.Type.systemBars())
    }
    open fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
        val win: Window = activity.window
        val winParams: WindowManager.LayoutParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    /* fun showLoading() {
         baseActivityBinding.loadingView.parent.visibility = View.VISIBLE
         window.setFlags(
             WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
             WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
         );
     }

     fun hideLoading() {
         baseActivityBinding.loadingView.parent.visibility = View.GONE
         window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
     }*/

    private var loadingView: View? = null


    override fun onBackPressed() {
        Timber.tag("loadingTest").i("onBackPressed:")
        hideLoading()
        super.onBackPressed()
    }

    fun hideLoading() {
        Timber.tag("loadingTest").i("hideLoading:  Called\n************************************")
        loadingView?.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
    fun showLoading(parent: ViewGroup?) {
        if (loadingView == null) {
            try {
                loadingView = LayoutInflater.from(this).inflate(
                    R.layout.view_loading,
                    null
                )
                loadingView?.apply {
                    id = View.generateViewId()
                    translationZ = resources.getDimension(R.dimen.button_pressed_z_material)

                    layoutParams = ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    parent?.addView(loadingView, 0)
                    setOnClickListener { }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: Error) {
                e.printStackTrace()
            }
        } else if (loadingView?.visibility == View.GONE) {
            loadingView?.visibility = View.VISIBLE
        }

        /* window.setFlags(
             WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
             WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
         )*/


    }

}

