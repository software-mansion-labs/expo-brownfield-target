import SwiftUI
import MyBrownfieldApp

let messageDateFormatter: DateFormatter = {
  let formatter = DateFormatter()
  formatter.locale = Locale(identifier: "en_US_POSIX")
  formatter.dateFormat = "dd MMM yyyy HH:mm:ss"
  return formatter
}()

// MARK: - ContentView

struct ContentView: View {
  @State private var listenerId: String? = nil
  @State private var timer: Timer? = nil

  init() {
    ReactNativeHostManager.shared.initialize()
  }

  var body: some View {
    // CASE: Simple use case
//        VStack {
//            ReactNativeView(moduleName: "main")
//        }
    // CASE: Navigation use case
    NavigationStack {
      VStack {
        NavigationLink {
          ReactNativeView(moduleName: "main")
        } label: {
          Text("Open React Native app")
            .font(.largeTitle)
            .underline(true)
        }
      }
    }
    .onAppear {
      listenerId = BrownfieldMessaging.addListener { message in
        print("Message from React Native received:")
        print(message)
      }
      startSendingMessages()
    }
    .onDisappear {
      if let id = listenerId {
        BrownfieldMessaging.removeListener(id: id)
      }
      stopSendingMessages()
    }
  }

  private func startSendingMessages() {
    // Run on background thread so that interaction like scrolling
    // don't block the messaging
    DispatchQueue.global(qos: .background).async {
      self.timer = Timer.scheduledTimer(
        withTimeInterval: 5.0,
        repeats: true
      ) { [self] _ in
        self.sendMessage()
      }
      let runLoop = RunLoop.current
      runLoop.add(self.timer as! Timer, forMode: .default)
      runLoop.run()
    }
  }

  private func stopSendingMessages() {
    timer?.invalidate()
    timer = nil
  }

  private func sendMessage() {
    let message: [String: Any] = [
      "sender": "iOS app",
      "receiver": "React Native app",
      "data": [
        "array": [1, 2, 3.5, true, "hello", false, ["key": "value"]],
        "object": [
          "nested": ["key": "value"],
        ],
        "number": 123.456,
        "boolean": false,
        "string": "hello",
      ],
      "metadata": [
        "timestamp": messageDateFormatter.string(from: Date()),
        "platform": "iOS",
      ],
    ]

    BrownfieldMessaging.sendMessage(message)
  }
}

#Preview {
  ContentView()
}
