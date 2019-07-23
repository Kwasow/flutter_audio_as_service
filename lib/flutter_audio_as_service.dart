import 'dart:async';

import 'package:flutter/services.dart';

class FlutterAudioAsService {
  static const MethodChannel _channel =
      const MethodChannel('flutter_audio_as_service');

  static Future<String> get returnText async {
    final String result = await _channel.invokeMethod('returnText');
    return result;
  }
}
