package tech.wenchuan.rnphotoeditor.engine

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import tech.wenchuan.rnphotoeditor.entity.LocalMedia


/**
 * @author：luck
 * @date：2020-01-14 17:08
 * @describe：Media Data Crop Engine
 */

interface CropEngine {
    /**
     * Custom crop image engine
     *
     * The following parameter settings are supported by default
     * [LocalMedia.cropPath] Cropped Path
     * [LocalMedia.cropWidth]#[LocalMedia.cropHeight] Cropped width and height
     * [LocalMedia.cropOffsetX]#[LocalMedia.cropOffsetY]
     * [LocalMedia.cropAspectRatio]
     *
     * If there are more parameters, please use the [LocalMedia.customizeExtra]
     */
    fun onCrop(fragment: Fragment, @NonNull dataSource: MutableList<LocalMedia>, requestCode: Int)
}