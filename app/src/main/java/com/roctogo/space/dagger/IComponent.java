package com.roctogo.space.dagger;

import com.roctogo.space.checker.Checker;
import com.roctogo.space.plug.Plug;
import com.roctogo.space.web.Web;

import dagger.Component;

@UserScope
@Component(modules = {ModuleChecker.class, ModuleWeb.class, ModulePlug.class})
public interface IComponent {
    void inject(Checker checker);

    void inject(Plug plug);

    void inject(Web web);
}
