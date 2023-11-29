package com.github.klee0kai.thekey.app.di

import com.github.klee0kai.stone.KotlinWrappersStone
import com.github.klee0kai.stone.Stone
import com.github.klee0kai.stone.annotations.component.Component
import com.github.klee0kai.stone.annotations.module.BindInstance
import com.github.klee0kai.thekey.app.App
import com.github.klee0kai.thekey.app.di.dependencies.AppComponentProviders
import com.github.klee0kai.thekey.app.di.modules.CoroutineModule
import com.github.klee0kai.thekey.app.di.modules.DBModule
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.di.modules.RepositoriesModule
import com.github.klee0kai.thekey.app.ui.navigation.Destination
import dev.olshevski.navigation.reimagined.NavController

val DI: AppComponent = Stone.createComponent(AppComponent::class.java)

@Component(
    wrapperProviders = [
        KotlinWrappersStone::class
    ]
)
interface AppComponent : AppComponentProviders {

    open fun coroutine(): CoroutineModule

    open fun presenters(): PresentersModule

    open fun repositories(): RepositoriesModule

    open fun databases(): DBModule


    @BindInstance(cache = BindInstance.CacheType.Weak)
    fun app(app: App? = null): App

    @BindInstance
    fun navigator(navController: NavController<Destination>? = null): NavController<Destination>

}