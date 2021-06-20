package com.roctogo.space.dagger;

import com.roctogo.space.Imvp;
import com.roctogo.space.model.Model;
import com.roctogo.space.checker.CheckerPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class ModuleChecker {
    @Provides
    @UserScope
    Imvp.IPresenterChecker provideChecker(Imvp.IModel model) {
        return new CheckerPresenter(model);
    }

    @Provides
    @UserScope
    Imvp.IModel provideModel() {
        return new Model();
    }
}
