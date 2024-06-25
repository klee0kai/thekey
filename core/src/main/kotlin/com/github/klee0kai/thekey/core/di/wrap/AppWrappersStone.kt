package com.github.klee0kai.thekey.core.di.wrap

import com.github.klee0kai.stone.annotations.wrappers.WrappersCreator
import com.github.klee0kai.stone.wrappers.creators.CircleWrapper
import javax.inject.Provider

@WrappersCreator(
    wrappers = [
        AsyncCoroutineProvide::class
    ]
)
class AppWrappersStone : CircleWrapper {

    override fun <Wr : Any?, T : Any?> wrap(
        wrapperCl: Class<Wr>,
        originalProvider: Provider<T>
    ): Wr? {
        return when {
            wrapperCl == AsyncCoroutineProvide::class.java -> AsyncCoroutineProvide { originalProvider.get() } as Wr
            else -> null
        }
    }

    override fun <Wr : Any?, T : Any?> unwrap(wrapperCl: Class<Wr>?, objectType: Class<T>?, wrapper: Wr): T? {
        return when {
            wrapperCl == AsyncCoroutineProvide::class.java -> (wrapper as AsyncCoroutineProvide<T>).syncGet()
            else -> null
        }
    }

}