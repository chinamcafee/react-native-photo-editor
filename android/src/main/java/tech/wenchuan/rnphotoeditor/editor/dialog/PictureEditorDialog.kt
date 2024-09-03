package tech.wenchuan.rnphotoeditor.editor.dialog


//import tech.wenchuan.rnphotoeditor.selector.bean.MediaBean
//import tech.wenchuan.rnphotoeditor.selector.dialog.PictureSelectorCallback
//import tech.wenchuan.rnphotoeditor.selector.dialog.PictureSelectorDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import coil.dispose
import coil.load
import tech.wenchuan.rnphotoeditor.R
import tech.wenchuan.rnphotoeditor.base.dialog.FullDialog
import tech.wenchuan.rnphotoeditor.clip.dialog.PictureClipCallback
import tech.wenchuan.rnphotoeditor.clip.dialog.PictureClipDialog
import tech.wenchuan.rnphotoeditor.databinding.PictureEditorDialogBinding
import tech.wenchuan.rnphotoeditor.editor.bean.StickerAttrs
import tech.wenchuan.rnphotoeditor.editor.utils.ColorUtils
import tech.wenchuan.rnphotoeditor.editor.view.PictureEditorView
import tech.wenchuan.rnphotoeditor.editor.view.layer.OnStickerClickListener
import tech.wenchuan.rnphotoeditor.utils.saveImagesToAlbum

class PictureEditorDialog : FullDialog() {

    companion object {
        @JvmStatic
        fun newInstance(): PictureEditorDialog {
            return PictureEditorDialog()
        }
    }

    private var _binding: PictureEditorDialogBinding? = null
    private val binding get() = _binding!!
    private var _callback: PictureEditorCallback? = null
    private val colors: MutableList<RelativeLayout> = arrayListOf()
    private val tools: MutableList<ImageView> = arrayListOf()
    private var bitmapPath: String? = null
    private var bitmapUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PictureEditorDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.apply {
            setDimAmount(1f)
        }
        colors.add(binding.white)
        colors.add(binding.black)
        colors.add(binding.red)
        colors.add(binding.yellow)
        colors.add(binding.green)
        colors.add(binding.blue)
        colors.add(binding.purple)
        tools.add(binding.graffiti)
//        tools.add(binding.sticker)
        tools.add(binding.text)
        tools.add(binding.screenshot)
        tools.add(binding.mosaic)
        binding.back.setOnClickListener { dismiss() }
        binding.complete.setOnClickListener {
            if (binding.complete.isEnabled) {
                binding.complete.isEnabled = false
                binding.progress.visibility = View.VISIBLE
                binding.progress.load(R.drawable.ps_ic_progress)
                Toast.makeText(it.context, "保存中，请稍候...", Toast.LENGTH_SHORT).show()
                it.context.saveImagesToAlbum(binding.picEditor.saveBitmap()) { path, uri ->
                    binding.complete.isEnabled = true
                    binding.progress.visibility = View.GONE
                    binding.progress.dispose()
                    _callback?.onFinish(path, uri)
                    dismiss()
                }
            }
        }
        bitmapPath?.let {
            binding.picEditor.setBitmapPathOrUri(it, null)
        }
        bitmapUri?.let {
            binding.picEditor.setBitmapPathOrUri(null, it)
        }
        binding.colorUndo.setOnClickListener { binding.picEditor.graffitiUndo() }
        binding.mosaicUndo.setOnClickListener { binding.picEditor.mosaicUndo() }
        colors.forEachIndexed { index, color ->
            color.setOnClickListener {
                selectedColor(color)
                binding.picEditor.setGraffitiColor(ColorUtils.colorful[index])
            }
        }
        tools.forEachIndexed { index, tool ->
            tool.setOnClickListener {
                binding.colorBar.visibility = View.GONE
                binding.mosaicUndo.visibility = View.GONE
                binding.picEditor.setMode(PictureEditorView.Mode.STICKER)
                if (!tool.isSelected) {
                    selectedTool(tool)
                    when (index) {
                        0 -> {
                            binding.colorBar.visibility = View.VISIBLE
                            binding.picEditor.setMode(PictureEditorView.Mode.GRAFFITI)
                        }

//                        1 -> {
////                            openAlbum()
//                            tool.isSelected = false
//                        }

                        1 -> {
                            openTextDialog()
                            tool.isSelected = false
                        }

                        2 -> {
                            openClipDialog(binding.picEditor.saveBitmap())
                            tool.isSelected = false
                        }

                        3 -> {
                            binding.mosaicUndo.visibility = View.VISIBLE
                            binding.picEditor.setMode(PictureEditorView.Mode.MOSAIC)
                        }
                    }
                } else {
                    tool.isSelected = false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _callback = null
        _binding = null
    }

    fun setBitmapPathOrUri(path: String?, uri: Uri?): PictureEditorDialog {
        this.bitmapPath = path
        this.bitmapUri = uri
        return this
    }

    fun setPictureEditorCallback(callback: PictureEditorCallback): PictureEditorDialog {
        this._callback = callback
        return this
    }

//    private fun openAlbum() {
//        PictureSelectorDialog.newInstance()
//            .setPictureSelectorCallback(object : PictureSelectorCallback {
//                override fun onSelectedData(data: List<MediaBean>) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                        requireActivity().getBitmapFromUri(data.first().uri, 200)?.let { bitmap ->
//                            binding.picEditor.setSticker(StickerAttrs(bitmap))
//                        }
//                    } else {
//                        requireActivity().getBitmapFromPath(
//                            requireActivity().getBitmapPathFromUri(data.first().uri), 200
//                        )?.let { bitmap ->
//                            binding.picEditor.setSticker(StickerAttrs(bitmap))
//                        }
//                    }
//                }
//            }).show(childFragmentManager)
//    }

    private fun openTextDialog(attrs: StickerAttrs? = null) {
        PictureTextDialog.newInstance()
            .setStickerAttrs(attrs)
            .setPictureTextCallback(object : PictureTextCallback {
                override fun onFinish(attrs: StickerAttrs) {
                    binding.picEditor.setSticker(attrs, object : OnStickerClickListener {
                        override fun onClick(attrs: StickerAttrs) {
                            openTextDialog(attrs)
                        }
                    })
                }
            })
            .show(manager)
    }

    private fun openClipDialog(bitmap: Bitmap) {
        PictureClipDialog.newInstance()
            .setBitmapResource(bitmap)
            .setPictureClipCallback(object : PictureClipCallback {
                override fun onFinish(path: String, uri: Uri) {
                    _callback?.onFinish(path, uri)
                    dismiss()
                }
            })
            .show(manager)
    }

    private fun selectedColor(view: View) {
        colors.forEach {
            it.isSelected = false
        }
        view.isSelected = true
    }

    private fun selectedTool(view: View) {
        tools.forEach {
            it.isSelected = false
        }
        view.isSelected = true
    }

}

interface PictureEditorCallback {
    fun onFinish(path: String, uri: Uri)
}