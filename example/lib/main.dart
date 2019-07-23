import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_audio_as_service/flutter_audio_as_service.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  String _result = "Awaiting";

  @override
  void initState() {
    super.initState();
    getTheSomething();
  }

  Future<void> getTheSomething() async {
    String result;

    try {
      result = await FlutterAudioAsService.returnText;
    } on PlatformException {
      result = "Something went wrong";
    }

    if (!mounted) return;

    setState(() {
      _result = result;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Running on: $_result\n'),
        ),
      ),
    );
  }
}
