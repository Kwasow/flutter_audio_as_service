//
//  Flutter plugin for audio playback on Android
//  Created by Karol Wąsowski (karol@tailosive.net) on June 23rd 2019
//  Licensed under the BSD License
//

package net.tailosive.flutter_audio_as_service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;

import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import io.flutter.plugin.common.MethodChannel;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static net.tailosive.flutter_audio_as_service.FlutterAudioAsServicePlugin.pluginRegistrar;
import static net.tailosive.flutter_audio_as_service.FlutterAudioAsServicePlugin.methodChannel;
import static net.tailosive.flutter_audio_as_service.FlutterAudioAsServicePlugin.isBound;
import static net.tailosive.flutter_audio_as_service.FlutterAudioAsServicePlugin.audioService;

public class AudioService extends Service {
  private final IBinder iBinder = new LocalBinder();

  Context context = this;
  private Handler handler;

  private static Cache cache;
  public SimpleExoPlayer player;
  private PlayerNotificationManager playerNotificationManager;
  private String PLAYBACK_CHANNEL_ID = "playback channel";
  private int PLAYBACK_NOTIFICATION_ID = 1;
  MediaSessionCompat mediaSession;
  private String MEDIA_SESSION_TAG = "AudioPlaybackSession";
  MediaSessionConnector mediaSessionConnector;

  private String nowPlayingUrl;

  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Override
  public int onStartCommand(final Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);

    final String title = intent.getStringExtra("title");
    final String channel = intent.getStringExtra("channel");
    final String url = intent.getStringExtra("url");
    final String imageUrl = intent.getStringExtra("bigIcon");
    nowPlayingUrl = url;

