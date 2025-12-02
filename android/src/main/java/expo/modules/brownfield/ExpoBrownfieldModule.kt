package expo.modules.brownfield

import androidx.activity.ComponentActivity
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

object BrownfieldNavigationState {
  var nativeBackEnabled = true
}

class ExpoBrownfieldModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ExpoBrownfieldModule")

    Function("popToNative") { animated: Boolean ->
      appContext.currentActivity?.runOnUiThread {
        val componentActivity = appContext.currentActivity as? ComponentActivity
        if (componentActivity != null) {
          val enabled = BrownfieldNavigationState.nativeBackEnabled
          BrownfieldNavigationState.nativeBackEnabled = true
          componentActivity.onBackPressedDispatcher?.onBackPressed()
          BrownfieldNavigationState.nativeBackEnabled = enabled
        }
      }
    }

    Function("setNativeBackEnabled") { enabled: Boolean ->
      BrownfieldNavigationState.nativeBackEnabled = enabled
    }
  }
}
