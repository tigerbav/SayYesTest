package com.roctogo.space.plug;

import com.roctogo.space.Imvp;

import javax.inject.Inject;

public class PlugPresenter implements Imvp.IPresenterPlug {
    private Imvp.IViewPlug viewPlug;
    private Imvp.IModel model;

    @Inject
    public PlugPresenter(Imvp.IModel model) {
        this.model = model;
    }

    @Override
    public void attachView(Imvp.IViewPlug viewPlug) {
        this.viewPlug = viewPlug;
    }

    @Override
    public void detachView() {
        viewPlug = null;
    }
}
