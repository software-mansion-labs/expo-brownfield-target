package expo.modules.brownfield

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class ExpoBrownfieldModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ExpoBrownfieldModule")

    Function("popToNative") { animated: Boolean ->
      appContext.currentActivity?.runOnUiThread {
        @Suppress("DEPRECATION")
        appContext.currentActivity?.onBackPressed()
      }
    }

    Function("setNativeBackEnabled") { enabled: Boolean ->
      println("setNativeBackEnabled: $enabled")
    }
  }
}
