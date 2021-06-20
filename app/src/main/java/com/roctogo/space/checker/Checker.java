package com.roctogo.space.checker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.roctogo.space.Constants;
import com.roctogo.space.ICallWebOrPlug;
import com.roctogo.space.Imvp;
import com.roctogo.space.R;
import com.roctogo.space.dagger.DaggerIComponent;
import com.roctogo.space.plug.Plug;
import com.roctogo.space.web.Web;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Checker extends AppCompatActivity implements Imvp.IViewChecker, ICallWebOrPlug {
    @Inject
    Imvp.IPresenterChecker presenterChecker;
    @BindView(R.id.loadingText)
    TextView loading;

    private Animation mFadeInAnimation;
    private Animation mFadeOutAnimation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checker);

        ButterKnife.bind(this);

        DaggerIComponent.builder().build().inject(this);

        presenterChecker.attachView(this);
        presenterChecker.setCallBack(this);
        presenterChecker.setNetworkResource((ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE));
        presenterChecker.checkUser();

        hideNavigation();

        setAnimation();
    }

    private void hideNavigation() {
        int currentApiVersion = Build.VERSION.SDK_INT;


        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;


        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(flags);


            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(visibility -> {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            decorView.setSystemUiVisibility(flags);
                        }
                    });
        }
    }

    private void setAnimation() {
        mFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        mFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        mFadeInAnimation.setAnimationListener(getAnimListener(mFadeOutAnimation));
        mFadeOutAnimation.setAnimationListener(getAnimListener(mFadeInAnimation));

        loading.startAnimation(mFadeOutAnimation);
    }

    private Animation.AnimationListener getAnimListener(Animation anim) {
        return new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                loading.startAnimation(anim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        loading.clearAnimation();
    }

    @Override
    protected void onDestroy() {
        presenterChecker.detachView();
        super.onDestroy();
    }

    @Override
    public void openWeb(String url) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                "com.roctogo.space", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.LINK, url).apply();
        letDoIt(Web.class);
    }

    @Override
    public void openPlug() {
        letDoIt(Plug.class);
    }

    private void letDoIt(final Class<?> cls) {
        Intent intent = new Intent(Checker.this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
