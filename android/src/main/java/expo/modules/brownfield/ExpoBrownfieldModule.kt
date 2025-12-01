package expo.modules.brownfield

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

const val NATIVE_MESSAGE_EVENT_NAME = "onMessage"

class ExpoBrownfieldModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ExpoBrownfieldModule")

    Events(NATIVE_MESSAGE_EVENT_NAME)

    OnStartObserving {
      BrownfieldMessaging.setExpoModule(this@ExpoBrownfieldModule)
    }

    OnStopObserving {
      BrownfieldMessaging.setExpoModule(null)
    }

    Function("popToNative") { animated: Boolean ->
      appContext.currentActivity?.runOnUiThread {
        @Suppress("DEPRECATION")
        appContext.currentActivity?.onBackPressed()
      }
    }

    Function("sendMessage") { message: BrownfieldMessage ->
      BrownfieldMessaging.emit(message)
    }

    Function("setNativeBackEnabled") { enabled: Boolean ->
      BrownfieldNavigationState.nativeBackEnabled = enabled
    }
  }

  fun sendMessage(message: BrownfieldMessage) {
    sendEvent(NATIVE_MESSAGE_EVENT_NAME, message)
  }
}
