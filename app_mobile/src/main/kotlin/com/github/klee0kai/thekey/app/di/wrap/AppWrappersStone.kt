package com.github.klee0kai.thekey.app.di.wrap

import com.github.klee0kai.stone.annotations.wrappers.WrappersCreator
import com.github.klee0kai.stone.wrappers.creators.ProviderWrapper
import javax.inject.Provider

@WrappersCreator(
    wrappers = [
        AsyncCoroutineProvide::class
    ]
)
class AppWrappersStone : ProviderWrapper {

    override fun <Wr : Any?, T : Any?> wrap(
        wrapperCl: Class<Wr>,
        originalProvider: Provider<T>
    ): Wr? {
        return when {
            wrapperCl == AsyncCoroutineProvide::class.java -> AsyncCoroutineProvide { originalProvider.get() } as Wr
            else -> null
        }
    }

}