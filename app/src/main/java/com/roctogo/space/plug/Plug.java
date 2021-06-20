package com.roctogo.space.plug;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.roctogo.space.Imvp;
import com.roctogo.space.R;
import com.roctogo.space.dagger.DaggerIComponent;

import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Plug extends AppCompatActivity implements Imvp.IViewPlug {
    @Inject
    Imvp.IPresenterPlug presenterPlug;

    @BindView(R.id.pole)
    TableLayout tableLayout;

    private Display display;
    private int width;
    private static int height;

    private int x, y;
    private int xPlayer, yPlayer;
    private int boom;
    private int win;

    private ImageView[][] imageViews;

    private int[][] pole;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plug);

        ButterKnife.bind(this);

        DaggerIComponent.builder().build().inject(this);
        presenterPlug.attachView(this);

        hideNavigation();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        playGame();
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

    @Override
    protected void onDestroy() {
        presenterPlug.detachView();
        super.onDestroy();
    }

    private void playGame() {
        display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();

        win = sharedPreferences.getInt("Win", 0);

        x = width / 100;
        y = height / 100;
        boom = x * y / 10;

        xPlayer = (x - 1) / 2;
        yPlayer = y - 1;

        imageViews = new ImageView[x][y];
        pole = new int[x][y];

        for (int i = 0; i < y; i++) {
            TableRow tableRow = new TableRow(this);

            TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tableLayoutParams.gravity = Gravity.CENTER;
            tableRow.setLayoutParams(tableLayoutParams);
            tableLayout.addView(tableRow);

            for (int j = 0; j < x; j++) {
                final ImageView imageView = new ImageView(this);
                if (i == 0) {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.finish));
                } else {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.lotoc));
                }

                imageViews[j][i] = imageView;

                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(100, 100);
                imageView.setLayoutParams(layoutParams);
                tableRow.addView(imageView);


                int finalJ = j;
                int finalI = i;
                imageView.setOnClickListener(view -> {
                    if (getFrogAround(finalJ, finalI)) {
                        pole[finalJ][finalI] = 1;
                        xPlayer = finalJ;
                        yPlayer = finalI;
                        imageViews[finalJ][finalI].setImageDrawable(getResources().getDrawable(R.drawable.frog));
                        delBoom();
                        setBoom();
                        if (yPlayer == 0) {
                            sharedPreferences.edit().putInt("Win", ++win).apply();
                            gameOver("Congratulations! You saved the frog's life!");
                        }
                    }
                });

            }
        }
        imageViews[xPlayer][yPlayer].setImageDrawable(getResources().getDrawable(R.drawable.frog));
        pole[xPlayer][yPlayer] = 1;


        new AlertDialog.Builder(this)
                .setTitle("Welcome!!!")
                .setMessage("Get home to the reeds by jumping on nearby lotuses. Beware of Dynamites!!!")
                .setCancelable(false)
                .setPositiveButton("Play!", (dialogInterface, i) ->
                        dialogInterface.cancel()
                )
                .create()
                .show();
    }

    private void setBoom() {
        int row, column;
        boolean flag = false;
        for (int i = 0; i < boom; i++) {
            row = new Random().nextInt(x);
            column = new Random().nextInt(y - 1) + 1;

            if (pole[row][column] == 1) {
                pole[row][column] = 2;
                imageViews[row][column].setImageDrawable(getResources().getDrawable(R.drawable.rip));
                flag = true;
            } else if (pole[row][column] != 2) {
                pole[row][column] = 2;
                imageViews[row][column].setImageDrawable(getResources().getDrawable(R.drawable.boom));
            } else {
                --i;
            }
        }
        if (flag) {
            new Asynk().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void gameOver(String message) {

        new AlertDialog.Builder(this)
                .setTitle(message)
                .setMessage("Salvation: " + win)
                .setCancelable(false)
                .setPositiveButton("Restart", (dialogInterface, i) -> recreateField())
                .setNegativeButton("Exit", (dialogInterface, i) -> {
                    dialogInterface.cancel();
                    finish();
                })
                .create()
                .show();
    }

    private void recreateField() {
        delBoom();
        pole[xPlayer][yPlayer] = 0;
        xPlayer = (x - 1) / 2;
        yPlayer = y - 1;

        imageViews[xPlayer][yPlayer].setImageDrawable(getResources().getDrawable(R.drawable.frog));
        pole[xPlayer][yPlayer] = 1;
        for (int i = 0; i < x; i++) {
            imageViews[i][0].setImageDrawable(getResources().getDrawable(R.drawable.finish));
        }

    }

    private void delBoom() {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                if (pole[i][j] == 2) {
                    pole[i][j] = 0;
                    imageViews[i][j].setImageDrawable(getResources().getDrawable(R.drawable.lotoc));
                }
            }
        }
    }

    private boolean getFrogAround(int xI, int yI) {
        for (int i = xI - 1; i <= xI + 1; i++) {
            for (int j = yI - 1; j <= yI + 1; j++) {
                if (i < 0 || j < 0 || i >= x || j >= y) {
                    continue;
                } else if (pole[i][j] == 1) {
                    pole[i][j] = 0;
                    imageViews[i][j].setImageDrawable(getResources().getDrawable(R.drawable.lotoc));
                    return true;
                }
            }
        }
        return false;
    }

    public class Asynk extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            gameOver("Game Over");
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
