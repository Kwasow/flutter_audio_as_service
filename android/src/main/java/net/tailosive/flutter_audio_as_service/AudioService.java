//
//  Flutter plugin for audio playback on Android
//  Created by Karol Wąsowski (karol@tailosive.net) on June 23rd 2019
//  Licensed under GPLv3
//

package net.tailosive.flutter_audio_as_service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import java.io.IOException;

public class AudioService extends Service {
    private final IBinder binder = new AudioServiceBinder();
    MediaPlayer audioPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("Audio/service", "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class AudioServiceBinder extends Binder {
        AudioService getService() {
            return  AudioService.this;
        }
    }

    public void startService() {
        audioPlayer = new MediaPlayer();
        Log.i("Audio", "Service started successfuly");
    }

    public void loadAudio(String url) {
        Log.i("Audio", "Loading...");
        try {
            audioPlayer.setDataSource(url);
            audioPlayer.prepare();
            audioPlayer.start();
            Log.i("Audio", "Loaded and playing");
        } catch(IOException e) {

        }
    }

    public void playAudio() {
        audioPlayer.start();
        Log.i("Audio", "Play");
    }

    public void pauseAudio() {
        audioPlayer.pause();
        Log.i("Audio", "Paused");
    }

    public void stopAudio() {
        audioPlayer.reset();
        Log.i("Audio", "Source discarded");
    }

    public int getDuration() {
        return audioPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return audioPlayer.getCurrentPosition();
    }

    public void seekTo(int positionInMs) {
        audioPlayer.seekTo(positionInMs);
        Log.i("Audio", "Seeked to " + positionInMs + "ms");
    }
}