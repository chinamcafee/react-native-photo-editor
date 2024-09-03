package tech.wenchuan.rnphotoeditor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tech.wenchuan.rnphotoeditor.factory.ClassFactory
import tech.wenchuan.rnphotoeditor.helper.FragmentInjectManager
import tech.wenchuan.rnphotoeditor.immersive.ImmersiveManager.immersiveAboveAPI23
import tech.wenchuan.rnphotoeditor.provider.SelectorProviders

/**
 * @author：luck
 * @date：2022/2/10 6:07 下午
 * @describe：SelectorSupporterActivity
 */
class SelectorSupporterActivity : AppCompatActivity() {
    private val config = SelectorProviders.getInstance().getConfig()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersive()
        setContentView(R.layout.ps_activity_container)
        val instance = ClassFactory.NewInstance()
            .create(config.registry.get(SelectorMainFragment::class.java))
        FragmentInjectManager.injectFragment(this, instance.getFragmentTag(), instance)
    }

    private fun immersive() {
        immersiveAboveAPI23(
            this,
            config.statusBarStyle.getStatusBarColor(),
            config.statusBarStyle.getNavigationBarColor(),
            config.statusBarStyle.isDarkStatusBar()
        )
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.ps_anim_fade_in,
            config.windowAnimStyle.getExitAnimRes()
        )
    }
}