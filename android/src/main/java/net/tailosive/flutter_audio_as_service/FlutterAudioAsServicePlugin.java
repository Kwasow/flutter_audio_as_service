//
//  Flutter plugin for audio playback on Android
//  Created by Karol Wąsowski (wasowski02@gmail.com) on June 23rd 2019
//  Licensed under the BSD License
//

package net.tailosive.flutter_audio_as_service;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.util.Log;
import android.os.IBinder;

/** FlutterAudioAsServicePlugin */
public class FlutterAudioAsServicePlugin implements FlutterPlugin, MethodCallHandler {
  public static Context context;
  public static MethodChannel methodChannel;
  public static AudioService audioService;
  static Intent serviceIntent;
  public static boolean isBound = false;

  @Override
  public void onAttachedToEngine(FlutterPluginBinding binding) {
    Log.i("TailosiveAudio", "Using new plugin registration");

    methodChannel = new MethodChannel(binding.getFlutterEngine().getDartExecutor(), "AudioService");
    methodChannel.setMethodCallHandler(this);

    context = binding.getApplicationContext();
    serviceIntent = new Intent(context, AudioService.class);
  }

  @Override
  public void onDetachedFromEngine(FlutterPluginBinding flutterPluginBinding) {
    methodChannel = null;
    context = null;
    serviceIntent = null;
  }

  /** Plugin registration. */
  public static void registerWith(PluginRegistry.Registrar registrar) {
    methodChannel = new MethodChannel(registrar.messenger(), "AudioService");
    methodChannel.setMethodCallHandler(new FlutterAudioAsServicePlugin());

    context = registrar.activeContext();
    serviceIntent = new Intent(context, AudioService.class);
  }

  private ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      AudioService.LocalBinder binder = (AudioService.LocalBinder) service;
      audioService = binder.getService();
      isBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      isBound = false;
      audioService = null;
    }
  };

  public void onMethodCall(MethodCall call, Result result) {
    switch (call.method) {

      case "startService":
        // this really needs some work - it's to long as of now

        String title = call.argument("title");
        String channel = call.argument("channel");
        String url = call.argument("url");
        String smallIcon = call.argument("appIcon");
        String bigIcon = call.argument("albumCoverUrl");
        Log.d("TailosiveAudio", bigIcon);

        if (!(audioService == null)) {
          if (!(url.equals(audioService.getUrlPlaying()))) {
            audioService.serviceStop();

            if (isValidDrawableResource(context, smallIcon)) {
              serviceIntent.putExtra("appIcon", smallIcon);
            } else {
              serviceIntent.putExtra("appIcon", (String) null);
            }
/*
            if (isValidDrawableResource(pluginRegistrar.context(), bigIcon)) {
              serviceIntent.putExtra("bigIcon", bigIcon);
            } else {
              serviceIntent.putExtra("bigIcon", (String) null);
            }
*/
            serviceIntent.putExtra("bigIcon", bigIcon);
            serviceIntent.putExtra("title", title);
            serviceIntent.putExtra("channel", channel);
            serviceIntent.putExtra("url", url);
            context.startService(serviceIntent);
          } else {
            Log.i("Audio", "This audio is already playing, not starting again");
          }
        } else {
          if (isValidDrawableResource(context, smallIcon)) {
            serviceIntent.putExtra("appIcon", smallIcon);
          } else {
            serviceIntent.putExtra("appIcon", (String) null);
          }
/*
          if (isValidDrawableResource(pluginRegistrar.context(), bigIcon)) {
            serviceIntent.putExtra("bigIcon", bigIcon);
          } else {
            serviceIntent.putExtra("bigIcon", (String) null);
          }
*/          serviceIntent.putExtra("bigIcon", bigIcon);
          serviceIntent.putExtra("title", title);
          serviceIntent.putExtra("channel", channel);
          serviceIntent.putExtra("url", url);
          context.startService(serviceIntent);
        }

        result.success(null);
        break;

      case "stop":
        if (!(audioService == null)) {
          context.unbindService(serviceConnection);

          audioService.serviceStop();
        }

        result.success(null);
        break;

      case "pause":
        if (!(audioService == null)) {
          audioService.pauseAudio();
        }

        result.success(null);
        break;

      case "resume":
        if (!(audioService == null)) {
          audioService.resumeAudio();
        }

        result.success(null);
        break;

      case "seekBy":
        int seekByInMs = call.argument("seekByInMs");

        if (!(audioService == null)) {
          audioService.seekBy(seekByInMs);
        }

        methodChannel.invokeMethod("onPlayerPositionChanged", audioService.player.getCurrentPosition());
        result.success(null);
        break;

      case "getAudioLength":
        if (audioService.player == null) {
          result.success(0);
        } else {
          result.success(audioService.getPlayerAudioLength());
        }
        break;

      case "seekTo":
        long seekTo = 0;
        int seekToInMs = call.argument("seekToInMs");

        if (!(audioService == null)) {
          audioService.player.seekTo(seekTo + seekToInMs);
        }

        methodChannel.invokeMethod("onPlayerPositionChanged", audioService.player.getCurrentPosition());
        result.success(null);
        break;

      case "unBind":
        if (isBound) {
          context.unbindService(serviceConnection);
        }
        result.success(null);
        break;

      case "checkIfBound": {
        if (!isBound) {
          context.bindService(serviceIntent, serviceConnection, Context.BIND_IMPORTANT);
        }
        result.success(null);
        break;
      }

      case "getPlatformVersion": {
        result.success("Android " + android.os.Build.VERSION.RELEASE);
      }

      default: {
        Log.e("Audio", "Wrong method call");
        result.notImplemented();
        break;
      }
    }
  }

  private static boolean isValidDrawableResource(Context context, String name) {
    int resourceId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    if (resourceId == 0) {
      return false;
    }
    return true;
  }
}
