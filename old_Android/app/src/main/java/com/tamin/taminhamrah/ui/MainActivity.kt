package com.tamin.taminhamrah.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.google.android.material.color.DynamicColors
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.local.models.ApplicationThemeEnum
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.databinding.ActivityMainBinding
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.CustomTabsLauncher
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.login.LoginActivity
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.Utility.standardWebFlags
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val baseViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var webLauncher: CustomTabsLauncher
    private var loadingView: View? = null

    lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var isDark = false

    fun closeDrawer() {
        binding.mainDrawerLayout.closeDrawer(Gravity.RIGHT)
    }

    var doubleCheckExit = false

    fun openDrawer() {
        binding.mainDrawerLayout.openDrawer(Gravity.RIGHT)
    }

    fun redirectToServicesFragment() {
        binding.mainBottomNavigationView.selectedItemId = R.id.navigation_home
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.tag("AI_DEBUG").d("MainActivity created")
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        webLauncher.registerLifecycle(this)

        val map = HashMap<String, String>()
        map["CLASS"] = this.javaClass.simpleName
        map["METHOD"] = "onCreate"
        map["condition"] = " MAIN ACTIVITY OPEN SUCCESSFULLY"
        //   TaminLogger.putLog(map)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        isDark = baseViewModel.getApplicationTheme() == ApplicationThemeEnum.NIGHT.state
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.mainDrawerLayout.isDrawerOpen(Gravity.RIGHT))
                    closeDrawer()
                else {
                    if (navController.currentDestination?.id == R.id.servicesFragment) {

                        if (!doubleCheckExit) {
                            Toast.makeText(
                                this@MainActivity,
                                getString(R.string.double_click_exit_message),
                                Toast.LENGTH_SHORT
                            ).show()

                            doubleCheckExit = true
                            Handler(Looper.getMainLooper()).postDelayed(
                                { doubleCheckExit = false },
                                1500
                            )
                        } else
                            finish()
                    } else if (
                        navController.currentDestination?.id == R.id.myTaminFragment ||
                        navController.currentDestination?.id == R.id.treatmentFragment ||
                        navController.currentDestination?.id == R.id.profileFragment
                    ) {

                        binding.mainBottomNavigationView.selectedItemId = R.id.navigation_home

                    } else
                        navController.popBackStack()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this@MainActivity, onBackPressedCallback)


        navController = findNavController(R.id.nav_host_fragment) //Initialising navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.aiFragment) {
                binding.mainBottomNavigationView.animate()
                    .alpha(0f)
                    .translationY(binding.mainBottomNavigationView.height.toFloat())
                    .setDuration(250)
                    .withEndAction {
                        if (navController.currentDestination?.id == R.id.aiFragment) {
                            binding.mainBottomNavigationView.visibility = View.GONE
                        }
                    }
                    .start()
            } else {
                if (binding.mainBottomNavigationView.visibility != View.VISIBLE) {
                    binding.mainBottomNavigationView.visibility = View.VISIBLE
                    binding.mainBottomNavigationView.alpha = 0f
                    binding.mainBottomNavigationView.translationY = binding.mainBottomNavigationView.height.toFloat()
                    binding.mainBottomNavigationView.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(250)
                        .start()
                }
            }
        }

        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.servicesFragment,
            R.id.treatmentFragment,
            R.id.myTaminFragment,
            R.id.profileFragment
        ) //Pass the ids of fragments from nav_graph which you don't want to show back button in toolbar
            .setOpenableLayout(binding.mainDrawerLayout) //Pass the drawer layout id from activity xml
            .build()

        binding.mainNavigationView.setupWithNavController(navController) //Setup Drawer navigation with navController
        binding.mainBottomNavigationView.setupWithNavController(navController) //Setup Bottom navigation with navController

        initDrawer()

        binding.mainNavigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navGoTo1420 -> {
                    webLauncher.launchUrl(Constants.LINK_1420) { intent ->
                        intent.standardWebFlags()
                    }
                    closeDrawer()
                    true
                }

                R.id.navShareApp -> {
                    Utility.sendShare(
                        this,
                        getString(R.string.cafe_bazar_share_link),
                        getString(R.string.label_share)
                    )
                    closeDrawer()
                    true
                }

