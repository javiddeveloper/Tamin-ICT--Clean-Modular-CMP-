package com.tamin.taminhamrah.ui.dialog

import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.openAppSettings

class PermissionMessageDialog : DialogManagerMessageOfRequest() {
    fun showPermissionDialog(
        isPermanentlyDeclined: Boolean,
        permissionTextProvider: PermissionTextProvider,
        onCancelClicked: () -> Unit,
        onOkClicked: () -> Unit
    ) {
        createDialog().apply {
            arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.INFO,
                desc = permissionTextProvider.getDescription(isPermanentlyDeclined),
                btnCancel = isPermanentlyDeclined
            )
            setDialogClickListener(object : DialogClickInterface.onClickListener {
                override fun onConfirmClick() {
                    if (isPermanentlyDeclined) {
                        requireActivity().openAppSettings()
                    }
                    onOkClicked()
                }

                override fun onCancelClick() {
                    dismiss()
                    onCancelClicked()
                }

            })
        }

    }
}

interface PermissionTextProvider {
    fun getDescription(
        isPermanentlyDeclined: Boolean
    ): String
}

class CameraPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "به نظر می آید که شما به طور دائمی اجازه ی دسترسی به دوربین را رد کرده اید. می توانید به تنظیمات اپلیکیشن بروید تا آن را اعطا کنید"
        } else {
            "این اپلیکیشن نیاز به دسترسی به دوربین دارد تا عملکرد گرفتن تصویر را انجام دهد"
        }
    }
}

class StoragePermissionGetImageTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "به نظر می آید که شما به طور دائمی اجازه ی دسترسی به حافظه را رد کرده اید. می توانید به تنظیمات اپلیکیشن بروید تا آن را اعطا کنید"
        } else {
            "این اپلیکیشن نیاز به دسترسی به حافظه دارد تا عملکرد دریافت تصاویر را انجام دهد"
        }
    }
}
    class MicrophonePermissionGetImageTextProvider : PermissionTextProvider {
        override fun getDescription(isPermanentlyDeclined: Boolean): String {
            return if (isPermanentlyDeclined) {
                "به نظر می آید که شما به طور دائمی اجازه ی دسترسی به میکروفون را رد کرده اید. می توانید به تنظیمات اپلیکیشن بروید تا آن را اعطا کنید"
            } else {
                "این اپلیکیشن نیاز به دسترسی به میکروفون دارد تا عملکرد ضبط صدا را انجام دهد"
            }
        }
    }
    class StoragePermissionGetAppTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "به نظر می آید که شما به طور دائمی اجازه ی دسترسی به حافظه را رد کرده اید. می توانید به تنظیمات اپلیکیشن بروید تا آن را اعطا کنید"
        } else {
            "این اپلیکیشن نیاز به دسترسی به حافظه دارد تا عملکرد دریافت اپلیکیشن را انجام دهد"
        }
    }
}