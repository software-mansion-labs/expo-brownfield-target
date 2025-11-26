internal import Expo
import Network
internal import React
internal import ReactAppDependencyProvider
import UIKit

/* Objective-C bridging needed for automatic initialization using
  `load()` in ReactNativeLoader.m */
@objc(ReactNativeHostManager)
public class ReactNativeHostManager: NSObject {
  @objc public static let shared = ReactNativeHostManager()

  private var reactNativeDelegate: ExpoReactNativeFactoryDelegate?
  private var reactNativeFactory: RCTReactNativeFactory?
  private var expoDelegate: ExpoAppDelegate?

  /*
   * Initializes the React Native host manager shared instance.
   * This method should be automatically called by the ReactNativeLoader.m file.
   * Prevents multiple initializations of the React Native host manager shared instance.
   */
  @objc public func initialize() {
    /* Prevent multiple initializations */
    guard reactNativeDelegate == nil else { return }

    let delegate = ReactNativeDelegate()
    let factory = ExpoReactNativeFactory(delegate: delegate)
    delegate.dependencyProvider = RCTAppDependencyProvider()

    reactNativeDelegate = delegate
    reactNativeFactory = factory

    expoDelegate = ExpoAppDelegate()
    expoDelegate?.bindReactNativeFactory(factory)

    /* Ensure this won't get stripped by the Swift compiler */
    let _ = ExpoModulesProvider()
  }

  /* 
   * Propagates delegate methods to ExpoAppDelegate.
   */
  public func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
  ) -> Bool {
    ((expoDelegate?.application(application, didFinishLaunchingWithOptions: launchOptions)) != nil)
  }

  /*
   * Loads and presents the React Native view.
   */
  public func loadView(
    moduleName: String,
    initialProps: [AnyHashable: Any]?,
    launchOptions: [AnyHashable: Any]?
  ) -> UIView {
    return
      (expoDelegate?
      .recreateRootView(
        withBundleURL: nil,
        moduleName: moduleName,
        initialProps: initialProps,
        launchOptions: launchOptions
      ))!
  }
}

class ReactNativeDelegate: ExpoReactNativeFactoryDelegate {
  /* Extension point for config-plugins */
  override func sourceURL(for bridge: RCTBridge) -> URL? {
    /* Needed to return the correct URL for expo-dev-client */
    bridge.bundleURL ?? bundleURL()
  }

  override func bundleURL() -> URL? {
    #if DEBUG
      return RCTBundleURLProvider.sharedSettings().jsBundleURL(
        forBundleRoot: ".expo/.virtual-metro-entry")
    #else
      /* `main.jsbundle` isn't part of the main app bundle
        so we need to load it from the framework bundle */
      let frameworkBundle = Bundle(for: ReactNativeHostManager.self)
      return frameworkBundle.url(forResource: "main", withExtension: "jsbundle")
    #endif
  }
}
