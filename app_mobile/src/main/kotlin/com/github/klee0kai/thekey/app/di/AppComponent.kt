package com.github.klee0kai.thekey.app.di

import com.github.klee0kai.stone.KotlinWrappersStone
import com.github.klee0kai.stone.Stone
import com.github.klee0kai.stone.annotations.component.Component
import com.github.klee0kai.stone.annotations.component.ExtendOf
import com.github.klee0kai.stone.annotations.component.Init
import com.github.klee0kai.stone.annotations.component.ModuleOriginFactory
import com.github.klee0kai.thekey.app.BuildConfig
import com.github.klee0kai.thekey.app.di.debug.DebugDI
import com.github.klee0kai.thekey.app.di.debug.DebugDI.initDummyModules
import com.github.klee0kai.thekey.app.di.dependencies.AppComponentProviders
import com.github.klee0kai.thekey.app.di.modules.AndroidHelpersModule
import com.github.klee0kai.thekey.app.di.modules.CoreAndroidHelpersModuleFactory
import com.github.klee0kai.thekey.app.di.modules.CoroutineModule
import com.github.klee0kai.thekey.app.di.modules.DBModule
import com.github.klee0kai.thekey.app.di.modules.EngineModule
import com.github.klee0kai.thekey.app.di.modules.HelpersModule
import com.github.klee0kai.thekey.app.di.modules.InteractorsModule
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.di.modules.RepositoriesModule
import com.github.klee0kai.thekey.app.features.allFeatures
import com.github.klee0kai.thekey.app.features.model.findApi
import com.github.klee0kai.thekey.app.ui.navigation.deeplink.configMainDeeplinks
import com.github.klee0kai.thekey.core.di.CoreComponent
import com.github.klee0kai.thekey.core.di.CoreDI
import com.github.klee0kai.thekey.core.di.hardResetToPreview
import com.github.klee0kai.thekey.core.di.identifiers.NoteGroupIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.NoteIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.PluginIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.di.wrap.AppWrappersStone
import com.github.klee0kai.thekey.core.domain.model.AppConfig
import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly

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
interface AppComponent : CoreComponent, AppComponentProviders {

    @ExtendOf
    fun ext(component: CoreComponent)


    /* get module */
    fun coroutine(): CoroutineModule
    fun presenters(): PresentersModule
    fun androidHelpers(): AndroidHelpersModule
    fun helpers(): HelpersModule
    fun interactors(): InteractorsModule
    fun repositories(): RepositoriesModule
    fun engine(): EngineModule
    fun databases(): DBModule

    /* get origin factories */
    @ModuleOriginFactory
    fun coroutineFactory(): CoroutineModule

    @ModuleOriginFactory
    fun presentersFactory(): PresentersModule

    @ModuleOriginFactory
    fun androidHelpersFactory(): AndroidHelpersModule

    @ModuleOriginFactory
    fun helpersFactory(): HelpersModule

    @ModuleOriginFactory
    fun interactorsFactory(): InteractorsModule

    @ModuleOriginFactory
    fun repositoriesFactory(): RepositoriesModule

    @ModuleOriginFactory
    fun engineFactory(): EngineModule

    @ModuleOriginFactory
    fun databasesFactory(): DBModule

    /* override */
    @Init
    fun initEngineModule(engineModule: Class<out EngineModule>)

    @Init
    fun initHelpersModule(helpers: Class<out HelpersModule>)

    @Init
    fun initAndroidHelpersModule(helpers: AndroidHelpersModule)

    @Init
    fun initPresenterModule(presentersModule: PresentersModule)

}

@DebugOnly
fun AppComponent.hardResetToPreview() {
    val ctx = ctx()
    val config = config()

    CoreDI.hardResetToPreview()
    DI = initAppComponent()

    DI.ctx(ctx)
    DI.config(config)
    DI.initDummyModules()
}

fun AppComponent.updateComponentsSoft() {
    gcWeak()
    if (BuildConfig.DEBUG) {
        with(DebugDI) { initDI() }
    }

    val availableFeatures = DynamicFeature
        .allFeatures()
        .filter { it.isCommunity || billingInteractor().isAvailable(it) }
        .mapNotNull { it.findApi() }

    availableFeatures
        .forEach { feature -> with(feature) { initDI() } }
}

fun AppComponent.configRouting() {
    val availableFeatures = DynamicFeature
        .allFeatures()
        .filter { it.isCommunity || billingInteractor().isAvailable(it) }
        .mapNotNull { it.findApi() }

    // init new deeplinks routing
    router().configDeeplinks {
        availableFeatures
            .forEach { feature -> with(feature) { configDeeplinks() } }

        configMainDeeplinks()
    }
}

private fun initAppComponent() = Stone.createComponent(AppComponent::class.java).apply {
    ext(CoreDI)
    with(CommercialDIInit) { initDI() }

    initCoreAndroidHelpersModule(CoreAndroidHelpersModuleFactory())
    config(AppConfig(isDebug = BuildConfig.DEBUG))
    updateComponentsSoft()
}
