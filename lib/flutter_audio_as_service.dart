//
//  Flutter plugin for audio playback on Android
//  Created by Karol Wąsowski (karol@tailosive.net) on June 23rd 2019
//  Licensed under the BSD License
//

import 'dart:async';
import 'package:flutter/services.dart';

import 'AudioInfoClass.dart';

/// A library to simply start an Android audio service with notification and control it
class FlutterAudioAsService {
  static const MethodChannel _nativeChannel =
      const MethodChannel("AudioService");
  static AudioPlayerListener _audioListener;

  // invoke Flutter from 
  
  /// Lets the service send callbacks to Flutter ex. for interface updates.
  /// It's important to remove listeners in onDestroy() (not implemented)
  static void setListeners(AudioPlayerListener listener) {
    _audioListener = listener;

    _nativeChannel.setMethodCallHandler(_methodCallHandler);
  }

  static Future<dynamic> _methodCallHandler(MethodCall call) {
    switch (call.method) {
      case "onPlayerStateChanged":
        switch (call.arguments) {
          case "idle":
            _audioListener.onPlayerStateChanged(PlayerState.idle);
            break;

          case "buffering":
            _audioListener.onPlayerStateChanged(PlayerState.loading);
            break;

          case "playing":
            _audioListener.onPlayerStateChanged(PlayerState.playing);
            break;

          case "paused":
            _audioListener.onPlayerStateChanged(PlayerState.paused);
            break;
        }
        break;

      case "onPlayerPositionChanged":
        _audioListener
            .onPlayerPositionChanged(Duration(milliseconds: call.arguments));
        break;

      case "onPlayerCompleted":
        _audioListener.onPlayerCompleted();
        break;

      // TODO: case launch app runApp() try this 

      default:
        print("ERROR: method not implemented");
        break;
    }

    return null;
  }

  // invoke native methods

  /// Starts the service, playback and sends a notification with given details
  /// This command has to be run before any other. The service will stop on itself when playback is done
  /// For more details about AudioInfo see the corresponding doc section
  static Future<void> init(AudioInfo details) async {
    String checkIfNull(String toCheck) {
      if (toCheck == null) {
        return "theGivenResourceIsNull";
      } else {
        return toCheck;
      }
    }

    await _checkIfBound();
    await _nativeChannel.invokeMethod("startService", {
      "title": details.title,
      "channel": details.artist,
      "url": details.url,
      "albumCoverFallback": checkIfNull(details.albumCoverFallback),
      "albumCoverUrl": checkIfNull(details.albumCoverUrl),
      "appIcon": checkIfNull(details.appIcon),
    });
  }

  /// Stops and destroys the service. [init()] has to be run after this one, if you want to start playback again
  /// This also runs [AudioPlayerListener.onPlayerCompleted()] to free resources
  static Future<void> stop() async {
    await _checkIfBound();
    await _nativeChannel.invokeMethod("stop");
  }

  /// Will pause the player. If player already paused will do nothing
  static Future<void> pause() async {
    await _checkIfBound();
    await _nativeChannel.invokeMethod("pause");
  }

  /// Will resume playback (only if already loaded earlier and paused afterwards). If already playing will do nothing
  static Future<void> resume() async {
    await _checkIfBound();
    await _nativeChannel.invokeMethod("resume");
  }

  /// Seeks by the specified time. Seeks forward  when a positive duration given and backwards if negative
  static Future<void> seekBy(Duration duration) async {
    await _checkIfBound();
    await _nativeChannel.invokeMethod("seekBy", {
      "seekByInMs": duration.inMilliseconds,
    });
  }

  /// Returns a Duration() with the current audio's length. Returns 0 if no audio is loaded
  static Future<Duration> getAudioLength() async {
    await _checkIfBound();
    dynamic audioLength = await _nativeChannel.invokeMethod("getAudioLength");
    return Duration(milliseconds: audioLength);
    // returns milliseconds
  }

  /// Seeks to a specified positions
  static Future<void> seekTo(Duration seekTo) async {
    await _checkIfBound();
    await _nativeChannel.invokeMethod("seekTo", {
      "seekToInMs": seekTo.inMilliseconds,
    });
  }

  static Future<void> unbind() async {
    await _checkIfBound();
    await _nativeChannel.invokeMethod("unBind");
  }

  static Future<void> _checkIfBound() async {
    await _nativeChannel.invokeMethod("checkIfBound");
  }

  static Future<String> get platformVersion async {
    final String version = await _nativeChannel.invokeMethod('getPlatformVersion');
    return version;
  }
}

/// Player states returned by the [AudioPlayerListener.onPlayerStateChanged()] callback
enum PlayerState { idle, loading, playing, paused }

/// A class to simplify the usage of callbacks. Specify the needed callbacks and pass the listener to
/// [FlutterAudioAsService.setListeners()]
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

  /// Runs whenever PlayerState changes ex. audio paused, resumed, stopped, loading, idle.
  /// Useful for making dynamic play/pause buttons (example app will include later) or having different behaviour for certain
  /// UI elements in different player states
  onPlayerStateChanged(PlayerState state) {
    if (_onPlayerStateChanged != null) {
      _onPlayerStateChanged(state);
    }
  }

  /// Runs whenever there is progress on audio playback. Useful to update a progress bar
  onPlayerPositionChanged(Duration position) {
    if (_onPlayerPositionChanged != null) {
      _onPlayerPositionChanged(position);
    }
  }

  /// Runs when the given audio source finishes. Could be used to implement playlists (plugin may include
  /// playlist somewhere in the future)
  onPlayerCompleted() {
    if (_onPlayerCompleted != null) {
      _onPlayerCompleted();
    }
  }
}