    player = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());

    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, "TailosiveAudio"));

    CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(getCache(this), dataSourceFactory);
    MediaSource audioSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory)
            .createMediaSource(Uri.parse(url));

    SimpleExoPlayer.EventListener audioEventListener = new SimpleExoPlayer.EventListener() {
      @Override
      public int hashCode() {
        return super.hashCode();
      }

      @Override
      public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == SimpleExoPlayer.STATE_IDLE) {
          methodChannel.invokeMethod("onPlayerStateChanged", "idle");
        } else if (playbackState == SimpleExoPlayer.STATE_BUFFERING) {
          methodChannel.invokeMethod("onPlayerStateChanged", "buffering");
          // add buffering string
        } else if (playbackState == SimpleExoPlayer.STATE_ENDED) {
          methodChannel.invokeMethod("onPlayerCompleted", null);
          methodChannel.invokeMethod("onPlayerStateChanged", "idle");
          stopSelf();
        } else if (playWhenReady) {
          methodChannel.invokeMethod("onPlayerStateChanged", "playing");
          // add playing
        } else {
          methodChannel.invokeMethod("onPlayerStateChanged", "paused");
          // add paused
        }
      }
    };

    player.addListener(audioEventListener);
    player.prepare(audioSource);
    player.setPlayWhenReady(true);

    playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            context,
            PLAYBACK_CHANNEL_ID,
            R.string.playback_channel_name,
            R.string.channel_description,
            PLAYBACK_NOTIFICATION_ID,
            new PlayerNotificationManager.MediaDescriptionAdapter() {
              @Override
              public String getCurrentContentTitle(Player player) {
                return title;
              }

              @Nullable
              @Override
              public PendingIntent createCurrentContentIntent(Player player) {
                /*
                System.out.println(getApplicationContext());
                Intent intent = new Intent(context, getApplication().getClass());
                return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                */
                return null;
              }

              @Nullable
              @Override
              public String getCurrentContentText(Player player) {
                return channel;
              }

              @Nullable
              @Override
              public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                File file = new File(getCacheDir().toString() + "/" + title);

                return BitmapFactory.decodeFile(file.toString());
              }
            },
            new PlayerNotificationManager.NotificationListener() {
              @Override
              public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                isBound = false;
                audioService = null;
                stopSelf();
              }

              @Override
              public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                startForeground(notificationId, notification);
              }
            }
    );

    if (!(intent.getStringExtra("appIcon") == null)) {
      playerNotificationManager.setSmallIcon(pluginRegistrar.context().getResources().getIdentifier(
              intent.getStringExtra("appIcon"),
              "drawable",
              pluginRegistrar.context().getPackageName()
      ));
    }
    playerNotificationManager.setUseStopAction(true);
    playerNotificationManager.setRewindIncrementMs(30000);  // 30s
    playerNotificationManager.setFastForwardIncrementMs(30000);

    playerNotificationManager.setPlayer(player);

    mediaSession = new MediaSessionCompat(
            context,
            MEDIA_SESSION_TAG
    );
    mediaSession.setActive(true);
    playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());
    mediaSessionConnector = new MediaSessionConnector(mediaSession);

    mediaSessionConnector.setPlayer(player);

    handler = new Handler();
    handler.postDelayed(new Runnable() {
        @Override
        public void run() {
            if (!(player == null)) {
                if (player.getPlayWhenReady()) {
                  methodChannel.invokeMethod("onPlayerPositionChanged", player.getCurrentPosition());
                }
                handler.postDelayed(this, 500);
            }
        }
    }, 500);

    Observable<File> observable = Observable.create(new ObservableOnSubscribe<File>() {
      @Override
      public void subscribe(ObservableEmitter<File> emitter) {
        String cache = getCacheDir().toString();
        File imageFile = new File(cache + "/" + title);

        Thread networkThread = new Thread(() -> {
          try {
            URL remoteFile = new URL(imageUrl);

            URLConnection connection = remoteFile.openConnection();
            connection.connect();

            InputStream inputStream = new BufferedInputStream(remoteFile.openStream(), 1024);
            OutputStream outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[1024];

            int count = 0;
            while ((count = inputStream.read(buffer)) > 0) {
              outputStream.write(buffer, 0, count);
            }

            outputStream.flush();
            inputStream.close();
            outputStream.close();

            emitter.onNext(imageFile);

          } catch (Exception e) {
            emitter.onError(e);
          }

          emitter.onComplete();
        });

        networkThread.start();
      }
    });

    Observer<File> downloadObserver = new Observer<File>() {
      @Override
      public void onSubscribe(Disposable d) {
        Log.d("TailosiveAudio", "Downloading album cover");
      }

      @Override
      public void onNext(File file) {
        playerNotificationManager.invalidate();
      }

      @Override
      public void onError(Throwable e) {
        Log.e("TailosiveAudio", e.toString());
      }

      @Override
      public void onComplete() {
        Log.d("TailosiveAudio", "Notification posted");
      }
    };

    observable.subscribe(downloadObserver);

    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    handler = null;
    nowPlayingUrl = "";

    mediaSession.release();
    mediaSessionConnector.setPlayer(null);
    playerNotificationManager.setPlayer(null);
    player.release();
    player = null;
  }

  public class LocalBinder extends Binder {
    AudioService getService() {
      return AudioService.this;
    }
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return iBinder;
  }

  @Override
  public boolean onUnbind(Intent intent) {
    return super.onUnbind(intent);
  }

  public void pauseAudio() {
    player.setPlayWhenReady(false);
  }

  public void resumeAudio() {
    player.setPlayWhenReady(true);
  }

  public void serviceStop() {
    stopSelf();
  }

  public String getUrlPlaying() {
    return nowPlayingUrl;
  }

  public long getPlayerAudioLength() {
    return player.getDuration();
  }

  public void seekBy(int seekByInMs) {
    player.seekTo(player.getCurrentPosition() + seekByInMs);
  }

  @Deprecated
  private static synchronized Cache getCache(Context context) {
    if (cache == null) {
      File cacheDirectory = new File(context.getCacheDir(), "audio");
      cache = new SimpleCache(cacheDirectory, new LeastRecentlyUsedCacheEvictor(300 * 1024 * 1024));
    }
    return cache;
  }
}
