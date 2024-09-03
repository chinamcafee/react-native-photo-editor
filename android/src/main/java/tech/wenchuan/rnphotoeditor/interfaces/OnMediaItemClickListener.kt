package tech.wenchuan.rnphotoeditor.interfaces

import android.view.View
import tech.wenchuan.rnphotoeditor.entity.LocalMedia

/**
 * @author：luck
 * @date：2023/1/13 11:40 上午
 * @describe：OnMediaItemClickListener
 */
interface OnMediaItemClickListener {
    fun openCamera()

    fun onItemClick(selectedView: View, position: Int, media: LocalMedia)

    fun onItemLongClick(itemView: View, position: Int, media: LocalMedia)

    fun onSelected(isSelected: Boolean, position: Int, media: LocalMedia): Int

    fun onComplete(isSelected: Boolean, position: Int, media: LocalMedia)
}