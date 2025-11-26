//
//  ContentView.swift
//  ios-swiftui
//
//  Created by Patryk Mleczek on 11/12/25.
//

import SwiftUI
import MyBrownfieldApp

struct ContentView: View {
    var body: some View {
        NavigationStack {
            VStack {
                NavigationLink {
                    MyBrownfieldApp.ReactNativeView(moduleName: "main")
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
