package expo.modules.brownfield

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class ExpoBrownfieldModule : Module() {
  companion object {
    var shouldPopToNative: Boolean = false
  }

  override fun definition() = ModuleDefinition {
    Name("ExpoBrownfieldModule")

    Function("popToNative") { animated: Boolean ->
      shouldPopToNative = true
      appContext.currentActivity?.runOnUiThread {
        @Suppress("DEPRECATION")
        appContext.currentActivity?.onBackPressed()
      }
    }
  }
}
