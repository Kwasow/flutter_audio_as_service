//
//  Flutter plugin for audio playback on Android
//  Created by Karol Wąsowski (karol@tailosive.net) on June 23rd 2019
//  Licensed under the BSD License
//

import 'dart:async';
import 'package:flutter/services.dart';

class FlutterAudioAsService {
  static const MethodChannel nativeChannel =
      const MethodChannel("AudioService");
  static AudioPlayerListener audioListener;

  // invoke Flutter from native
  static void setListeners(AudioPlayerListener listener) {
    audioListener = listener;

    nativeChannel.setMethodCallHandler(methodCallHandler);
  }

  static Future<dynamic> methodCallHandler(MethodCall call) {
    switch (call.method) {
      case "onPlayerStateChanged":
        switch (call.arguments) {
          case "idle":
            audioListener.onPlayerStateChanged(PlayerState.idle);
            break;

          case "buffering":
            audioListener.onPlayerStateChanged(PlayerState.loading);
            break;

          case "playing":
            audioListener.onPlayerStateChanged(PlayerState.playing);
            break;

          case "pause":
            audioListener.onPlayerStateChanged(PlayerState.paused);
            break;
        }
        break;

      case "onPlayerPositionChanged":
        audioListener
            .onPlayerPositionChanged(Duration(milliseconds: call.arguments));
        break;

      case "onPlayerCompleted":
        audioListener.onPlayerCompleted();
        break;

      default:
        print("ERROR: method not implemented");
        break;
    }

    return null;
  }

  // invoke native methods
  static Future<void> init(String title, String channel, String url,
      String albumCover, String appIcon) async {
    String checkIfNull(String toCheck) {
      if (toCheck == null) {
        return "theGivenResourceIsNull";
      } else {
        return toCheck;
      }
    }

    await nativeChannel.invokeMethod("startService", {
      "title": title,
      "channel": channel,
      "url": url,
      "albumCover": checkIfNull(albumCover),
      "appIcon": checkIfNull(appIcon),
    });
  }

  static Future<void> stop() async {
    await nativeChannel.invokeMethod("stop");
  }

  static Future<void> pause() async {
    await nativeChannel.invokeMethod("pause");
  }

  static Future<void> resume() async {
    await nativeChannel.invokeMethod("resume");
  }

  static Future<void> seekBy(Duration duration) async {
    await nativeChannel.invokeMethod("seekBy", {
      "seekByInMs": duration.inMilliseconds,
    });
  }

  static Future<Duration> getAudioLength() async {
    dynamic audioLength = await nativeChannel.invokeMethod("getAudioLength");
    if (audioLength == null) {
      audioLength = 1000;
    }
    return Duration(milliseconds: audioLength);
    // returns milliseconds
  }

  static Future<void> seekTo(Duration seekTo) async {
    await nativeChannel.invokeMethod("seekTo", {
      "seekToInMs": seekTo.inMilliseconds,
    });
  }
}

enum PlayerState { idle, loading, playing, paused }

class AudioPlayerListener {
  AudioPlayerListener({
    Function onPlayerStateChanged,
    Function onPlayerPositionChanged,
    Function onPlayerCompleted,
  })  : _onPlayerStateChanged = onPlayerStateChanged,
        _onPlayerPositionChanged = onPlayerPositionChanged,
        _onPlayerCompleted = onPlayerCompleted;

  final Function _onPlayerStateChanged;
  final Function _onPlayerPositionChanged;
  final Function _onPlayerCompleted;

  onPlayerStateChanged(PlayerState state) {
    if (_onPlayerStateChanged != null) {
      _onPlayerStateChanged(state);
    }
  }

  onPlayerPositionChanged(Duration position) {
    if (_onPlayerPositionChanged != null) {
      _onPlayerPositionChanged(position);
    }
  }

  onPlayerCompleted() {
    if (_onPlayerCompleted != null) {
      _onPlayerCompleted();
    }
  }
}
