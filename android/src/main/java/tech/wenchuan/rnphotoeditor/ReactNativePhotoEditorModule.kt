package tech.wenchuan.rnphotoeditor

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alibaba.fastjson2.JSONObject
import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import tech.wenchuan.rnphotoeditor.config.MediaType
import tech.wenchuan.rnphotoeditor.editor.dialog.PictureEditorCallback
import tech.wenchuan.rnphotoeditor.editor.dialog.PictureEditorDialog
import tech.wenchuan.rnphotoeditor.engine.GlideEngine
import tech.wenchuan.rnphotoeditor.entity.LocalMedia
import tech.wenchuan.rnphotoeditor.interfaces.OnEditorMediaListener
import tech.wenchuan.rnphotoeditor.interfaces.OnResultCallbackListener
import tech.wenchuan.rnphotoeditor.model.PictureSelector
import tech.wenchuan.rnphotoeditor.utils.FileUtils.stringToUri

class ReactNativePhotoEditorModule : Module() {
    // Each module class must implement the definition function. The definition consists of components
    // that describes the module's functionality and behavior.
    // See https://docs.expo.dev/modules/module-api for more details about available components.
    override fun definition() = ModuleDefinition {
        // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
        // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
        // The module will be accessible from `requireNativeModule('ReactNativePhotoEditor')` in JavaScript.
        Name("ReactNativePhotoEditor")

        // Sets constant properties on the module. Can take a dictionary or a closure that returns a dictionary.
        Constants(
            "PI" to Math.PI
        )

        // Defines event names that the module can send to JavaScript.
        Events("onChange")

        // Defines a JavaScript synchronous function that runs the native code on the JavaScript thread.
        Function("hello") {
            "Hello world! Link-U👋"
        }

        // Defines a JavaScript function that always returns a Promise and whose native code
        // is by default dispatched on the different thread than the JavaScript runtime runs on.
        AsyncFunction("setValueAsync") { value: String ->
            // Send an event to JavaScript.
            sendEvent(
                "onChange", mapOf(
                    "value" to value
                )
            )
        }

        AsyncFunction("openEditor") {path:String, promise: Promise ->
            try {
                val activity = appContext.currentActivity
                val context = appContext.reactContext!!

                if (activity is AppCompatActivity) {
                    println("Go to openEditor ===============")

                    PictureEditorDialog.newInstance()
                        .setBitmapPathOrUri(
                            path, null
                        )
                        .setPictureEditorCallback(object : PictureEditorCallback {
                            override fun onFinish(path: String, uri: Uri) {
//                                        previewAdapter.getItem(binding.viewpager2.currentItem).uri = uri
//                                        previewAdapter.notifyItemChanged(binding.viewpager2.currentItem)
                               promise.resolve(path)
                            }
                        })
                        .show(activity)
                    println("End to openEditor ===============")
                }

            } catch (e: Exception) {
                // 发生错误时，拒绝promise
                promise.reject("ERROR_CODE", "An error occurred", e)
            }
        }

        AsyncFunction("openPicker")
        { promise: Promise ->
            try {
                val activity = appContext.currentActivity
                val context = appContext.reactContext!!

                if (activity is AppCompatActivity) {
                    println("Go to edit photo ===============")

                    println("Go to edit photo end ===============")
                    PictureSelector.create(activity)
                        .openGallery(MediaType.IMAGE)
                        .setImageEngine(GlideEngine.create())
                        .isOriginalControl(true)
                        .setMaxSelectNum(9)
                        .isNewNumTemplate(true)
                        .setOnEditorMediaListener(object : OnEditorMediaListener {
                            override fun onEditorMedia(
                                fragment: Fragment,
                                media: LocalMedia,
                                requestCode: Int
                            ) {
                                println("Go to edit photo uri 123===============" + media.absolutePath)
                                val currentUri =
                                    media.absolutePath?.let { stringToUri(context, it) }
                                PictureEditorDialog.newInstance()
                                    .setBitmapPathOrUri(
                                        null, currentUri
                                    )
                                    .setPictureEditorCallback(object : PictureEditorCallback {
                                        override fun onFinish(path: String, uri: Uri) {
//                                        previewAdapter.getItem(binding.viewpager2.currentItem).uri = uri
//                                        previewAdapter.notifyItemChanged(binding.viewpager2.currentItem)
                                            println("Finish edit photo ===============$path")
                                        }
                                    })
                                    .show(context)
                                println("Go to edit photo end ===============")
                            }

                        })
                        .forResult(object : OnResultCallbackListener {
                            override fun onResult(result: List<LocalMedia>) {
                                println("Photo result=====" + JSONObject.toJSONString(result))
                                promise.resolve("success")
                            }

                            override fun onCancel() {}
                        })

                }

            } catch (e: Exception) {
                // 发生错误时，拒绝promise
                promise.reject("ERROR_CODE", "An error occurred", e)
            }
        }

        // Enables the module to be used as a native view. Definition components that are accepted as part of
        // the view definition: Prop, Events.
        View(ReactNativePhotoEditorView::class) {
            // Defines a setter for the `name` prop.
            Prop("name") { view: ReactNativePhotoEditorView, prop: String ->
                println(prop)
            }
        }
    }
}
