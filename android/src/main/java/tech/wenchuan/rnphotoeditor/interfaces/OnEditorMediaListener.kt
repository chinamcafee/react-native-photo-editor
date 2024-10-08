package tech.wenchuan.rnphotoeditor.interfaces

import androidx.fragment.app.Fragment
import tech.wenchuan.rnphotoeditor.entity.LocalMedia

/**
 * @author：luck
 * @date：2021/11/27 5:44 下午
 * @describe：OnEditorMediaListener
 */
interface OnEditorMediaListener {
    /**
     * Edit Media Resources
     * [LocalMedia.editorData]
     */
    fun onEditorMedia(fragment: Fragment, media: LocalMedia, requestCode: Int)
}
