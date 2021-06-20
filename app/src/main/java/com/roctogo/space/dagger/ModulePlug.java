package com.roctogo.space.dagger;

import com.roctogo.space.Imvp;
import com.roctogo.space.plug.PlugPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class ModulePlug {
    @Provides
    @UserScope
    Imvp.IPresenterPlug providePlug(Imvp.IModel model) {
        return new PlugPresenter(model);
    }
}
