//
//  Flutter plugin for audio playback on Android
//  Created by Karol Wąsowski (karol@tailosive.net) on June 23rd 2019
//  Licensed under GPLv3
//

import 'dart:async';
import 'package:flutter/services.dart';


class FlutterAudioAsService {
  static const MethodChannel nativeChannel =
  const MethodChannel("AudioService");

  static Future<void> init(String title, String channel, String url, String albumCover, String appIcon) async {
    await nativeChannel.invokeMethod("startService", {
      "title": title,
      "channel": channel,
      "url": url,
      "albumCover": albumCover,
      "appIcon": appIcon,
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

  static Future<dynamic> getAudioLength() {
    return nativeChannel.invokeMethod("getAudioLength");
    // returns miliseconds
  }

  static Future<void> seekTo(Duration seekTo) async {
    await nativeChannel.invokeMethod("seekTo", {
      "seekToInMs": seekTo.inMilliseconds,
    });
  }
}
