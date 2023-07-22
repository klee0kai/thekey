package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.annotations.component.Component;
import com.github.klee0kai.stone.annotations.component.GcSoftScope;
import com.github.klee0kai.stone.annotations.module.BindInstance;
import com.kee0kai.thekey.App;

@Component
public interface AppComponent extends IAppComponentInject {

    AppModule app();

    ProviderModule provider();

    EngineModule engine();

    DomainModule domain();

    ControlModule control();

    PresenterModule presenter();

    @GcSoftScope
    void gcAllSoftRefs();

    @BindInstance
    void bind(App app);

}
