//
//  Flutter plugin for audio playback on Android
//  Created by Karol Wąsowski (karol@tailosive.net) on June 23rd 2019
//  Licensed under GPLv3
//

package net.tailosive.flutter_audio_as_service;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.app.Service;
import android.app.Activity;

/** FlutterAudioAsServicePlugin */
public class FlutterAudioAsServicePlugin extends Activity implements MethodCallHandler {

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_audio_as_service");
    channel.setMethodCallHandler(new FlutterAudioAsServicePlugin());
  }

  AudioService audioService;

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    switch (call.method) {

      case "startService":
        Intent serviceIntent = new Intent();
        serviceIntent.setAction("net.tailosive.flutter_audio_as_service.AudioService");
        FlutterAudioAsServicePlugin.this.startService(serviceIntent);
        break;

      case "loadAudio":
        String url = call.argument("url");
        audioService.loadAudio(url);
        break;

      case "playAudio":
        audioService.playAudio();
        break;

      case "pauseAudio":
        audioService.pauseAudio();
        break;

      case "stopAudio":
        audioService.stopAudio();
        break;

      case "getDuration":
        result.success(audioService.getDuration());
        break;

      case "getCurrentPosition":
        result.success(audioService.getCurrentPosition());
        break;

      case "seekTo":
        int positionInMs = call.argument("positionInMs");
        audioService.seekTo(positionInMs);
        break;
      
      case "seekBy":
        int seekByInMs = call.argument("seekByInMs");
        audioService.seekTo(audioService.getCurrentPosition() + seekByInMs);
        break;

      default:
        Log.e("Audio", "Wrong method call");
        result.notImplemented();
        break;
    }
  }
}