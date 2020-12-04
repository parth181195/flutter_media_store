#import "FlutterMediaStorePlugin.h"
#if __has_include(<flutter_media_store/flutter_media_store-Swift.h>)
#import <flutter_media_store/flutter_media_store-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_media_store-Swift.h"
#endif

@implementation FlutterMediaStorePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterMediaStorePlugin registerWithRegistrar:registrar];
}
@end
