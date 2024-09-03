package tech.wenchuan.rnphotoeditor.interfaces

import androidx.recyclerview.widget.RecyclerView
import tech.wenchuan.rnphotoeditor.animators.BaseAnimationAdapter

/**
 * @author：luck
 * @date：2021/12/1 8:48 下午
 * @describe：Media List Animation wrap
 */
interface OnAnimationAdapterWrapListener {
    /**
     * extends [BaseAnimationAdapter] for implementing adapter list animation
     */
    fun wrap(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>): BaseAnimationAdapter?
}