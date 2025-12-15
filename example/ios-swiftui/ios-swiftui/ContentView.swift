import SwiftUI
import MyBrownfieldApp

struct ContentView: View {
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
  }
}

#Preview {
  ContentView()
}
