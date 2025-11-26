import SwiftUI

struct ReactNativeViewRepresentable: UIViewRepresentable {
  var moduleName: String
  var initialProps: [AnyHashable: Any]?
  var launchOptions: [AnyHashable: Any]?

  func makeUIView(context: Context) -> UIView {
    return ReactNativeHostManager.shared.loadView(
      moduleName: moduleName,
      initialProps: initialProps,
      launchOptions: launchOptions
    )
  }

  func updateUIView(_ uiView: UIView, context: Context) {}
}

public struct ReactNativeView: View {
  @Environment(\.dismiss) var dismiss
  
  var moduleName: String
  var initialProps: [AnyHashable: Any]? = [:]
  var launchOptions: [AnyHashable: Any]? = [:]

  public init(
    moduleName: String, 
    initialProps: [AnyHashable: Any] = [:],
    launchOptions: [AnyHashable: Any] = [:],  
  ) {
    self.moduleName = moduleName
    self.initialProps = initialProps
    self.launchOptions = launchOptions
  }

  public var body: some View {
    ReactNativeViewRepresentable(
      moduleName: moduleName, initialProps: initialProps, launchOptions: launchOptions
    )
    .ignoresSafeArea(.all)
    // .navigationBarBackButtonHidden(true)
    .navigationBarTitle("")
    .navigationBarHidden(true)
    .onReceive(NotificationCenter.default.publisher(for: NSNotification.Name("popToNative"))) { _ in
      dismiss()
    }
  }
}