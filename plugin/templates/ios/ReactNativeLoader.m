#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import <MyBrownfieldApp/MyBrownfieldApp-Swift.h>

@interface ReactNativeHostManagerLoader : NSObject
@end

@implementation ReactNativeHostManagerLoader

+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        UIApplication *application = [UIApplication sharedApplication];
        if (application && application.applicationState != UIApplicationStateBackground) {
            [[ReactNativeHostManager shared] initialize];
        } else {
            __block id<NSObject> observer = nil;
            
            observer = [[NSNotificationCenter defaultCenter] addObserverForName:UIApplicationDidFinishLaunchingNotification
                                                                          object:nil
                                                                           queue:[NSOperationQueue mainQueue]
                                                                      usingBlock:^(NSNotification * _Nonnull note) {
                [[ReactNativeHostManager shared] initialize];
                if (observer) {
                    [[NSNotificationCenter defaultCenter] removeObserver:observer];
                }
            }];
        }
    });
}

@end
