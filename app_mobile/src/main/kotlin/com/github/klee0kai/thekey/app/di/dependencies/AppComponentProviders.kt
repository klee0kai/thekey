package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.stone.annotations.dependencies.Dependencies

@Dependencies
interface AppComponentProviders :
    PresentersDependencies,
    AndroidHelpersDependencies,
    HelperDependencies,
    InteractorsDependencies,
    RepositoriesDependencies,
    EngineDependencies