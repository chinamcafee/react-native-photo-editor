package tech.wenchuan.rnphotoeditor.interfaces

import android.content.Context
import tech.wenchuan.rnphotoeditor.entity.LocalMedia

/**
 * @author：luck
 * @date：2021/11/27 5:44 下午
 * @describe：OnExternalPreviewListener
 */
interface OnExternalPreviewListener {
    /**
     * Delete current preview data
     */
    fun onDelete(context: Context, position: Int, media: LocalMedia)

    /**
     * Long press to download
     */
    fun onLongPressDownload(context: Context, media: LocalMedia): Boolean
}