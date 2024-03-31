package com.github.klee0kai.thekey.app.di

import androidx.activity.ComponentActivity
import com.github.klee0kai.stone.KotlinWrappersStone
import com.github.klee0kai.stone.Stone
import com.github.klee0kai.stone.annotations.component.Component
import com.github.klee0kai.stone.annotations.component.Init
import com.github.klee0kai.stone.annotations.module.BindInstance
import com.github.klee0kai.thekey.app.App
import com.github.klee0kai.thekey.app.BuildConfig
import com.github.klee0kai.thekey.app.di.debug.DebugDI
import com.github.klee0kai.thekey.app.di.dependencies.AppComponentProviders
import com.github.klee0kai.thekey.app.di.identifier.NoteGroupIdentifier
import com.github.klee0kai.thekey.app.di.identifier.NoteIdentifier
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
import com.github.klee0kai.thekey.app.model.AppConfig

val DI: AppComponent = Stone.createComponent(AppComponent::class.java).apply {
    if (BuildConfig.DEBUG) {
        with(DebugDI) { initDI() }
    }
}


@Component(
    identifiers = [
        StorageIdentifier::class,
        NoteIdentifier::class,
        NoteGroupIdentifier::class,
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

    @BindInstance(cache = BindInstance.CacheType.Weak)
    fun app(app: App? = null): App

    @BindInstance(cache = BindInstance.CacheType.Weak)
    fun activity(app: ComponentActivity? = null): ComponentActivity?

    @BindInstance(cache = BindInstance.CacheType.Strong)
    fun config(snackbarHostState: AppConfig? = null): AppConfig

}