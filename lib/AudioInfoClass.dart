class AudioInfo {
  String title;
  String artist;
  String url;
  String localFile;
  String albumCoverUrl;
  String albumCoverFallback;
  String appIcon;

  /// Both url and albumCoverUrl can be files and remote urls.
  /// If using a local file please remember to add `file:///` before the path!
  /// 
  /// appIcon can be set to null to use default values. Usage with values given:
  ///  - place the desired .png file inside android/app/src/main/res/drawable/
  ///  - if filename is app_icon.png then set appIcon value to be "app_icon"
  AudioInfo(
    this.title,
    this.artist,
    this.url,
    this.albumCoverUrl,
    this.appIcon
  );
}