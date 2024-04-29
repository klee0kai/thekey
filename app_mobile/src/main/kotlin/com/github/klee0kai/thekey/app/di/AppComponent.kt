package com.github.klee0kai.thekey.app.di

import android.content.Context
import androidx.activity.ComponentActivity
import com.github.klee0kai.stone.KotlinWrappersStone
import com.github.klee0kai.stone.Stone
import com.github.klee0kai.stone.annotations.component.Component
import com.github.klee0kai.stone.annotations.component.Init
import com.github.klee0kai.stone.annotations.module.BindInstance
import com.github.klee0kai.thekey.app.BuildConfig
import com.github.klee0kai.thekey.app.di.debug.DebugDI
import com.github.klee0kai.thekey.app.di.dependencies.AppComponentProviders
import com.github.klee0kai.thekey.app.di.identifier.NoteGroupIdentifier
import com.github.klee0kai.thekey.app.di.identifier.NoteIdentifier
import com.github.klee0kai.thekey.app.di.identifier.PluginIdentifier
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.di.modules.AndroidHelpersModule
import com.github.klee0kai.thekey.app.di.modules.CoroutineModule
import com.github.klee0kai.thekey.app.di.modules.DBModule
import com.github.klee0kai.thekey.app.di.modules.EngineModule
import com.github.klee0kai.thekey.app.di.modules.HelpersModule
import com.github.klee0kai.thekey.app.di.modules.InteractorsModule
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.di.modules.RepositoriesModule
import com.github.klee0kai.thekey.app.di.modules.ThemeModule
import com.github.klee0kai.thekey.app.di.wrap.AppWrappersStone
import com.github.klee0kai.thekey.app.domain.model.AppConfig
import com.github.klee0kai.thekey.app.features.allFeatures
import com.github.klee0kai.thekey.app.features.model.DynamicFeature
import com.github.klee0kai.thekey.app.features.model.findApi
import com.github.klee0kai.thekey.app.utils.annotations.DebugOnly

var DI: AppComponent = initAppComponent()
    private set

@Component(
    identifiers = [
        StorageIdentifier::class,
        NoteIdentifier::class,
        NoteGroupIdentifier::class,
        PluginIdentifier::class,
        DynamicFeature::class,
    ],
    wrapperProviders = [
        KotlinWrappersStone::class,
        AppWrappersStone::class,
    ],
)
interface AppComponent : AppComponentProviders {

    open fun coroutine(): CoroutineModule

    open fun presenters(): PresentersModule

    open fun androidHelpers(): AndroidHelpersModule

    open fun helpers(): HelpersModule

    open fun interactors(): InteractorsModule

    open fun repositories(): RepositoriesModule

    open fun engine(): EngineModule

    open fun databases(): DBModule

    open fun theme(): ThemeModule

    @Init
    fun initEngineModule(engineModule: Class<out EngineModule>)

    @Init
    fun initHelpersModule(helpers: Class<out HelpersModule>)

    @Init
    fun initAndroidHelpersModule(helpers: AndroidHelpersModule)

    @Init
    fun initPresenterModule(presentersModule: PresentersModule)

    @BindInstance(cache = BindInstance.CacheType.Weak)
    fun ctx(ctx: Context? = null): Context

    @BindInstance(cache = BindInstance.CacheType.Weak)
    fun activity(app: ComponentActivity? = null): ComponentActivity?

    @BindInstance(cache = BindInstance.CacheType.Strong)
    fun config(snackbarHostState: AppConfig? = null): AppConfig

}

@DebugOnly
fun AppComponent.hardReset() {
    DI = initAppComponent()
}

private fun initAppComponent() = Stone.createComponent(AppComponent::class.java).apply {
    config(AppConfig())

    if (BuildConfig.DEBUG) {
        with(DebugDI) { initDI() }
    }

    DynamicFeature
        .allFeatures()
        .forEach { feature ->
            feature.findApi()?.initDI()
        }

}
