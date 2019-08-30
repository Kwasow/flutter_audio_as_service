//
//  Flutter plugin for audio playback on Android
//  Created by Karol Wąsowski (karol@tailosive.net) on June 23rd 2019
//  Licensed under the BSD License
//

import 'package:flutter/material.dart';
import 'package:flutter_audio_as_service/flutter_audio_as_service.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Android Audio playback as service'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              RaisedButton(
                child: Text("Connect to service, load audio, start playback"),
                onPressed: () async {
                  await FlutterAudioAsService.init(
                    "Title",
                    "Author",
                    "https://feeds.soundcloud.com/stream/655083446-tailosivecast-ep-054-series-finale.m4a",
                    "ic_launcher",  // IMPORTANT!
                    "app_icon",     // see README for details about usage
                  );
                },
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  IconButton(
                    icon: Icon(Icons.play_arrow),
                    onPressed: () async {
                      await FlutterAudioAsService.resume();
                    },
                  ),
                  IconButton(
                    icon: Icon(Icons.pause),
                    onPressed: () async {
                      await FlutterAudioAsService.pause();
                    },
                  ),
                  IconButton(
                    icon: Icon(Icons.stop),
                    onPressed: () async {
                      await FlutterAudioAsService.stop();
                    },
                  ),
                ],
              ),
              RaisedButton(
                child: Text("Seek by 30s"),
                onPressed: () async {
                  await FlutterAudioAsService.seekBy(Duration(seconds: 30));
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}