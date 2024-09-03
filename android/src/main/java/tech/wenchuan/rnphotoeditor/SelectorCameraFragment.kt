package tech.wenchuan.rnphotoeditor

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.View
import tech.wenchuan.rnphotoeditor.base.BaseSelectorFragment
import tech.wenchuan.rnphotoeditor.constant.SelectedState
import tech.wenchuan.rnphotoeditor.constant.SelectorConstant
import tech.wenchuan.rnphotoeditor.entity.LocalMedia
import tech.wenchuan.rnphotoeditor.interfaces.OnRequestPermissionListener
import tech.wenchuan.rnphotoeditor.permissions.OnPermissionResultListener
import tech.wenchuan.rnphotoeditor.permissions.PermissionChecker
import tech.wenchuan.rnphotoeditor.provider.TempDataProvider
import tech.wenchuan.rnphotoeditor.utils.SdkVersionUtils.isQ
import tech.wenchuan.rnphotoeditor.utils.SelectorLogUtils
import tech.wenchuan.rnphotoeditor.utils.ToastUtils

/**
 * @author：luck
 * @date：2021/11/22 2:26 下午
 * @describe：SelectorCameraFragment
 */
open class SelectorCameraFragment : BaseSelectorFragment() {

    override fun getFragmentTag(): String {
        return SelectorCameraFragment::class.java.simpleName
    }

    override fun getResourceId(): Int {
        return R.layout.ps_empty
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            if (isQ()) {
                openSelectedCamera()
            } else {
                val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                PermissionChecker.requestPermissions(
                    this, permission,
                    object : OnPermissionResultListener {
                        override fun onGranted() {
                            showPermissionDescription(false, permission)
                            openSelectedCamera()
                        }

                        override fun onDenied() {
                            handlePermissionDenied(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        }
                    })
            }
        }
    }

    override fun showCustomPermissionApply(permission: Array<String>) {
        config.mListenerInfo.onPermissionApplyListener?.requestPermission(
            this,
            permission,
            object :
                OnRequestPermissionListener {
                override fun onCall(permission: Array<String>, isResult: Boolean) {
                    if (isResult) {
                        showPermissionDescription(false, permission)
                        openSelectedCamera()
                    } else {
                        handlePermissionDenied(permission)
                    }
                }
            })
    }

    override fun onResultCanceled(requestCode: Int, resultCode: Int) {
        if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == SelectorConstant.REQUEST_GO_SETTING) {
                handlePermissionSettingResult(TempDataProvider.getInstance().currentRequestPermission)
            } else {
                onBackPressed()
            }
        }
    }

    override fun onMergeCameraResult(media: LocalMedia?) {
        if (media != null && confirmSelect(media, false) == SelectedState.SUCCESS) {
            handleSelectResult()
        } else {
            onBackPressed()
            SelectorLogUtils.info("only camera analysisCameraData: Parsing LocalMedia object as empty")
        }
    }

    override fun handlePermissionSettingResult(permission: Array<String>) {
        if (permission.isEmpty()) {
            return
        }
        val context = requireContext()
        showPermissionDescription(false, permission)
        var isHasPermissions: Boolean
        val onPermissionApplyListener = config.mListenerInfo.onPermissionApplyListener
        if (onPermissionApplyListener != null) {
            isHasPermissions = onPermissionApplyListener.hasPermissions(this, permission)
        } else {
            isHasPermissions =
                PermissionChecker.checkSelfPermission(context, arrayOf(Manifest.permission.CAMERA))
            if (isQ()) {
            } else {
                isHasPermissions = PermissionChecker.checkSelfPermission(
                    context,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                )
            }
        }
        if (isHasPermissions) {
            openSelectedCamera()
        } else {
            if (!PermissionChecker.checkSelfPermission(
                    context,
                    arrayOf(Manifest.permission.CAMERA)
                )
            ) {
                ToastUtils.showMsg(context, getString(R.string.ps_camera))
            } else {
                if (!PermissionChecker.checkSelfPermission(
                        context,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    )
                ) {
                    ToastUtils.showMsg(requireContext(), getString(R.string.ps_jurisdiction))
                }
            }
            onBackPressed()
        }
        TempDataProvider.getInstance().currentRequestPermission = arrayOf()
    }
}