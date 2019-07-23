import 'dart:async';

import 'package:flutter/services.dart';

class FlutterAudioAsService {
  static const MethodChannel _channel =
      const MethodChannel('flutter_audio_as_service');

  static Future<String> get returnText async {
    final String result = await _channel.invokeMethod('returnText');
    return result;
  }

  static Future<void> loadAudio(String url) async {
    await _channel.invokeMethod('loadAudio');
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
}
