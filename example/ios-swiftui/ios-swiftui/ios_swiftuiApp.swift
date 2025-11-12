//
//  ios_swiftuiApp.swift
//  ios-swiftui
//
//  Created by Patryk Mleczek on 11/12/25.
//

import SwiftUI
import MyBrownfieldApp

@main
struct ios_swiftuiApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        MyBrownfieldApp.ReactNativeHostManager.shared.initialize()
        return true
    }
}
