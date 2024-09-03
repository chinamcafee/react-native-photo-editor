package tech.wenchuan.rnphotoeditor.editor.bean

import android.graphics.Bitmap

class StickerAttrs(
    var bitmap: Bitmap,
    var description: String = "",
    var rotation: Float = 0f,
    var scale: Float = 1f,
    var translateX: Float = 0f,
    var translateY: Float = 0f,
)