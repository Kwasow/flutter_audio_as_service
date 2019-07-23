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

import AudioService.java;

/** FlutterAudioAsServicePlugin */
public class FlutterAudioAsServicePlugin implements MethodCallHandler {
  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_audio_as_service");
    channel.setMethodCallHandler(new FlutterAudioAsServicePlugin());
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    
    if (call.method.equals('loadAudio')) {
      
    } else {
      result.notImplemente();
    }

    if (call.method.equals('playAudio')) {
      
    } else {
      result.notImplemente();
    }

    if (call.method.equals('pauseAudio')) {
      
    } else {
      result.notImplemente();
    }

    if (call.method.equals('stopAudio')) {
      
    } else {
      result.notImplemente();
    }

  }
}
