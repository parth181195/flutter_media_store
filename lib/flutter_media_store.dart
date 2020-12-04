import 'dart:async';
import 'dart:typed_data';
import 'package:flutter/services.dart';

class MediaStore {
  static const MethodChannel _channel = const MethodChannel('com.parth181195');

  /// Save the PNG，JPG，JPEG image or video located at [path] to the local device media gallery.
  static Future brodcastImage(Uint8List path) async {
    assert(path != null);
    final result = await _channel.invokeMethod('flutter_media_store', path);
    return result;
  }
}
