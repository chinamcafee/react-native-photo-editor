package tech.wenchuan.rnphotoeditor

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tech.wenchuan.rnphotoeditor.base.BaseSelectorFragment
import tech.wenchuan.rnphotoeditor.config.MediaType
import tech.wenchuan.rnphotoeditor.config.SelectionMode
import tech.wenchuan.rnphotoeditor.constant.SelectedState
import tech.wenchuan.rnphotoeditor.constant.SelectorConstant
import tech.wenchuan.rnphotoeditor.interfaces.OnRequestPermissionListener
import tech.wenchuan.rnphotoeditor.permissions.OnPermissionResultListener
import tech.wenchuan.rnphotoeditor.permissions.PermissionChecker
import tech.wenchuan.rnphotoeditor.permissions.PermissionChecker.isCheckReadStorage
import tech.wenchuan.rnphotoeditor.provider.TempDataProvider
import tech.wenchuan.rnphotoeditor.utils.MediaUtils
import tech.wenchuan.rnphotoeditor.utils.SelectorLogUtils
import tech.wenchuan.rnphotoeditor.utils.ToastUtils

/**
 * @author：luck
 * @date：2022/1/16 10:22 下午
 * @describe：Using the systems built-in image library
 */
/**
 * System all image or video album
 */
const val SYSTEM_ALL = "image/*,video/*"

/**
 * System image album
 */
const val SYSTEM_IMAGE = "image/*"

/**
 * System video album
 */
const val SYSTEM_VIDEO = "video/*"

/**
 * System audio album
 */
const val SYSTEM_AUDIO = "audio/*"

open class SelectorSystemFragment : BaseSelectorFragment() {

    override fun getFragmentTag(): String {
        return SelectorSystemFragment::class.java.simpleName
    }

    override fun getResourceId(): Int {
        return R.layout.ps_empty
    }

    private var mDocMultipleLauncher: ActivityResultLauncher<String>? = null

    private var mDocSingleLauncher: ActivityResultLauncher<String>? = null

    private var mContentsLauncher: ActivityResultLauncher<String>? = null

    private var mContentLauncher: ActivityResultLauncher<String>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createSystemContracts()
        if (isCheckReadStorage(requireContext(), config.mediaType)) {
            openSystemAlbum()
        } else {
            val permissionArray = PermissionChecker.getReadPermissionArray(
                requireContext(),
                config.mediaType
            )
            showPermissionDescription(true, permissionArray)
            val onPermissionApplyListener = config.mListenerInfo.onPermissionApplyListener
            if (onPermissionApplyListener != null) {
                showCustomPermissionApply(permissionArray)
            } else {
                PermissionChecker.requestPermissions(this, permissionArray,
                    object : OnPermissionResultListener {
                        override fun onGranted() {
                            showPermissionDescription(false, permissionArray)
                            openSystemAlbum()
                        }

                        override fun onDenied() {
                            handlePermissionDenied(permissionArray)
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
                        openSystemAlbum()
                    } else {
                        handlePermissionDenied(permission)
                    }
                }
            })
    }

    /**
     * 打开系统相册
     */
    open fun openSystemAlbum() {
        if (config.selectionMode == SelectionMode.SINGLE || config.selectionMode == SelectionMode.ONLY_SINGLE) {
            if (config.mediaType == MediaType.ALL) {
                mDocSingleLauncher?.launch(SYSTEM_ALL)
            } else {
                getInput()?.let { mContentLauncher?.launch(it) }
            }
        } else {
            if (config.mediaType == MediaType.ALL) {
                mDocMultipleLauncher?.launch(SYSTEM_ALL)
            } else {
                getInput()?.let { mContentsLauncher?.launch(it) }
            }
        }
    }

    open fun getInput(): String? {
        return when (config.mediaType) {
            MediaType.VIDEO -> {
                SYSTEM_AUDIO
                SYSTEM_VIDEO
            }
            MediaType.AUDIO -> {
                SYSTEM_AUDIO
            }
            else -> {
                SYSTEM_IMAGE
            }
        }
    }

    /**
     * create System Contracts
     */
    open fun createSystemContracts() {
        val selectionMode = config.selectionMode
        if (selectionMode == SelectionMode.MULTIPLE) {
            if (config.mediaType == MediaType.ALL) {
                createMultipleDocuments()
            } else {
                createMultipleContents()
            }
        } else {
            if (config.mediaType == MediaType.ALL) {
                createSingleDocuments()
            } else {
                createContent()
            }
        }
    }

