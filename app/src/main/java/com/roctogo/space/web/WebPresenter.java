package com.roctogo.space.web;

import com.roctogo.space.Imvp;

import javax.inject.Inject;

public class WebPresenter implements Imvp.IPresenterWeb {
    private Imvp.IViewWeb viewWeb;
    private Imvp.IModel model;

    @Inject
    public WebPresenter(Imvp.IModel model) {
        this.model = model;
    }

    @Override
    public void attachView(Imvp.IViewWeb viewWeb) {
        this.viewWeb = viewWeb;
    }

    @Override
    public void detachView() {
        viewWeb = null;
    }
}
