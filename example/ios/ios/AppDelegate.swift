import UIKit
import MyBrownfieldApp

@main
class AppDelegate: BrownfieldAppDelegate {
  var window: UIWindow?

  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    // TODO: Adopt to UIScene lifecycle for the future
    let superResult = super.application(application, didFinishLaunchingWithOptions: launchOptions)
    window = UIWindow(frame: UIScreen.main.bounds)

    // CASE: Simple use case
//        let viewController = ReactNativeViewController(moduleName: "main")
//        window?.rootViewController = viewController
    // CASE: Navigation use case
    let viewController = ViewController()
    let navigationController = UINavigationController(rootViewController: viewController)
    window?.rootViewController = navigationController

    window?.makeKeyAndVisible()

    return true && superResult
  }
}
