package com.example.a2170131.el_levelingalarm;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    private SoundPool mSoundPool;
    private int mSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        playFromSoundPool();

        // ボタンを押した時の処理
        Button button_ok = findViewById(R.id.button_ok);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK, new Intent());
                finish();
            }
        });

        // 3分後に自動で元の画面に戻る(失敗扱い)
        new Handler().postDelayed(func, 180000);
    }

    /**
     * 画面起動後、一定時間経過で元の画面に戻る
     */
    public final Runnable func= new Runnable() {
        @Override
        public void run() {
            // 元の画面に戻る(失敗扱い)
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        // 予め音声データを読み込む
//        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        //  Android5.0(API 21)から非推奨になったので以下に置き換え
        final AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(attrs)
                .setMaxStreams(1)
                .build();

        // TODO 音声ファイルを探してくる
//        mSoundId = mSoundPool.load(getApplicationContext(), R.raw.nyanpasu, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // リリース
        mSoundPool.release();
    }

    private void playFromSoundPool() {
        // 再生
        mSoundPool.play(mSoundId, 1.0F, 1.0F, 0, 0, 1.0F);
    }
}
