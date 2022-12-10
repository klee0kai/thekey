package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.annotations.component.Component;
import com.github.klee0kai.stone.annotations.component.GcSoftScope;
import com.github.klee0kai.stone.interfaces.IComponent;

@Component
public interface AppComponent extends IComponent, IAppComponentInject {

    AppModule app();

    ProviderModule provider();

    EngineModule engine();

    DomainModule domain();

    ControlModule control();

    PresenterModule presenter();

    @GcSoftScope
    void gcAllSoftRefs();

}
