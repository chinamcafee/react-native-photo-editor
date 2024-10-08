package tech.wenchuan.rnphotoeditor.registry

/**
 * @author：luck
 * @date：2021/11/19 10:02 下午
 * @describe：Customizing PictureSelector Adapter
 */
class AdapterRegistry : BaseRegistry() {

    @Synchronized
    override fun <Model> register(targetClass: Class<Model>) {
        transcoders.add(Entry(targetClass))
    }

    @Synchronized
    override fun <Model> unregister(targetClass: Class<Model>) {
        transcoders.forEach { entry ->
            if (entry.handles(targetClass)) {
                transcoders.remove(entry)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Model> get(targetClass: Class<Model>): Class<Model> {
        transcoders.forEach { entry ->
            if (entry.handles(targetClass)) {
                return entry.fromClass as Class<Model>
            }
        }
        return targetClass
    }
}