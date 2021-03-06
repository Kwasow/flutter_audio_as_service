## 0.3.1

 - **Renamed plugin to flutter_audio_service** - setup is still the same

## 0.3.0

 - Bug fixes
 - Implementation changes
 - Tested offline audio and albumCover source
 - Added abbility to set a different albumCover for each song
 - Updated everything to be on par with how things are done now

## 0.2.1
 
 - Stability improvements

## 0.2.0

 - The service now gets bound to the flutter app and unbinds automatically. This improves background performance and stability

## 0.1.3

 - Updated documentation

## 0.1.2

 - Updated example
 - Fixed seekTo() throwing error

## 0.1.1

 - Prevented running functions on null object references
 - If player initialized with source that is already playing player won't start over - it will keep playing
 - getAudioLength() now returns 0 if player is idle

## 0.1.0

Minor changes, improved consistency

## 0.0.4

Added listeners to player state changes, audio position changes and audio completed playing. This will be released as 0.1.0 after some testing.

## 0.0.3

Adds missing feature - set notification album cover, and makes the plugin release-ready (beta)

## 0.0.2

Offers basic functionality with support for starting a service and notification controls.

## 0.0.1

Offers basic playback functionality without starting a service.
