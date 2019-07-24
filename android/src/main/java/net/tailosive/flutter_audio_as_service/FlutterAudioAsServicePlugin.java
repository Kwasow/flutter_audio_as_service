//
//  Flutter plugin for audio playback on Android
//  Created by Karol Wąsowski (karol@tailosive.net) on June 23rd 2019
//  Licensed under GPLv3
//

package net.tailosive.flutter_audio_as_service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;


import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class FlutterAudioAsServicePlugin extends FlutterActivity implements MethodCallHandler {

    AudioService audioService;
    boolean serviceConnected = false;
    MethodChannel.Result keepResult = null;

    /** Plugin registration. */
    public static void registerWith(Registrar registrar) {
      final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_audio_as_service");
      channel.setMethodCallHandler(new FlutterAudioAsServicePlugin());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new MethodChannel(getFlutterView(), "_channel").setMethodCallHandler(this::onMethodCall);
    }

    private void connectToService() {
        if (!serviceConnected) {
            Intent service = new Intent(this, AudioService.class);
            startService(service);
            bindService(service, connection, Context.BIND_AUTO_CREATE);
        } else {
            Log.i("Audio", "Service already connected");
            if (keepResult != null) {
                keepResult.success(null);
                keepResult = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Context context = getApplicationContext();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Toast.makeText(context, "This app needs to be whitelisted", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + packageName));
                context.startActivity(intent);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        serviceConnected = false;
    }

    private ServiceConnection connection = new ServiceConnection() {

      @Override
      public void onServiceConnected(ComponentName className,
                                      IBinder service) {
          AudioService.AudioServiceBinder binder = (AudioService.AudioServiceBinder) service;
          audioService = binder.getService();
          serviceConnected = true;
          Log.i("Audio", "Service connected");
          if (keepResult != null) {
              keepResult.success(null);
              keepResult = null;
          }
      }

      @Override
      public void onServiceDisconnected(ComponentName arg0) {
          serviceConnected = false;
          Log.i("Audio", "Service disconnected");
      }
    };

    @Override
    public void onMethodCall(MethodCall call, Result result) {
      try {
          if (call.method.equals("connect")) {
              connectToService();
              keepResult = result;
          } else if (serviceConnected) {
            switch (call.method) {
              case "loadAudio":
                String url = call.argument("url");
                audioService.startService();
                audioService.loadAudio(url);
                break;
        
              case "playAudio":
                audioService.playAudio();
                break;
        
              case "pauseAudio":
                audioService.pauseAudio();
                break;
        
              case "stopAudio":
                audioService.stopAudio();
                break;
        
              case "getDuration":
                result.success(audioService.getDuration());
                break;
        
              case "getCurrentPosition":
                result.success(audioService.getCurrentPosition());
                break;
        
              case "seekTo":
                int positionInMs = call.argument("positionInMs");
                audioService.seekTo(positionInMs);
                break;
              
              case "seekBy":
                int seekByInMs = call.argument("seekByInMs");
                audioService.seekTo(audioService.getCurrentPosition() + seekByInMs);
                break;
        
              default:
                Log.e("Audio", "Wrong method call");
                result.notImplemented();
                break;
            }
          } else {
              result.error(null, "App not connected to service", null);
          }
      } catch (Exception e) {
          result.error(null, e.getMessage(), null);
      }
    }
}