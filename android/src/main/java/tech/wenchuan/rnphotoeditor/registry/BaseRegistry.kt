package tech.wenchuan.rnphotoeditor.registry

import androidx.annotation.NonNull
import tech.wenchuan.rnphotoeditor.adapter.MediaPreviewAdapter
import tech.wenchuan.rnphotoeditor.adapter.base.BaseListViewHolder
import tech.wenchuan.rnphotoeditor.adapter.base.BaseMediaListAdapter
import tech.wenchuan.rnphotoeditor.adapter.base.BasePreviewMediaHolder
import tech.wenchuan.rnphotoeditor.base.BaseSelectorFragment
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author：luck
 * @date：2021/11/19 10:02 下午
 * @describe：BaseRegistry
 */
abstract class BaseRegistry {
    var transcoders = CopyOnWriteArrayList<Entry<*>>()
    abstract fun <Model> register(@NonNull targetClass: Class<Model>)
    abstract fun <Model> unregister(@NonNull targetClass: Class<Model>)
    abstract fun <Model> get(@NonNull targetClass: Class<Model>): Class<Model>
    class Entry<Model> constructor(var fromClass: Class<Model>) {
        fun handles(fromClass: Class<*>): Boolean {
            return fromClass.isAssignableFrom(this.fromClass)
        }
    }

    fun <V> isAssignableFromCapture(targetClass: Class<V>): Boolean {
        return VideoCaptureComponent::class.java.isAssignableFrom(targetClass)
                || ImageCaptureComponent::class.java.isAssignableFrom(targetClass)
                || SoundCaptureComponent::class.java.isAssignableFrom(targetClass)
    }

    fun <V> isAssignableFromHolder(targetClass: Class<V>): Boolean {
        return BaseListViewHolder::class.java.isAssignableFrom(targetClass)
                || BasePreviewMediaHolder::class.java.isAssignableFrom(targetClass)
    }

    fun <V> isAssignableFromFragment(targetClass: Class<V>): Boolean {
        return BaseSelectorFragment::class.java.isAssignableFrom(targetClass)
    }

    fun <A> isAssignableFromAdapter(targetClass: Class<A>): Boolean {
        return BaseMediaListAdapter::class.java.isAssignableFrom(targetClass)
                || MediaPreviewAdapter::class.java.isAssignableFrom(targetClass)
    }

    open fun clear() {
        if (transcoders.isNotEmpty()) {
            transcoders.clear()
        }
    }
}