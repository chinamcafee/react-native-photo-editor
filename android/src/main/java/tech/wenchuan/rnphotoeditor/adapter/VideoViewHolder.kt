package tech.wenchuan.rnphotoeditor.adapter

import android.view.View
import android.widget.TextView
import tech.wenchuan.rnphotoeditor.R
import tech.wenchuan.rnphotoeditor.entity.LocalMedia
import tech.wenchuan.rnphotoeditor.utils.DateUtils

/**
 * @author：luck
 * @date：2022/11/30 3:32 下午
 * @describe：VideoViewHolder
 */
open class VideoViewHolder(itemView: View) : ListMediaViewHolder(itemView) {
    private var tvDuration: TextView = itemView.findViewById(R.id.tv_duration)

    override fun bindData(media: LocalMedia, position: Int) {
        super.bindData(media, position)
        tvDuration.text = media.duration.let { DateUtils.formatDurationTime(it) }
    }
}