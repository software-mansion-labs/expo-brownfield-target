import ExpoModulesCore

public class ExpoBrownfieldModule: Module {
  public func definition() -> ModuleDefinition {
    Name("ExpoBrownfieldModule")

    Function("popToNative") { (animated: Bool) in
      DispatchQueue.main.async {
        NotificationCenter.default.post(
          name: Notification.Name("popToNative"),
          object: nil,
          userInfo: ["animated": animated]
        )
      }
    }

    Function("setNativeBackEnabled") { (enabled: Bool) in
      DispatchQueue.main.async {
        NotificationCenter.default.post(
          name: Notification.Name("setNativeBackEnabled"),
          object: nil,
          userInfo: ["enabled": enabled]
        )
      }
    }
  }
}
