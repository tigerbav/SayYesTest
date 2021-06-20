package com.roctogo.space.checker;

import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.roctogo.space.ICallWebOrPlug;
import com.roctogo.space.Imvp;

import javax.inject.Inject;

public class CheckerPresenter implements Imvp.IPresenterChecker {
    private Imvp.IViewChecker viewChecker;
    private Imvp.IModel model;

    @Inject
    public CheckerPresenter(Imvp.IModel model) {
        this.model = model;
    }

    @Override
    public void attachView(Imvp.IViewChecker viewChecker) {
        this.viewChecker = viewChecker;
    }

    @Override
    public void detachView() {
        viewChecker = null;
    }

    @Override
    public void checkUser() {
        model.checkGit();
    }

    @Override
    public void setCallBack(ICallWebOrPlug iCallWebOrPlug) {
        model.setCallBack(iCallWebOrPlug);
    }

    @Override
    public void setNetworkResource(ConnectivityManager connectivityManager) {
        model.setNetworkResource(connectivityManager);
    }
}
