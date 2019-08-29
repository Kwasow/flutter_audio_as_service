# flutter_audio_as_service 0.0.1


Flutter plugin for audio playback on Android
Created by Karol Wąsowski (karol@tailosive.net) on June 23rd 2019
Licensed under GPLv3.

Pull requests are welcome.


## Usage

1. Before you begin
Add this permission to AndridManifest.xml:
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>

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
    <service 
      android:name=".AudioService"
      android:process="your.app.name.AudioService"
      android:enabled="true"
      android:exported="true"/>
  </application>
</manifest>
```