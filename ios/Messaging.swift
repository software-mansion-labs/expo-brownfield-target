import Foundation

public typealias BrownfieldMessage = [String: Any?]
public typealias BrownfieldCallback = (BrownfieldMessage) -> Void

public class BrownfieldMessagingInternal {
  public static let shared = BrownfieldMessagingInternal()

  private var listeners: [String: BrownfieldCallback] = [:]
  private var expoModule: ExpoBrownfieldModule? = nil

  private init() {}

  @discardableResult
  public func addListener(
    _ callback: @escaping BrownfieldCallback
  ) -> String  {
    let id = UUID().uuidString
    listeners[id] = callback
    return id
  }

  public func removeListener(id: String) {
    listeners.removeValue(forKey: id)
  }

  public func sendMessage(_ message: BrownfieldMessage) {
    expoModule?.sendMessage(message)
  }

  internal func emit(_ message: BrownfieldMessage) {
    listeners.values.forEach { listener in
      listener(message)
    }
  }

  internal func setExpoModule(_ expoModule: ExpoBrownfieldModule?) {
    self.expoModule = expoModule
  }
}
