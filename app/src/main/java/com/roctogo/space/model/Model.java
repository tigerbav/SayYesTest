package com.roctogo.space.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.roctogo.space.Constants;
import com.roctogo.space.ICallWebOrPlug;
import com.roctogo.space.Imvp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Model implements Imvp.IModel {
    private ICallWebOrPlug callWebOrPlug;
    private NetworkInfo wifiInfo;
    private ConnectivityManager connectivityManager;


    @Override
    public void checkGit() {
        try{
            if(hasConnection()) {
                checkInNewThread(Constants.URL_FLAG);
            } else {
                callWebOrPlug.openPlug();
            }
        } catch (Exception e) {
            callWebOrPlug.openPlug();
        }

    }

    @Override
    public void setCallBack(ICallWebOrPlug iCallWebOrPlug) {
        callWebOrPlug = iCallWebOrPlug;
    }

    @Override
    public void setNetworkResource(ConnectivityManager connectivityManager) {
        this.connectivityManager = connectivityManager;
    }

    private void checkInNewThread(String url) {
        new Thread(() -> {
            URL obj = null;
            try {
                obj = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) obj.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                connection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }

            //read
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String inputLine = null;
            StringBuilder response = new StringBuilder();

            while (true) {
                try {
                    if ((inputLine = in.readLine()) == null) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                response.append(inputLine);
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String temp = response.toString();

            if (url.equals(Constants.WAY_TO_LINK) && !temp.equals(Constants.EMPTY)) {
                callWebOrPlug.openWeb(temp);
            } else {
                if (temp.equals(Constants.EMPTY) || temp.equals(Constants.FALSE)) {
                    callWebOrPlug.openPlug();
                } else if (temp.equals(Constants.TRUE)) {
                    checkInNewThread(Constants.WAY_TO_LINK);
                }
            }

        }).start();
    }

    private boolean hasConnection() {
        wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
            return true;
        wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
            return true;
        wifiInfo = connectivityManager.getActiveNetworkInfo();
        return wifiInfo != null && wifiInfo.isConnected();
    }
}
