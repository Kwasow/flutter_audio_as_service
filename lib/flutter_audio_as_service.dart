//
//  Flutter plugin for audio playback on Android
//  Created by Karol Wąsowski (karol@tailosive.net) on June 23rd 2019
//  Licensed under GPLv3
//

import 'dart:async';

import 'package:flutter/services.dart';

class FlutterAudioAsService {
  static const MethodChannel _channel =
      const MethodChannel('flutter_audio_as_service');

  static Future<void> init() async {
    await _channel.invokeMethod('startService');
  }

  static Future<void> loadAudio(String url) async {
    await _channel.invokeMethod('loadAudio', {"url": url});
  }

  static Future<void> playAudio() async {
    await _channel.invokeMethod('playAudio');
  }

  static Future<void> pauseAudio() async {
    await _channel.invokeMethod('pauseAudio');
  }

  static Future<void> stopAudio() async {
    await _channel.invokeMethod('stopAudio');
  }

  static Future<int> getDuration() async {
    return await _channel.invokeMethod('getDuration');
  }

  static Future<int> getCurrentPosition() async {
    int position = await _channel.invokeMethod('getCurrentPosition');
    return position;
  }

  static Future<void> seekTo(int positionInMs) async {
    await _channel.invokeMethod('seekTo', {"positionInMs": positionInMs});
  }

  static Future<void> seekBy(int seekByInMs) async {
    await _channel.invokeMethod('seekBy', {"seekByInMs": seekByInMs});
  }
}
