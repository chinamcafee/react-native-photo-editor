package tech.wenchuan.rnphotoeditor.app

import tech.wenchuan.rnphotoeditor.engine.ImageEngine

/**
 * @author：luck
 * @date：2020/4/22 11:36 AM
 * @describe：SelectorEngine
 */
interface SelectorEngine {
    /**
     * Create ImageLoad Engine
     */
    fun createImageLoaderEngine(): ImageEngine
}