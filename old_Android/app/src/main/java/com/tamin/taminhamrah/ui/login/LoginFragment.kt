package com.tamin.taminhamrah.ui.login


import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Base64
import android.view.View
import android.webkit.CookieManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.farsitel.bazaar.IUpdateCheckService
import com.google.gson.Gson
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.BuildConfig
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.Constants.CAFFE_BAZAAR
import com.tamin.taminhamrah.Constants.CaffeBazaarPackageName
import com.tamin.taminhamrah.Constants.DIRECT
import com.tamin.taminhamrah.Constants.MYKET
import com.tamin.taminhamrah.Constants.MyketPackageName
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.CheckUpdateData
import com.tamin.taminhamrah.data.remote.models.services.CheckUpdateResponse
import com.tamin.taminhamrah.data.remote.models.user.DeviceDetailModel
import com.tamin.taminhamrah.data.remote.models.user.LoginResponse
import com.tamin.taminhamrah.databinding.FragmentLoginBinding
import com.tamin.taminhamrah.ui.MainActivity
import com.tamin.taminhamrah.ui.NavigatorAdapter
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.base.CustomTabsLauncher
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.contracts.EditContractFragment
import com.tamin.taminhamrah.ui.login.usermode.UserModeDialogFragment
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.Utility.standardWebFlags
import com.tamin.taminhamrah.utils.auth.Preconditions
import com.tamin.taminhamrah.utils.biometric.BiometricHelper
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.isBiometricAvailable
import com.tamin.taminhamrah.utils.updater.AppUpdaterDialog
import com.tamin.taminhamrah.utils.updater.interfaces.CancelUpdateListener
import dagger.hilt.android.AndroidEntryPoint
import saman.zamani.persiandate.PersianDate
import timber.log.Timber
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.concurrent.Executor
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(),
    DialogResultInterface.OnResultListener<MenuModel>,
    AdapterInterface.OnItemClickListener<MenuModel> {

    override val mViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var webLauncher: CustomTabsLauncher

    lateinit var listAdapter: NavigatorAdapter

    @Inject
    lateinit var biometricHelper: BiometricHelper

    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_login

    override fun setupObserver() {
        Timber.tag("loginRepository").e("setupObserver:  CALLED")
        mViewModel.mldCheckUpdate.observe(this, ::checkUpdate)
        mViewModel.mldLoginRes.observe(this, ::showLoginRes)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (requireActivity().intent?.data?.scheme == "mytamin"
            && requireActivity().intent?.data?.host == "login"
        ) {

            val code = requireActivity().intent.data?.getQueryParameter("code") ?: ""
            val codeVerifier = mViewModel.getCodeVerifier()

            Timber.tag("login").i("code::$code")
            Timber.tag("login").i("codeVerifier::$codeVerifier")

            if (code != "" && codeVerifier != "") {
                mViewModel.signIn(codeFromServer = code, codeVerifier!!)
            }
        }
    }

    override fun initView() {
        webLauncher.registerLifecycle(viewLifecycleOwner)
        viewDataBinding?.apply {
            Timber.tag("DeviceManagerKeyGuard").i("getNationalCode: ${mViewModel.getNationalId()}")

            if (Utility.isRooted()) {
                val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                dialog.arguments = createBundle(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.root_error)
                )
                dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")
                dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                    override fun onConfirmClick() {
                        requireActivity().finish()
                    }

                    override fun onCancelClick() {
                    }
                })
            }

            listAdapter = NavigatorAdapter()
            viewDataBinding?.recycler?.apply {
                adapter = listAdapter
                if (itemDecorationCount == 0)
                    addItemDecoration(UiUtils.createDivider(requireContext()))
            }
            listAdapter.setItems(mViewModel.getLoginMenuList(), this@LoginFragment)

        }
    }

    override fun getData() {

    }

    private fun showPrivacyDialog() {
        PrivacyInfoFragment.getInstance()
            .show(childFragmentManager, PrivacyInfoFragment::class.java.simpleName)
    }

    override fun onItemClick(item: MenuModel, transitionView: View?, tag: String?) {
        when (item.id) {
            "1" -> {
                handlePageDestination(R.id.action_frag_login_to_frag_eligibilityTreatment)
            }

            "2" -> {
                webLauncher.launchUrl(Constants.LINK_1420) { intent ->
                    intent.standardWebFlags()
                }
            }

            "3" -> {
                showPrivacyDialog()
            }
            "4"->{
                handlePageDestination(R.id.action_login_to_rules)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onClick() {
        viewDataBinding?.apply {

            btnLogin.setOnClickListener {

                if (!mViewModel.mldLoginRes.value?.accessToken.isNullOrBlank()) {
                    showLoginRes(mViewModel.mldLoginRes.value)
                } else {
                    mViewModel.logOut()
                    clearWebViewCookies()

                    val codeVerifier = generateRandomCodeVerifier()
                    mViewModel.setCodeVerifier(codeVerifier)

                    val url = "${Constants.BASE_URL_ACCOUNT}server/authorize?" +
                            "redirect_uri=mytamin://login" +
                            "&response_type=code" +
                            "&client_id=${Constants.CLIENT_ID}" +
                            "&code_challenge=" + deriveCodeVerifierChallenge(codeVerifier!!) +
                            "&code_challenge_method=" + getCodeVerifierChallengeMethod()

                    webLauncher.launchUrl(url) { intent ->
                        intent.standardWebFlags()
                    }
                }
            }
        }
    }

    private fun clearWebViewCookies() {
        // Clear all cookies by removing all sessions
        CookieManager.getInstance().removeAllCookies(null)
    }

    fun showUserInfoDialog() {
        val dialog = UserModeDialogFragment()
        val bundle = Bundle()
        bundle.putString(
            UserModeDialogFragment.ARG_TEMP_TOKEN,
            mViewModel.mldLoginRes.value?.accessToken
        )
        bundle.putBoolean(UserModeDialogFragment.ARG_ENABLE_LOG_OUT_BUTTON, false)
        /* bundle.putString(
             UserModeDialogFragment.ARG_USER_NATIONAL_CODE,
             viewDataBinding?.widgetNationalCode?.getValueNationalCode()
         )*/
        dialog.arguments = bundle
        dialog.setResultListener(this)
        dialog.show(childFragmentManager, "")
    }

    private fun showLoginRes(result: LoginResponse?) {

        if (result?.isSuccess == true) {
            //clear code verifier
            mViewModel.setCodeVerifier("")
            //     AppController.codeVerifier=null


            Timber.tag("loginRepository").i(" accessToken =%s", result.accessToken)
            mViewModel.checkUpdate(result.accessToken)

            /*            if (isAppInstalled(CaffeBazaarPackageName) && BuildConfig.FLAVOR == CAFFE_BAZAAR)
                            connectService(CaffeBazaarPackageName)
                        else if(isAppInstalled(MyketPackageName) && BuildConfig.FLAVOR == MYKET)
                            connectService(MyketPackageName)*/

            if (BuildConfig.FLAVOR == CAFFE_BAZAAR)
                connectService(CaffeBazaarPackageName)
            else if (BuildConfig.FLAVOR == MYKET)
                connectService(MyketPackageName)
        }
    }

    private fun checkUpdate(result: CheckUpdateResponse) {
        if (result.isSuccess) {
            if (result.data?.getUpdateStatus() != CheckUpdateData.UpdateStatus.NO_UPDATE) {
                if ((updateAvailableInCafeBazaarStore && BuildConfig.FLAVOR == CAFFE_BAZAAR) || BuildConfig.FLAVOR == DIRECT || (updateAvailableInMyKetStore && BuildConfig.FLAVOR == MYKET))
                    AppUpdaterDialog.getInstance(
                        isForce = result.data?.getUpdateStatus() == CheckUpdateData.UpdateStatus.FORCE_UPDATE,
                        updateInfo = result.data?.getChangeList() ?: listOf(""),
                        cafeBazaarEnable = updateAvailableInCafeBazaarStore && BuildConfig.FLAVOR != DIRECT && BuildConfig.FLAVOR != MYKET,
                        myKetEnable = updateAvailableInMyKetStore && BuildConfig.FLAVOR != DIRECT && BuildConfig.FLAVOR != CAFFE_BAZAAR,
                        directLink = result.data?.updateLink
                            ?: "https://ssodcfs.tamin.ir/Eservices/mytaminhamrah.apk",
                        cancelUpdateListener = object : CancelUpdateListener {
                            override fun onCancelUpdateClickListener() {
                                showUserInfoDialog()
                            }
                        }
                    ).show(childFragmentManager, "TAG")
                else
                    showUserInfoDialog()


            } else
                showUserInfoDialog()
        } else
            showUserInfoDialog()

    }

    private fun connectService(packageName: String) {
        connection = UpdateServiceConnection()
        connection?.apply {
            val intent =
                Intent("${packageName}.service.UpdateCheckService.BIND").setPackage(packageName)
            requireActivity().bindService(
                intent,
                this,
                Context.BIND_AUTO_CREATE
            )
        }

    }


    companion object {
        var updateAvailableInCafeBazaarStore = true
        var updateAvailableInMyKetStore = true
    }

    private var connection: UpdateServiceConnection? = null

    class UpdateServiceConnection : ServiceConnection {

        var service: IUpdateCheckService? = null

        override fun onBindingDied(name: ComponentName?) {
            super.onBindingDied(name)
        }

        override fun onNullBinding(name: ComponentName?) {
            super.onNullBinding(name)
        }

        override fun onServiceConnected(name: ComponentName, boundService: IBinder) {
            service = IUpdateCheckService.Stub.asInterface(boundService)
            try {
                val vCodeFromBazaar = service?.getVersionCode(BuildConfig.APPLICATION_ID) ?: -1

                val currentVersion = BuildConfig.VERSION_CODE
                Timber.tag("UpdateCheck").d("$vCodeFromBazaar::$currentVersion")

                if (vCodeFromBazaar > 0) {
                    updateAvailableInCafeBazaarStore = true
                    updateAvailableInMyKetStore = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Timber.tag("UpdateCheck").d("onServiceConnected(): Connected")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            service = null
            Timber.tag("UpdateCheck").d("onServiceDisconnected(): Disconnected")
        }
    }

    override fun onDialogResult(userMode: MenuModel) {
        //  mViewModel.mldLoginInfo.value?.data?.apply {
        viewDataBinding?.apply {
            if (mViewModel.mldLoginRes.value != null) {
                mViewModel.mldLoginRes.value?.let { res ->
                    mViewModel.saveLoginInfo(
                        res.accessToken,
                        (System.currentTimeMillis() / 1000) + res.expiresIn,
                        res.refreshToken
                    )
                }
                val today = PersianDate()
                val deviceDetailModel = DeviceDetailModel(
                    Settings.Secure.getString(
                        requireActivity().contentResolver,
                        Settings.Secure.ANDROID_ID
                    ),
                    serial = Build.SERIAL,
                    model = Build.MODEL,
                    manufacture = Build.MANUFACTURER,
                    sdk = Build.VERSION.SDK_INT,
                    lastLoginDate = today.toString(),
                    lastLoginTimeStamp = today.time,
                    lastUserType = userMode.title ?: "Unknown",
                    versionName = BuildConfig.VERSION_NAME,
                    versionCode = BuildConfig.VERSION_CODE,
                    flavor = BuildConfig.FLAVOR,
                    fcmToken = mViewModel.getFcmToken(),
                    lastLoginId = userMode.description2 ?: "Unknown"
                )

                Timber.tag("deviceDataModel").i(Gson().toJson(deviceDetailModel))
                mViewModel.postDeviceDataModel(deviceDetailModel)
                if (requireContext().isBiometricAvailable()) {
                    val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                    dialog.arguments = createBundle(
                        MessageOfRequestDialogFragment.MessageType.WITHOUT_TITLE,
                        getString(R.string.biometric_dialog_desc),
                        btnCancel=true
                    )
                    dialog.setDialogClickListener(object :
                        DialogClickInterface.onClickListener {
                        override fun onConfirmClick() {
                            showBiometricPrompt()
                        }

                        override fun onCancelClick() {
                            mViewModel.setBiometricEnabled(false)
                            handlePageDestination(
                                R.id.action_user_mode_to_home,
                                finishActivity = true
                            )
                            dialog.dismiss()
                        }
                    }
                    )
                    dialog.show(childFragmentManager, "Biometric")
                } else {
                    mViewModel.setBiometricEnabled(false)
                    handlePageDestination(
                        R.id.action_user_mode_to_home,
                        finishActivity = true
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        connection?.let { requireActivity().unbindService(connection!!) }
        connection = null
        super.onDestroy()
    }

    /**
     * The default entropy (in bytes) used for the code verifier.
     */
    private val defaultCodeVerifierEntropy = 64

    /**
     * The minimum permitted entropy (in bytes) for use with
     * [.generateRandomCodeVerifier].
     */
    private val minCodeVerifierEntropy = 32


    /**
     * The maximum permitted entropy (in bytes) for use with
     * [.generateRandomCodeVerifier].
     */
    private val maxCodeVerifierEntropy = 96

    /**
     * Base64 encoding settings used for generated code verifiers.
     */
    private val codeBase64EncodeSettings = Base64.NO_WRAP or Base64.NO_PADDING or Base64.URL_SAFE


    /**
     * SHA-256 based code verifier challenge method.
     *
     * @see "Proof Key for Code Exchange by OAuth Public Clients
     */
    private val codeChallengeMethodS256 = "S256"

    /**
     * Plain-text code verifier challenge method. This is only used by AppAuth for Android if
     * SHA-256 is not supported on this platform.
     *
     * @see "Proof Key for Code Exchange by OAuth Public Clients
     */
    private val codeChallengeMethodPlain = "plain"


    /**
     * Generates a random code verifier string using the provided entropy source and the specified
     * number of bytes of entropy.
     */
    private fun generateRandomCodeVerifier(): String? {
        val entropySource = SecureRandom()
        val entropyBytes = defaultCodeVerifierEntropy

        Preconditions.checkNotNull(entropySource, "entropySource cannot be null")
        Preconditions.checkArgument(
            minCodeVerifierEntropy <= entropyBytes,
            "entropyBytes is less than the minimum permitted"
        )
        Preconditions.checkArgument(
            entropyBytes <= maxCodeVerifierEntropy,
            "entropyBytes is greater than the maximum permitted"
        )
        val randomBytes = ByteArray(entropyBytes)
        entropySource.nextBytes(randomBytes)
        return Base64.encodeToString(randomBytes, codeBase64EncodeSettings)

    }


    /**
     * Produces a challenge from a code verifier, using SHA-256 as the challenge method if the
     * system supports it (all Android devices _should_ support SHA-256), and falls back
     * to the [&quot;plain&quot; challenge type][AuthorizationRequest.CODE_CHALLENGE_METHOD_PLAIN] if
     * unavailable.
     */
    private fun deriveCodeVerifierChallenge(codeVerifier: String): String? {
        return try {
            val sha256Digester = MessageDigest.getInstance("SHA-256")
            sha256Digester.update(codeVerifier.toByteArray(charset("ISO_8859_1")))
            val digestBytes = sha256Digester.digest()
            Base64.encodeToString(digestBytes, codeBase64EncodeSettings)
        } catch (e: NoSuchAlgorithmException) {
            //Logger.warn("SHA-256 is not supported on this device! Using plain challenge", e);
            codeVerifier
        } catch (e: UnsupportedEncodingException) {
            // Logger.error("ISO-8859-1 encoding not supported on this device!", e);
            throw IllegalStateException("ISO-8859-1 encoding not supported", e)
        }
    }

    /**
     * Returns the challenge method utilized on this system: typically
     * [SHA-256][AuthorizationRequest.CODE_CHALLENGE_METHOD_S256] if supported by
     * the system, [plain][AuthorizationRequest.CODE_CHALLENGE_METHOD_PLAIN] otherwise.
     */
    private fun getCodeVerifierChallengeMethod(): String? {
        return try {
            MessageDigest.getInstance("SHA-256")
            // no exception, so SHA-256 is supported
            codeChallengeMethodS256
        } catch (e: NoSuchAlgorithmException) {
            codeChallengeMethodPlain
        }
    }

    private fun showBiometricPrompt() {
        biometricHelper.authenticate(
            fragment = this,
            onSuccess = {
                mViewModel.setBiometricEnabled(true)
                handlePageDestination(
                    R.id.action_user_mode_to_home,
                    finishActivity = true
                )
            },
            onError = { _, _ ->
                mViewModel.setBiometricEnabled(false)
                handlePageDestination(
                    R.id.action_user_mode_to_home,
                    finishActivity = true
                )
            }
        )
    }
}