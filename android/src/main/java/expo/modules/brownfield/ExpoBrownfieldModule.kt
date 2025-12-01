package expo.modules.brownfield

import androidx.activity.ComponentActivity
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import java.util.UUID

typealias BrownfieldMessage = Map<String, Any?>

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

    Function("sendMessage") { message: BrownfieldMessage ->
      println("Message received")
      println(message)
      BrownfieldMessaging.emit(message)
    }

    Function("setNativeBackEnabled") { enabled: Boolean ->
      BrownfieldNavigationState.nativeBackEnabled = enabled
    }
  }
}

object BrownfieldNavigationState {
  var nativeBackEnabled = true
}

object BrownfieldMessaging {
  data class BrownfieldListener(
    val id: String,
    val filter: ((BrownfieldMessage) -> Boolean)? = null,
    val callback: (BrownfieldMessage) -> Unit
  )

  private val listeners = mutableSetOf<BrownfieldListener>()

  fun emit(message: BrownfieldMessage) {
    listeners.forEach { listener ->
      if (listener.filter?.invoke(message) != false) {
        listener.callback(message)
      }
    }
  }

  fun addListener(
    filter: ((BrownfieldMessage) -> Boolean)? = null,
    callback: ((BrownfieldMessage) -> Unit)
  ): String {
    val id = java.util.UUID.randomUUID().toString()
    listeners.add(BrownfieldListener(
      id,
      filter,
      callback
    ))

    return id
  }

  fun removeListener(id: String) {
    listeners.removeAll { it.id == id }
  }
}
