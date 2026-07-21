package com.tamin.taminhamrah.utils.updater

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.tamin.taminhamrah.BuildConfig
import com.tamin.taminhamrah.Constants.CAFFE_BAZAAR
import com.tamin.taminhamrah.Constants.CaffeBazaarPackageName
import com.tamin.taminhamrah.Constants.DIRECT
import com.tamin.taminhamrah.Constants.MYKET
import com.tamin.taminhamrah.Constants.MyketPackageName
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.FragmentAppUpdaterDialogBinding
import com.tamin.taminhamrah.ui.dialog.PermissionMessageDialog
import com.tamin.taminhamrah.ui.dialog.StoragePermissionGetImageTextProvider
import com.tamin.taminhamrah.utils.updater.directlink.DirectLinkDownload
import com.tamin.taminhamrah.utils.updater.interfaces.CancelUpdateListener
import com.tamin.taminhamrah.utils.updater.pojo.Store
import com.tamin.taminhamrah.utils.updater.pojo.UpdaterFragmentModel
import com.tamin.taminhamrah.utils.updater.pojo.UpdaterStoreList
import com.tamin.taminhamrah.utils.updater.stores.CafeBazaarStore
import com.tamin.taminhamrah.utils.updater.stores.GooglePlayStore
import com.tamin.taminhamrah.utils.updater.stores.IranAppsStore
import com.tamin.taminhamrah.utils.updater.stores.MyketStore
import com.tamin.taminhamrah.utils.updater.utils.DATA_LIST
import com.tamin.taminhamrah.utils.updater.utils.typeface
import timber.log.Timber
import kotlin.system.exitProcess


