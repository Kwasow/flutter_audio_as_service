# flutter_audio_as_service 0.0.1


Flutter plugin for audio playback on Android
Created by Karol Wąsowski (karol@tailosive.net) on June 23rd 2019
Licensed under GPLv3.

Pull requests are welcome.


## Usage

1. Before you begin
Add this permission to AndridManifest.xml:
`<uses-permission android:name="android.permission.WAKE_LOCK" />`
Make sure your app uses Java8 by adding this code into your app-level build.gradle into the android section:
```compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
```
Enable androidx support in your app.
Add the service to AndroidMainfest.xml
```
<manifest>
  ...
  <application>
    ...
    ...
    <service android:name="net.tailosive.flutter_audio_as_service.AudioService"/>
  </application>
</manifest>
```

2. Media notifiaction
If you want to use media notification add this to your AndroidManifest.xml:
`<permission android:name="android.permission.MEDIA_CONTENT_CONTROL" />`

3. Audio Controls
