package com.example.a2170131.el_levelingalarm;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_FILE_NAME = "com.example.a2170131.el_levelingalarm.PREF_FILE";
    // メイン画面で時刻表示してるTextView
    TextView text_now;
    // メイン画面でレベルの数値を表示
    TextView textView_lv;
    // レベル表示用
    ProgressBar progressBar;
    // メイン画面のコンテキスト
    Context main_disp;
    // アラームに設定されている時刻
    int setting_hourOfDay;
    int setting_minute;
    // 現在時刻
    int now_hourOfDay;
    int now_minute;
    int now_second;
    // 経験値
    int exp;
    int lv;
    // 今回の経験値量(初期値：1)
    int exp_bonus;

    // 画面下のボタン
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_setting:
                    // 時刻設定ダイアログの表示
                    TimePickerDialog dialog = new TimePickerDialog(
                            main_disp,
                            // 設定完了時の処理
                            new TimePickerDialog.OnTimeSetListener(){
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    // アラームの時間と分をセット
                                    setting_hourOfDay = hourOfDay;
                                    setting_minute = minute;
                                }
                            },
                            now_hourOfDay,now_minute,true);
                    dialog.show();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 画面下のボタンの表示
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // 設定画面用にコンテキストの取得
        main_disp = this;

        // 現在時刻の取得
        Calendar calendar = Calendar.getInstance();
        now_hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        now_minute = calendar.get(Calendar.MINUTE);
        now_second = calendar.get(Calendar.SECOND);

        // 現在時刻を表示するTextViewを取得
        text_now = findViewById(R.id.textView_now);

        // 一秒ごとに処理する部分
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                Timer_tick(); // 時刻の更新
                Alarm(); // 時間になったらアラーム起動
                handler.postDelayed(this, 1000); // 処理の動作間隔(1000ミリ秒)
            }
        };
        handler.post(r); // 処理の起動

        // 保存してあるデータの読み取り
        SharedPreferences sharedPref = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        setting_hourOfDay = sharedPref.getInt("hourOfDay",now_hourOfDay);
        setting_minute = sharedPref.getInt("minute",now_minute);
        exp = sharedPref.getInt("exp",0);
        lv = sharedPref.getInt("lv",1);
        exp_bonus = sharedPref.getInt("exp_bonus",1);

        // 経験値表示の更新
        textView_lv = findViewById(R.id.textView_LevelNum);
        progressBar = findViewById(R.id.progressBar);
        textView_lv.setText(Integer.toString(lv));
        progressBar.setProgress(exp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            // 時間内にアラームを止められた場合の処理
            if (resultCode == RESULT_OK) {
                // 経験値を追加
                addExp();
                setExp_bonus(true);
                // トーストの表示
                Toast.makeText(this, "経験値を獲得したよ！", Toast.LENGTH_LONG).show();
                // 経験値表示の更新
                textView_lv = findViewById(R.id.textView_LevelNum);
                progressBar = findViewById(R.id.progressBar);

                textView_lv.setText(Integer.toString(lv));
                progressBar.setProgress(exp);
            }
        } else {
            setExp_bonus(false);
        }

    }

    /**
     * 次回の経験値の設定
     * @param success_flag 時間内に起き上がれたか
     */
    protected void setExp_bonus(boolean success_flag){
        if(success_flag){
            exp_bonus += 1;
        } else {
            exp_bonus = 1;
        }
    }

    protected void addExp(){
        // 経験値を追加
        exp += exp_bonus;
        // レベルが上がるかのチェック
        if (check_lv()){
            Toast.makeText(this, "レベルが上がったよ！", Toast.LENGTH_LONG).show();
        }
    }

    protected boolean check_lv(){
        // レベルが上がるか
        if (exp >= 15){
            exp -= 15;
            lv++;
            return true;
        } else {
            return false;
        }
    }

    protected void Timer_tick(){
        // 時刻の更新
        now_second++;
        if (now_second >= 60){
            now_minute++;
            now_second = 0;
        }
        if(now_minute >= 60){
            now_hourOfDay++;
            now_minute = 0;
        }
        if (now_hourOfDay >= 24){
            now_hourOfDay = 0;
        }

        // 画面表示の更新
        text_now.setText(
                String.format("%02d:%02d:%02d", now_hourOfDay,now_minute,now_second));
    }

    protected void Alarm(){
        if(text_now.getText().equals(
                String.format("%02d:%02d:00", setting_hourOfDay, setting_minute))){
            // 設定時刻になった
            Intent intent = new Intent(getApplication(), AlarmActivity.class);
            startActivityForResult(intent, 0);
        }
    }
}
