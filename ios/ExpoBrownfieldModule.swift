import ExpoModulesCore

public class ExpoBrownfieldModule: Module {
  public func definition() -> ModuleDefinition {
    Name("ExpoBrownfieldModule")

    Constant("PI") {
      Double.pi
    }

    Events("onChange")

    Function("hello") {
      return "Test"
    }

    AsyncFunction("setValueAsync") { (value: String) in
      self.sendEvent("onChange", [
        "value": value
      ])
    }
  }
}
