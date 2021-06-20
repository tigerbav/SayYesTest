package com.roctogo.space;

import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

public interface Imvp {
    interface IViewPlug {
    }

    interface IPresenterPlug {
        void attachView(IViewPlug viewPlug);
        void detachView();
    }

    interface IViewWeb {
    }

    interface IPresenterWeb {
        void attachView(IViewWeb viewWeb);
        void detachView();
    }

    interface IViewChecker {
    }

    interface IPresenterChecker {
        void attachView(IViewChecker viewChecker);
        void detachView();
        void checkUser();
        void setCallBack(ICallWebOrPlug iCallWebOrPlug);
        void setNetworkResource(ConnectivityManager connectivityManager);
    }

    interface IModel {
        void checkGit();
        void setCallBack(ICallWebOrPlug iCallWebOrPlug);
        void setNetworkResource(ConnectivityManager connectivityManager);
    }
}
