#import "FreightPlugin.h"
#import <MapManager/MapManager.h>
#import <AMapFoundationKit/AMapFoundationKit.h>
#import <AMapLocationKit/AMapLocationKit.h>


@interface FreightPlugin ()

@property(nonatomic, retain) AMapLocationManager *locationManager;

@end

@implementation FreightPlugin {
    MapService *service;
}
- (instancetype)init {
    self = [super init];

    service = [[MapService alloc] init];

    return self;
}

+ (void)registerWithRegistrar:(NSObject <FlutterPluginRegistrar> *)registrar {
    FlutterMethodChannel *channel = [FlutterMethodChannel
            methodChannelWithName:@"freight"
                  binaryMessenger:[registrar messenger]
    ];
    FreightPlugin *instance = [[FreightPlugin alloc] init];
    [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall *)call result:(FlutterResult)result {
    if ([@"getPlatformVersion" isEqualToString:call.method]) {
        result([@"iOS版本: " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
    } else if ([@"init" isEqualToString:call.method]) {
        NSLog(@"[Freight][iOS].[init]");
        [service openServiceWithAppId:call.arguments[@"appId"] appSecurity:call.arguments[@"appSecurity"] enterpriseSenderCode:call.arguments[@"eeCode"] environment:call.arguments[@"env"] listener:^(id model, NSError *error) {
            if (error) {
                NSLog(@"开启服务失败: %@", error);
            } else {
                NSDictionary *dict = model;
                NSLog(@"开启服务成功: %@", dict[@"code"]);
                NSLog(@"开启服务成功: %@", dict[@"message"]);
                result(dict);
            }
        }];
    } else if ([@"start" isEqualToString:call.method]) {
        NSLog(@"[Freight][iOS].[start]");
        [service startLocationWithShippingNoteInfos:call.arguments[@"ships"] listener:^(id model, NSError *error) {
            if (error) {
                NSLog(@"启用定位失败: %@", error);
            } else {
                NSDictionary *dict = model;
                NSLog(@"启用定位成功: %@", dict[@"code"]);
                NSLog(@"启用定位成功: %@", dict[@"message"]);
                result(dict);
            }
        }];
    } else if ([@"stop" isEqualToString:call.method]) {
        NSLog(@"[Freight][iOS].[stop]");
        [service stopLocationWithShippingNoteInfos:call.arguments[@"ships"] listener:^(id model, NSError *error) {
            if (error) {
                NSLog(@"结束定位失败: %@", error);
            } else {
                NSDictionary *dict = model;
                NSLog(@"结束定位成功: %@", dict[@"code"]);
                NSLog(@"结束定位成功: %@", dict[@"message"]);
                result(dict);
            }
        }];
    } else if ([@"initAmap" isEqualToString:call.method]) {
        NSLog(@"[Freight][iOS].[initAmap]");
        [AMapServices sharedServices].apiKey = call.arguments[@"key"];

        self.locationManager = [[AMapLocationManager alloc] init];
        // 带逆地理信息的一次定位（返回坐标和地址信息）
        [self.locationManager setDesiredAccuracy:kCLLocationAccuracyBest];
        // 定位超时时间，最低2s，此处设置为10s
        self.locationManager.locationTimeout = 10;
        // 逆地理请求超时时间，最低2s，此处设置为10s
        self.locationManager.reGeocodeTimeout = 10;

        result(@YES);
    } else if ([@"getLocation" isEqualToString:call.method]) {
        NSLog(@"[Freight][iOS].[getLocation]");
        [self.locationManager requestLocationWithReGeocode:NO completionBlock:^(CLLocation *location, AMapLocationReGeocode *regeocode, NSError *error) {
            if (error) {
                NSLog(@"获取定位信息失败: %zd, %@", error.code, error.localizedDescription);
            } else {
                result(@{
                        @"latitude": @(location.coordinate.latitude),
                        @"longitude": @(location.coordinate.longitude),
                });
            }
        }];
    } else {
        result(FlutterMethodNotImplemented);
    }
}

@end
