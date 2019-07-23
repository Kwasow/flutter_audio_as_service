import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
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
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              RaisedButton(
                child: Text("Load audio"),
                onPressed: () async {
                  await FlutterAudioAsService.loadAudio(
                    "https://feeds.soundcloud.com/stream/655083446-tailosivecast-ep-054-series-finale.m4a"
                  );
                },
              ),
              Row(
                children: <Widget>[
                  IconButton(
                    icon: Icon(Icons.play_arrow),
                    onPressed: () async {
                      await FlutterAudioAsService.playAudio();
                    },
                  ),
                  IconButton(
                    icon: Icon(Icons.pause),
                    onPressed: () async {
                      await FlutterAudioAsService.pauseAudio();
                    },
                  ),
                  IconButton(
                    icon: Icon(Icons.stop),
                    onPressed: () async {
                      await FlutterAudioAsService.stopAudio();
                    },
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
