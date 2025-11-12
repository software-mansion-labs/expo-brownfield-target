//
//  ContentView.swift
//  ios-swiftui
//
//  Created by Patryk Mleczek on 11/12/25.
//

import SwiftUI
import MyBrownfieldApp

struct ContentView: View {
    @State private var moduleName = "main"
    private let initialProps: [AnyHashable: Any]? = [:]
    private let launchOptions: [AnyHashable: Any]? = [:]
    
    var body: some View {
        VStack {
            MyBrownfieldApp.ReactNativeView(
                moduleName: "main",
            )
        }
        .ignoresSafeArea(.all)
    }
}

#Preview {
    ContentView()
}
