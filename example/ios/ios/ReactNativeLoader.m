//
//  ReactNativeLoader.m
//  ios
//
//  Created by Patryk Mleczek on 11/21/25.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import <MyBrownfieldApp/MyBrownfieldApp-Swift.h>

@interface ReactNativeHostManagerLoader : NSObject
@end

@implementation ReactNativeHostManagerLoader

+ (void)load {
    [[NSNotificationCenter defaultCenter] addObserverForName:UIApplicationDidFinishLaunchingNotification
                                                      object:nil
                                                       queue:[NSOperationQueue mainQueue]
                                                  usingBlock:^(NSNotification * _Nonnull note) {
        [[ReactNativeHostManager shared] initialize];
    }];
}

@end
