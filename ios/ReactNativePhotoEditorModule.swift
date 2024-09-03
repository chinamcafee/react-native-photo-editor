import ExpoModulesCore
import Photos
import UIKit

public class ReactNativePhotoEditorModule: Module {
  // Each module class must implement the definition function. The definition consists of components
  // that describes the module's functionality and behavior.
  // See https://docs.expo.dev/modules/module-api for more details about available components.
  public func definition() -> ModuleDefinition {
    // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
    // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
    // The module will be accessible from `requireNativeModule('ReactNativePhotoEditor')` in JavaScript.
    Name("ReactNativePhotoEditor")

    // Sets constant properties on the module. Can take a dictionary or a closure that returns a dictionary.
    Constants([
      "PI": Double.pi,
    ])

    // Defines event names that the module can send to JavaScript.
    Events("onChange")

    // Defines a JavaScript synchronous function that runs the native code on the JavaScript thread.
    Function("hello") {
      "Hello world! 👋"
    }

    // Defines a JavaScript function that always returns a Promise and whose native code
    // is by default dispatched on the different thread than the JavaScript runtime runs on.
    AsyncFunction("setValueAsync") { (value: String) in
      // Send an event to JavaScript.
      self.sendEvent("onChange", [
        "value": value,
      ])
    }

    AsyncFunction("openPicker") {
      (promise: Promise) in

      DispatchQueue.main.async {
        let config = PickerConfiguration.default
        print("openPicker invoked ==========")
        //            promise.resolve("123123 success")
        //                if let topViewController = self.getTopViewController() {
        //                    if let navigationController = topViewController.navigationController {
        //                        navigationController.pushViewController(, animated: true)
        //
        //                    }
        //
        //                }
        Photo.picker(
          config
        ) { result, _ in
          // 选择完成的回调
          // result 选择结果
          //  .photoAssets 当前选择的数据
          //  .isOriginal 是否选中了原图
          // photoPickerController 对应的照片选择控制器
          print("pick photo success ========================= start")
          let asset = result.photoAssets[0].phAsset!
          self.getImageFromAsset(asset: asset) { filePath in
            if let filePath = filePath {
              print("图片路径: \(filePath)")
              let image = UIImage(contentsOfFile: filePath)!
              let editorAsset = EditorAsset(type: .image(image))
              Photo.edit(asset: editorAsset, config: EditorConfiguration())
            } else {
              print("获取图片路径失败")
            }
          }
          print("Is Original: \(result.isOriginal)")
          print("pick photo success ========================= end")
          promise.resolve("123123 success")
        } cancel: { _ in
          // 取消的回调
          // photoPickerController 对应的照片选择控制器
          // 打印取消文本到控制台
          print("Cancelled selecting photos.")
//                    promise.resolve("123123 cancel")
        }
      }
    }

    AsyncFunction("openEditor") {
      (path: String, promise: Promise) in

      DispatchQueue.main.async {
//                let image = UIImage(contentsOfFile: "/private/var/mobile/Containers/Data/Application/174A1B74-64EF-4890-BCCA-29FAFE35693F/tmp/FF92367F-D84E-4621-B45C-16AFE92B1BA5.jpg")!
//                let editorAsset = EditorAsset.init(type: .image(image))
//                Photo.edit(asset: editorAsset, config: EditorConfiguration.init())
          self.imageFromPHURL(phURL: path) { image in
              if let image = image {
                  // 处理 UIImage 对象，例如显示在 UIImageView 中
                let editorAsset = EditorAsset.init(type: .image(image))
                Photo.edit(asset: editorAsset, config: EditorConfiguration.init(), finished: { (asset, viewController) in
                    // 在这里处理完成编辑后的操作
                    print("编辑完成了！")
                    if let resultUrl = asset.result?.url {
                        print("编辑后的URL是：\(resultUrl.path)")
                        promise.resolve(resultUrl.path)
                        // 可以执行其他逻辑，例如关闭编辑器视图控制器
                        viewController.dismiss(animated: true, completion: nil)
                    }
    
                })
              } else {
                  print("无法加载图片")
                  promise.resolve("")
              }
          }
      }
    }

    // Enables the module to be used as a native view. Definition components that are accepted as part of the
    // view definition: Prop, Events.
    View(ReactNativePhotoEditorView.self) {
      // Defines a setter for the `name` prop.
      Prop("name") { (_: ReactNativePhotoEditorView, prop: String) in
        print(prop)
      }
    }
  }
    
    func imageFromPHURL(phURL: String, completion: @escaping (UIImage?) -> Void) {
        // Step 1: 提取 local identifier
        let localIdentifier = phURL.replacingOccurrences(of: "ph://", with: "")
        
        // Step 2: 获取 PHAsset
        let fetchResult = PHAsset.fetchAssets(withLocalIdentifiers: [localIdentifier], options: nil)
        guard let asset = fetchResult.firstObject else {
            completion(nil)
            return
        }
        
        // Step 3: 请求 UIImage
        let imageManager = PHImageManager.default()
        let options = PHImageRequestOptions()
        options.isSynchronous = false // 设置为异步加载
        options.deliveryMode = .highQualityFormat
        
        imageManager.requestImage(for: asset, targetSize: CGSize(width: 300, height: 300), contentMode: .aspectFit, options: options) { (image, _) in
            // 返回 UIImage
            completion(image)
        }
    }

     func getTopViewController() -> UIViewController? {
        // Find the active window
        let activeWindow: UIWindow? = UIApplication.shared.connectedScenes
            .filter { $0.activationState == .foregroundActive }
            .compactMap { $0 as? UIWindowScene }
            .first?.windows
            .filter { $0.isKeyWindow }.first
        
        // Start with the root view controller of the window
        var topController = activeWindow?.rootViewController
        // Follow presented view controllers to find the topmost one
        while let presentedViewController = topController?.presentedViewController {
            topController = presentedViewController
        }
        return topController
    }
    
    func getImageFromAsset(asset: PHAsset, completion: @escaping (String?) -> Void) {
        let manager = PHImageManager.default()
        let options = PHImageRequestOptions()
        options.version = .original
        options.isSynchronous = true

        manager.requestImageData(for: asset, options: options) { imageData, dataUTI, orientation, info in
            guard let data = imageData, let info = info else {
                completion(nil)
                return
            }
            
            // 检查文件的URL
            if let fileUrl = info["PHImageFileURLKey"] as? URL {
                completion(fileUrl.path)
            } else {
                // 如果无法直接获得URL，需要将图片数据写入临时文件
                let filename = UUID().uuidString + ".jpg"
                let path = NSTemporaryDirectory().appending(filename)
                do {
                    try data.write(to: URL(fileURLWithPath: path))
                    completion(path)
                } catch {
                    print("写入文件错误: \(error)")
                    completion(nil)
                }
            }
        }
    }
}
