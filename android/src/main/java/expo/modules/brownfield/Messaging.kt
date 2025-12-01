package expo.modules.brownfield

import java.util.UUID

typealias BrownfieldMessage = Map<String, Any?>
typealias BrownfieldCallback = (BrownfieldMessage) -> Unit
typealias BrownfieldFilter = ((BrownfieldMessage) -> Boolean)?

object BrownfieldMessaging {
  data class BrownfieldListener(
    val id: String,
    val filter: BrownfieldFilter = null,
    val callback: BrownfieldCallback
  )

  private val listeners = mutableSetOf<BrownfieldListener>()
  private var expoModule: ExpoBrownfieldModule? = null

  fun addListener(
    filter: BrownfieldFilter = null,
    callback: BrownfieldCallback
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

  fun sendMessage(message: BrownfieldMessage) {
    expoModule?.let { module ->
      print(message)
      module.sendMessage(message)
    }
  }

  internal fun emit(message: BrownfieldMessage) {
    listeners.forEach { listener ->
      if (listener.filter?.invoke(message) != false) {
        listener.callback(message)
      }
    }
  }

  internal fun setExpoModule(expoModule: ExpoBrownfieldModule?) {
    this.expoModule = expoModule
  }
}