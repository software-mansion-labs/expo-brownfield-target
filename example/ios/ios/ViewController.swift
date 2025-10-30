//
//  ViewController.swift
//  ios
//
//  Created by Patryk Mleczek on 10/30/25.
//

import MyBrownfieldApp
import UIKit

class ViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let reactNativeView = MyBrownfieldApp
            .ReactNativeHostManager
            .shared
            .loadView(
                moduleName: "main",
                initialProps: nil,
                launchOptions: [:]
            )
        
        view.addSubview(reactNativeView)
        
        reactNativeView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            reactNativeView.topAnchor.constraint(equalTo: view.topAnchor),
            reactNativeView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            reactNativeView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            reactNativeView.trailingAnchor.constraint(equalTo: view.trailingAnchor)
        ])
    }
}
