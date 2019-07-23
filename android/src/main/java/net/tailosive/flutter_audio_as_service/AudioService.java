//
//  Flutter plugin for audio playback on Android
//  Created by Karol Wąsowski (karol@tailosive.net) on June 23rd 2019
//  Licensed under GPLv3
//

package net.tailosive.flutter_audio_as_service;

import android.app.Service;
import android.os.Binder;
import android.os.IBinder;
import java.util.Timer;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import java.io.IOException;

public class AudioService extends Service {
    private final IBinder binder = new AudioServiceBinder();
    MediaPlayer audioPlayer;

    public class AudioServiceBinder extends Binder {
        AudioService getService() {
            return  AudioService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void startService() {
        audioPlayer = new MediaPlayer();
        Log.i("Audio", "Service started successfuly");
    }

    public void loadAudio(String url) {
        try {
            audioPlayer.setDataSource(url);
            audioPlayer.prepare();
            audioPlayer.start();
        } catch(IOException e) {

        }
    }

    public void playAudio() {
        audioPlayer.start();
    }

    public void pauseAudio() {
        audioPlayer.pause();
    }

    public void stopAudio() {
        audioPlayer.reset();
    }
}