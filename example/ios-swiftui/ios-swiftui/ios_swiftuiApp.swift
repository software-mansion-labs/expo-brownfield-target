import SwiftUI
import MyBrownfieldApp

@main
struct ios_swiftuiApp: App {
  @UIApplicationDelegateAdaptor var delegate: BrownfieldAppDelegate

  var body: some Scene {
    WindowGroup {
      ContentView()
    }
  }
}