    /**
     * 同时获取图片或视频(多选)
     *
     * 部分机型可能不支持多选操作
     */
    open fun createMultipleDocuments() {
        mDocMultipleLauncher =
            registerForActivityResult(object : ActivityResultContract<String, List<Uri>>() {
                override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
                    val result = mutableListOf<Uri>()
                    intent ?: return result
                    val clipData = intent.clipData
                    if (clipData != null) {
                        for (i in 0 until clipData.itemCount) {
                            result.add(clipData.getItemAt(i).uri)
                        }
                    } else {
                        intent.data?.let {
                            result.add(it)
                        }
                    }
                    return result
                }

                override fun createIntent(context: Context, input: String): Intent {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    intent.type = input
                    return intent
                }
            }) { result ->
                if (result.isEmpty()) {
                    onBackPressed()
                } else {
                    viewModel.viewModelScope.launch {
                        val context = requireContext()
                        result.forEach { uri ->
                            MediaUtils.getPath(context, uri)?.let { absolutePath ->
                                val media = MediaUtils.getAssignPathMedia(context, absolutePath)
                                if (media != null) {
                                    confirmSelect(media, false)
                                } else{
                                    SelectorLogUtils.info("createMultipleDocuments: Parsing LocalMedia object as empty")
                                }
                            }
                        }
                        handleSelectResult()
                    }
                }
            }
    }

    /**
     * 获取图片或视频
     *
     * 部分机型可能不支持多选操作
     */
    open fun createMultipleContents() {
        mContentsLauncher =
            registerForActivityResult(object : ActivityResultContract<String, List<Uri>>() {
                override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
                    val result = mutableListOf<Uri>()
                    if (intent == null) {
                        return result
                    }
                    val clipData = intent.clipData
                    if (clipData != null) {
                        for (i in 0 until clipData.itemCount) {
                            result.add(clipData.getItemAt(i).uri)
                        }
                    } else {
                        intent.data?.let { uri ->
                            result.add(uri)
                        }
                    }
                    return result
                }

                override fun createIntent(context: Context, input: String): Intent {
                    val intent: Intent =
                        when {
                            TextUtils.equals(SYSTEM_VIDEO, input) -> {
                                Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                                )
                            }
                            TextUtils.equals(SYSTEM_AUDIO, input) -> {
                                Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                                )
                            }
                            else -> {
                                Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                )
                            }
                        }
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    return intent
                }
            }) { result ->
                if (result.isEmpty()) {
                    onBackPressed()
                } else {
                    viewModel.viewModelScope.launch {
                        val context = requireContext()
                        result.forEach { uri ->
                            MediaUtils.getPath(context, uri)?.let { absolutePath ->
                                val media = MediaUtils.getAssignPathMedia(context, absolutePath)
                                if (media != null) {
                                    confirmSelect(media, false)
                                } else{
                                    SelectorLogUtils.info("createMultipleContents: Parsing LocalMedia object as empty")
                                }
                            }
                        }
                        handleSelectResult()
                    }
                }
            }
    }


    /**
     * 同时获取图片或视频(单选)
     */
    open fun createSingleDocuments() {
        mDocSingleLauncher =
            registerForActivityResult(object : ActivityResultContract<String, Uri>() {
                override fun parseResult(resultCode: Int, intent: Intent?): Uri {
                    return intent?.data!!
                }

                override fun createIntent(context: Context, input: String): Intent {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = input
                    return intent
                }
            }) { result ->
                if (result == null) {
                    onBackPressed()
                } else {
                    viewModel.viewModelScope.launch {
                        MediaUtils.getPath(requireContext(), result)?.let { absolutePath ->
                            val media =
                                MediaUtils.getAssignPathMedia(requireContext(), absolutePath)
                            if (media == null) {
                                onBackPressed()
                                SelectorLogUtils.info("createSingleDocuments: Parsing LocalMedia object as empty")
                            } else {
                                if (confirmSelect(media, false) == SelectedState.SUCCESS) {
                                    handleSelectResult()
                                } else {
                                    onBackPressed()
                                }
                            }
                        }
                    }
                }
            }
    }


    /**
     * 单选图片或视频
     */
    open fun createContent() {
        mContentLauncher =
            registerForActivityResult(object : ActivityResultContract<String, Uri>() {
                override fun parseResult(resultCode: Int, intent: Intent?): Uri {
                    return intent?.data!!
                }

                override fun createIntent(context: Context, input: String): Intent {
                    val intent: Intent =
                        when {
                            TextUtils.equals(SYSTEM_VIDEO, input) -> {
                                Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                                )
                            }
                            TextUtils.equals(SYSTEM_AUDIO, input) -> {
                                Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                                )
                            }
                            else -> {
                                Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                )
                            }
                        }
                    return intent
                }
            }) { result ->
                if (result == null) {
                    onBackPressed()
                } else {
                    viewModel.viewModelScope.launch {
                        MediaUtils.getPath(requireContext(), result)?.let { absolutePath ->
                            val media =
                                MediaUtils.getAssignPathMedia(requireContext(), absolutePath)
                            if (media == null) {
                                onBackPressed()
                                SelectorLogUtils.info("createContent: Parsing LocalMedia object as empty")
                            } else {
                                if (confirmSelect(media, false) == SelectedState.SUCCESS) {
                                    handleSelectResult()
                                } else {
                                    onBackPressed()
                                }
                            }
                        }
                    }
                }
            }
    }

    override fun onResultCanceled(requestCode: Int, resultCode: Int) {
        if (requestCode == SelectorConstant.REQUEST_GO_SETTING) {
            handlePermissionSettingResult(TempDataProvider.getInstance().currentRequestPermission)
        } else {
            onBackPressed()
        }
    }

    override fun handlePermissionSettingResult(permission: Array<String>) {
        if (permission.isEmpty()) {
            return
        }
        showPermissionDescription(false, permission)
        val onPermissionApplyListener = config.mListenerInfo.onPermissionApplyListener
        val isHasPermissions = onPermissionApplyListener?.hasPermissions(this, permission)
            ?: PermissionChecker.checkSelfPermission(requireContext(), permission)
        if (isHasPermissions) {
            openSystemAlbum()
        } else {
            ToastUtils.showMsg(requireContext(), getString(R.string.ps_jurisdiction))
            onBackPressed()
        }
        TempDataProvider.getInstance().currentRequestPermission = arrayOf()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDocMultipleLauncher?.unregister()
        mDocSingleLauncher?.unregister()
        mContentsLauncher?.unregister()
        mContentLauncher?.unregister()
    }
}