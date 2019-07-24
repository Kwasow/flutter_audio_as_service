//
//  Flutter plugin for audio playback on Android
//  Created by Karol Wąsowski (karol@tailosive.net) on June 23rd 2019
//  Licensed under GPLv3
//

package net.tailosive.flutter_audio_as_service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.media.MediaPlayer;
import java.io.IOException;
import android.util.Log;

public class AudioService extends Service {
  MediaPlayer audioPlayer;

  @Override
  public IBinder onBind(Intent intent) {
      return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);

    // create a new player
    audioPlayer = new MediaPlayer();
    Log.i("Audio", "Service started successfuly");
    return START_NOT_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    // destroy the player
    audioPlayer.release();
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
    stopSelf();
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