class AppUpdaterDialog : DialogFragment() {
    lateinit var binding: FragmentAppUpdaterDialogBinding
    private var isDark  = false
    private var directLink:String=""
    var cancelUpdateListener: CancelUpdateListener? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // setting isCancelable
         isDark = context?.let {
            (it.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        }?:false

        // Set background for the dialog
        dialog?.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.dialog_background
            )
        )
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding =
            FragmentAppUpdaterDialogBinding.inflate(LayoutInflater.from(context), container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    override fun onStart() {
        super.onStart()

        // make dialog's width matchParent
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }



    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            try {
                val permissionDialog = PermissionMessageDialog()
                permissions.entries.forEach { perm ->
                    when (perm.key) {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE ->{
                            if (!perm.value) {
                                if (VERSION.SDK_INT >= VERSION_CODES.M) {
                                 //   dismiss()
                                    permissionDialog.showPermissionDialog(
                                        isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                            perm.key
                                        ),
                                        permissionTextProvider = StoragePermissionGetImageTextProvider(),
                                        onCancelClicked = {},
                                        onOkClicked = {},
                                    )
                                    permissionDialog.createDialog().show(childFragmentManager,"permissionDialog")
                                } else {
                                //   dismiss()
                                }
                            } else {
                                downloadApp()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.tag("permissionLauncher").v("permissionLauncher exception = " + e)
            }
        }


    var cancelableMode = false
    private fun getData() {
        val data = arguments?.getSerializable(DATA_LIST) as UpdaterFragmentModel
        val title = data.title
        val cafeBazaarEnable = data.cafeBazaarEnable ?: false
        val myKetEnable = data.myKetEnable ?: false
        directLink = data.directLink
        binding.apply {
            context?.let {
                if (isDark) {
                    bazaarDownload.setColorFilter(
                        ContextCompat.getColor(
                            it,
                            R.color.white
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            }
            bazaarDownload.isVisible = (cafeBazaarEnable && BuildConfig.FLAVOR == CAFFE_BAZAAR)
            myKetDownload.isVisible = (myKetEnable && BuildConfig.FLAVOR == MYKET)
            directLayout.isVisible = (BuildConfig.FLAVOR == DIRECT)

            cancelableMode = !(data.isForceUpdate ?: false)
            setDialogCancelable()
            updateTitle.text = title


            recyclerUpdateInfo.adapter =
                UpdateInfoRecyclerAdapter(data.updateInfo)

            directLayout.setOnClickListener {
              if (VERSION.SDK_INT == VERSION_CODES.TIRAMISU){
                  downloadApp()
              }else{
                  permissionLauncher.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
              }
            }

            bazaarDownload.setOnClickListener {
                if (!safeClick) {
                    context?.apply {

                        if (isAppInstalled(CaffeBazaarPackageName)) {
                            safeClick = true
                            val appUpdateUrl = "bazaar://details?id="
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(appUpdateUrl + BuildConfig.APPLICATION_ID)
                            intent.setPackage(CaffeBazaarPackageName)
                            try {
                                context?.startActivity(intent)
                            } catch (e: ActivityNotFoundException) {

                            }
                            Handler(Looper.getMainLooper()).postDelayed(
                                Runnable { safeClick = false },
                                1500
                            )
                        } else {
                            // Myket is not installed
                            val webUpdateUrl = "https://cafebazaar.ir/app/"
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(webUpdateUrl + BuildConfig.APPLICATION_ID)
                            )
                            startActivity(intent)
                        }

                    }

                }
            }

            myKetDownload.setOnClickListener {
                if (!safeClick) {
                    context?.apply {
                        if (isAppInstalled(MyketPackageName)) {
                            safeClick = true
                            val appUpdateUrl = "myket://details?id="
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(appUpdateUrl + BuildConfig.APPLICATION_ID)
                            intent.setPackage(MyketPackageName)
                            try {
                                context?.startActivity(intent)
                            } catch (e: ActivityNotFoundException) {

                            }
                            Handler(Looper.getMainLooper()).postDelayed(
                                Runnable { safeClick = false },
                                1500
                            )
                        } else {
                            // Myket is not installed
                            val webUpdateUrl = "https://myket.ir/app/"
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(webUpdateUrl + BuildConfig.APPLICATION_ID)
                            )
                            startActivity(intent)
                        }

                    }
                }
            }
        }
    }

    private fun downloadApp() {
        if (!safeClick && directLink.isNotBlank()) {
            safeClick = true
            DirectLinkDownload().getApk(directLink, activity, childFragmentManager)
            Handler(Looper.getMainLooper()).postDelayed(
                Runnable { safeClick = false },
                1500
            )
        }
    }

    private fun setDialogCancelable() {

        isCancelable = cancelableMode
        if (cancelableMode)
            binding.btnCancelUpdate.text = getString(R.string.label_cancel_update)
        else
            binding.btnCancelUpdate.text = getString(R.string.label_drawer_desc_logout)


        binding.btnCancelUpdate.setOnClickListener {
            if (cancelableMode)
                dismiss()
            else
                exitProcess(0)
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        cancelUpdateListener?.onCancelUpdateClickListener()
        super.onDismiss(dialog)
    }


    var safeClick = false
    private fun onListListener(item: UpdaterStoreList) {
        if (!safeClick) {
            safeClick = true
            when (item.store) {
                Store.DIRECT_URL ->
                    DirectLinkDownload().getApk(item.url, activity, childFragmentManager)

                Store.GOOGLE_PLAY ->
                    GooglePlayStore().setStoreData(context, item)

                Store.CAFE_BAZAAR ->
                    CafeBazaarStore().setStoreData(context, item)

                Store.MYKET ->
                    MyketStore().setStoreData(context, item)

                Store.IRAN_APPS ->
                    IranAppsStore().setStoreData(context, item)
            }
            Handler(Looper.getMainLooper()).postDelayed(Runnable { safeClick = false }, 2000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        typeface = null
    }

    companion object {


        /**
         * get Instance method
         */
        fun getInstance(
            title: String = "نسخه جدید اپلیکیشن با امکانات زیر ارایه گردیده است",
            updateInfo: List<String> = listOf(
                "بهبود رابط کاربری",
                "سرویس جدید قرارداد بیمه دانشجویان",
                "سرویس جدید نسخه الکترونیک",
                "بهبود رابط کاربری",
                "سرویس جدید قرارداد بیمه دانشجویان",
                "سرویس جدید نسخه الکترونیک",
                "بهبود رابط کاربری",
                "سرویس جدید قرارداد بیمه دانشجویان",
                "سرویس جدید نسخه الکترونیک"
            ),
            isForce: Boolean = false,
            cafeBazaarEnable: Boolean = false,
            myKetEnable: Boolean = false,
            directLink: String,
            cancelUpdateListener: CancelUpdateListener? = null
        ): AppUpdaterDialog {

            // set typeface in utils class to use later in application
            val fragment = AppUpdaterDialog()

            // bundle to add data to our dialog
            val bundle = Bundle()
            val data =
                UpdaterFragmentModel(title, updateInfo, isForce, cafeBazaarEnable,myKetEnable, directLink)
            bundle.putSerializable(DATA_LIST, data)
            fragment.arguments = bundle
            fragment.cancelUpdateListener = cancelUpdateListener
            return fragment
        }
    }

    fun isAppInstalled(uri: String): Boolean {
        val pm: PackageManager = requireActivity().packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return false
    }

}