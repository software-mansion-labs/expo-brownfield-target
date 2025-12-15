import MyBrownfieldApp
import UIKit

let dateFormatter: DateFormatter = {
  let formatter = DateFormatter()
  formatter.locale = Locale(identifier: "en_US_POSIX")
  formatter.dateFormat = "dd MMM yyyy HH:mm:ss"
  return formatter
}()

// MARK: - ViewController

class ViewController: UIViewController {
  private var listenerId: String?
  private var timer: Timer?

  override func viewDidLoad() {
    super.viewDidLoad()

    view.backgroundColor = .systemBackground
    let button = UIButton(type: .system)
    button.setTitle("Open React Native app", for: .normal)
    button.titleLabel?.font = .systemFont(ofSize: 28, weight: .bold)
    button.addTarget(self, action: #selector(openReactNativeApp), for: .touchUpInside)

    button.translatesAutoresizingMaskIntoConstraints = false
    view.addSubview(button)

    NSLayoutConstraint.activate([
      button.centerXAnchor.constraint(equalTo: view.centerXAnchor),
      button.centerYAnchor.constraint(equalTo: view.centerYAnchor),
    ])

    listenerId = BrownfieldMessaging.addListener { message in
      print("Message from React Native received:")
      print(message)
    }

    startSendingMessages()
  }

  deinit {
    stopSendingMessages()
    if let id = listenerId {
      BrownfieldMessaging.removeListener(id: id)
    }
  }

  @objc
  private func openReactNativeApp() {
    let reactNativeViewController = ReactNativeViewController(moduleName: "main")
    navigationController?.pushViewController(reactNativeViewController, animated: true)
  }

  private func startSendingMessages() {
    timer = Timer.scheduledTimer(
      withTimeInterval: 5.0,
      repeats: true
    ) { [weak self] _ in
      self?.sendMessage()
    }
  }

  private func stopSendingMessages() {
    timer?.invalidate()
    timer = nil
  }

  private func sendMessage() {
    let message: [String: Any] = [
      "sender": "iOS UIKit app",
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
        "timestamp": dateFormatter.string(from: Date()),
        "platform": "iOS (UIKit)",
      ],
    ]

    BrownfieldMessaging.sendMessage(message)
  }
}
