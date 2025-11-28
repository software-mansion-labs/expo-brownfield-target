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
    }
    
    @objc private func openReactNativeApp() {
        let reactNativeViewController = ReactNativeViewController(moduleName: "main")
        navigationController?.pushViewController(reactNativeViewController, animated: true)
    }
}