//                R.id.navSocial -> {
//                    navController.navigate(
//                        R.id.socialResponsibilityFragment,
//                        createToolbarBundle(
//                            getString(R.string.social_responsibility),
//                            getString(R.string.label_drawer_desc_social),
//                            R.drawable.ic_social
//                        )
//                    )
//                    true
//                }
                /*         R.id.navRules -> {
                             navController.navigate(
                                 R.id.taminRulesFragment,
                                 createToolbarBundle(
                                     getString(R.string.rules_title),
                                     getString(R.string.tamin_rules),
                                     R.drawable.ic_doc
                                 )
                             )
                             true
                         }*/

                R.id.contactUsFragment -> {
                    navController.navigate(
                        R.id.contactUsFragment,
                        createToolbarBundle(
                            getString(R.string.label_contact_us),
                            getString(R.string.label_drawer_desc_contact_us),
                            R.drawable.ic_phone
                        )
                    )
                    closeDrawer()
                    true
                }

                R.id.versioningFragment -> {
                    navController.navigate(
                        R.id.versioningFragment,
                        createToolbarBundle(
                            getString(R.string.label_drawer_title_versioning),
                            getString(R.string.label_drawer_desc_versioninig),
                            R.drawable.ic_history
                        )
                    )
                    closeDrawer()
                    true

                }

                R.id.loginActivity -> {
                    val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                    dialog.arguments = createBundle(
                        MessageOfRequestDialogFragment.MessageType.WITHOUT_TITLE,
                        getString(R.string.label_are_you_sure_to_exit), btnCancel = true
                    )
                    dialog.setDialogClickListener(object :
                        DialogClickInterface.onClickListener {
                        override fun onConfirmClick() {
                            baseViewModel.logOut()
                            resetView()
                            val url = "${Constants.BASE_URL_ACCOUNT}signout?" +
                                    "redirect_uri=mytamin://logout" +
                                    "&response_type=assertion" +
                                    "&client_id=${Constants.CLIENT_ID}"
                            webLauncher.launchUrl(url) { intent ->
                                intent.standardWebFlags()
                            }

                        }

                        override fun onCancelClick() {
                            dialog.dismiss()
                        }
                    }
                    )
                    dialog.show(supportFragmentManager, "ExitFromApp")
                    closeDrawer()
                    true
                }

                else -> false
            }
        }

        binding.mainBottomNavigationView.setOnItemReselectedListener {
            val allowedDestinations = setOf(
                R.id.servicesFragment,
                R.id.myTaminFragment,
                R.id.treatmentFragment,
                R.id.profileFragment
            )

            Timber.tag("AI_DEBUG").d("BottomNav reselected: currentDestination = ${navController.currentDestination?.label ?: "unknown"}")

            if (navController.currentDestination?.id !in allowedDestinations) {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        baseViewModel.mldCheckSuccessPayment.observe(this) {
//            Timber.i("heckSuccessPayment %s", it.data.toString())
        }

        if (intent?.data?.scheme == "mytamin")
            showCallbackResponse(intent)

    }

    private fun changeAppTheme() {
        if (!isDark) {
            // Dark mode enabled
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            baseViewModel.setApplicationTheme(ApplicationThemeEnum.NIGHT)
        } else {
            // Dark mode disabled
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            baseViewModel.setApplicationTheme(ApplicationThemeEnum.DAY)

        }
        isDark = !isDark
        window.setWindowAnimations(R.style.WindowAnimationTransition)
        recreate()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        //super.onSaveInstanceState(outState, outPersistentState)
    }

    private fun showCallbackResponse(intent: Intent) {
        try {
            Timber.i("DeepLink %s", intent.data?.host)

            when (intent.data?.host) {
                Constants.REDIRECT_HOST_FREELANCE,
                Constants.REDIRECT_HOST_OPTIONAL,
                Constants.REDIRECT_TFH,
                Constants.REDIRECT_HOST_FRACTION,
                Constants.REDIRECT_HOST_EMPLOYER_DEBT -> {
                    baseViewModel.updatePaymentStatus()
                }

                Constants.REDIRECT_WORKERS_PAYMENT -> {
                    baseViewModel.updateWorkersPaymentStatus()
                }

                Constants.REDIRECT_HOST_LOGOUT -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    onBackPressedDispatcher.onBackPressed()
                }
            }

        } catch (ex: Exception) {
            baseViewModel.updatePaymentStatus()
        }


    }

    override fun onNewIntent(intent: Intent) {
        showCallbackResponse(intent)
        super.onNewIntent(intent)
    }

    //bottom nav
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        binding.mainDrawerLayout.apply {
            if (isDrawerOpen(Gravity.RIGHT)) {
                closeDrawer(Gravity.RIGHT)
            } else {
                openDrawer(Gravity.RIGHT)
            }
        }
        return item.onNavDestinationSelected(navController) ||
                super.onOptionsItemSelected(item)
    }

    fun handleResponse(
        result: Resource<Any?>?,
        showError: Boolean = true
    ) {
        when (result?.status) {
            Resource.Status.LOADING -> {
                showLoading(binding.parent)
            }

            Resource.Status.ERROR -> {
                hideLoading()
                if (showError)
                    showError(result.message?.message)

            }

            Resource.Status.NEED_REFRESH_TOKEN -> {
                hideLoading()
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.REFRESHTOKEN,
                    resources.getString(R.string.message_need_to_refresh_token)
                )
            }

            Resource.Status.NEED_NETWORK -> {
                hideLoading()
                result.message?.let {
                    showAlertDialog(MessageOfRequestDialogFragment.MessageType.INFO, it.message)
                }
            }

            Resource.Status.SUCCESS -> {
                hideLoading()
            }

            else ->
                hideLoading()
        }
    }

    private fun showError(message: String?) {
        if (message == "null" || message == null) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                getString(R.string.message_invalide_error_null)
            )
        } else {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                message
            )
        }
    }

    private fun initDrawer() {
        binding.mainNavigationView.getHeaderView(0).apply {
            ImageUtils.loadUserAvatar(
                (findViewById(R.id.imgProfile) as? AppCompatImageView),
                baseViewModel.getUserAvatar()
            )
            baseViewModel.mldProfile.observe(this@MainActivity) {
                (findViewById(R.id.tvUsername) as? AppCompatTextView)?.text = it.data?.fullName
                (findViewById(R.id.tvNationalCode) as? AppCompatTextView)?.text =
                    it.data?.nationalCode
            }

            setOnClickListener {
                binding.mainDrawerLayout.apply {
                    if (isDrawerOpen(Gravity.RIGHT)) {
                        closeDrawer(Gravity.RIGHT)
                    } else {
                        openDrawer(Gravity.RIGHT)
                    }
                }

                navController.navigate(
                    R.id.navigation_profile,
                    Bundle().apply { putBoolean("showBackButton", true) })
            }

            baseViewModel.getProfileInfo()

        }
        binding.mainNavigationView.menu.clear()
        binding.mainNavigationView.inflateMenu(R.menu.drawer_nav_menu)

        setupDrawerMenu(
            R.id.navGoTo1420,
            R.drawable.ic_my_tamin,
            R.string.label_drawer_title_1420,
            R.string.label_drawer_desc_1420
        )
        setupDrawerMenu(
            R.id.navShareApp,
            R.drawable.ic_share,
            R.string.label_share,
            R.string.label_drawer_desc_share
        )
//        setupDrawerMenu(
//            R.id.navSocial,
//            R.drawable.ic_social,
//            R.string.social_responsibility,
//            R.string.label_drawer_desc_social
//        )
        /*     setupDrawerMenu(
                 R.id.navRules,
                 R.drawable.ic_doc,
                 R.string.rules_title,
                 R.string.tamin_rules
             )
     */
        setupDrawerMenu(
            R.id.contactUsFragment,
            R.drawable.ic_phone,
            R.string.label_contact_us,
            R.string.label_drawer_desc_contact_us
        )
        setupDrawerMenu(
            R.id.versioningFragment,
            R.drawable.ic_history,
            R.string.label_drawer_title_versioning,
            R.string.label_drawer_desc_versioninig
        )
        setupDrawerMenu(
            R.id.loginActivity,
            R.drawable.ic_exit_to_app,
            R.string.label_logout,
            subTitleId = null
        )
    }

    private fun setupDrawerMenu(
        actionLayoutId: Int,
        iconId: Int,
        titleId: Int,
        subTitleId: Int? = null
    ) {
        binding.mainNavigationView.menu.apply {
            ((findItem(actionLayoutId).actionView) as? ViewGroup)?.apply {
                (findViewWithTag<View>("main_icon") as? AppCompatImageView)?.setImageResource(iconId)
                (findViewWithTag<View>("title") as? AppCompatTextView)?.text = getString(titleId)
                val subTitleTxt = (findViewWithTag<View>("subTitle") as? AppCompatTextView)

                if (subTitleId != null) {
                    subTitleTxt?.text =
                        getString(subTitleId)
                } else {
                    subTitleTxt?.isGone = true
                }
            }
        }
    }

    fun createToolbarBundle(title: String, description: String, iconRes: Int): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.TOOLBAR_TITLE, title)
        bundle.putString(Constants.TOOLBAR_SUBTITLE, description)
        bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, iconRes)
        return bundle
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

    fun resetView() {
        // Navigate to the login screen or any other initial screen
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        // Finish the current activity to remove it from the back stack
        finish()
    }
}