# flutter_audio_as_service 0.0.1


A plugin by Tailosive Development (@TailosiveDev) created by Karol Wąsowski (@KarolWasowski)
Licensed under GPLv3.

Pull requests are welcome.

The plugin is Android only as of now.


## Usage

1. Before you begin
Add this permission to AndridManifest.xml:
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>

Make sure your app uses Java8 by adding this code into your app-level build.gradle into the android section:
```gradle
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}
```

Enable androidx support in your app.

Add the service to AndroidMainfest.xml
```xml
<manifest>
  ...
  <application>
    ...
    ...
    <service 
      android:name="net.tailosive.flutter_audio_as_service.AudioService"/>
  </application>
</manifest>
```

2. Usage
To start playback initialize the player with the following:
```dart
FlutterAudioAsService.init("Title", "Author", audioSource, appIcon);
```
audioSource is a string - could be both a url or a file directory.
albumCover is a string - the name (path) to image asset
appIcon - sets the small notification icon

Playback is controlled with the following functions:
```dart
FlutterAudioAsService.pause();
FlutterAudioAsService.resume();
```

During playback you may also seek with:
```dart
FlutterAudioAsService.seekBy(Duration(seconds: 30));
FlutterAudioAsService.seekTo(Duration(minutes: 15, seconds: 47));
```

To stop playback and destroy the service run:
```dart
FlutterAudioAsService.stop();
```

3. Features
The plugin supports audio playback as an Android service with caching functionality included. It uses ExoPlayer with MediaSession API and rich notification controls.
