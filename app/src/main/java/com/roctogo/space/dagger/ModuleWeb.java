package com.roctogo.space.dagger;

import com.roctogo.space.Imvp;
import com.roctogo.space.web.WebPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class ModuleWeb {
    @Provides
    @UserScope
    Imvp.IPresenterWeb provideWeb(Imvp.IModel model) {
        return new WebPresenter(model);
    }
}
