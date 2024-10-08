package tech.wenchuan.rnphotoeditor.interfaces

import tech.wenchuan.rnphotoeditor.entity.LocalMedia

/**
 * @author：luck
 * @date：2020-01-14 17:08
 * @describe：Filter Listener
 */
interface OnQueryFilterListener {
    /**
     * Filter out multimedia data that does not comply with rules
     * @param media media information
     * @return true filtering
     */
    fun onFilter(media: LocalMedia): Boolean
}