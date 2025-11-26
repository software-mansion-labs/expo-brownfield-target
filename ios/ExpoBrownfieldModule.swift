import ExpoModulesCore

public class ExpoBrownfieldModule: Module {
  public func definition() -> ModuleDefinition {
    Name("ExpoBrownfieldModule")

    Function("popToNative") {
      DispatchQueue.main.async {
        NotificationCenter.default.post(
          name: Notification.Name("popToNative"),
          object: nil
        )
      }
    }
  }
}
