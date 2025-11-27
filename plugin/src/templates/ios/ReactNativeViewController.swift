import UIKit

@objc public class ReactNativeViewController: UIViewController {
    private let moduleName: String
    private let initialProps: [AnyHashable: Any]?
    private let launchOptions: [AnyHashable: Any]?
    
    @objc public init(
        moduleName: String,
        initialProps: [AnyHashable: Any]? = nil,
        launchOptions: [AnyHashable: Any]? = nil
    ) {
        self.moduleName = moduleName
        self.initialProps = initialProps
        self.launchOptions = launchOptions
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    public override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view = ReactNativeHostManager.shared.loadView(
            moduleName: moduleName,
            initialProps: initialProps,
            launchOptions: launchOptions,
        )
        
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(popToNative(_:)),
            name: NSNotification.Name("popToNative"),
            object: nil
        )
    }

    public override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(true, animated: false)
    }

    public override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        navigationController?.setNavigationBarHidden(false, animated: false)
    }
    
    @objc private func popToNative(_ notification: Notification) {
        let animated = notification.userInfo?["animated"] as? Bool ?? false
        DispatchQueue.main.async { [weak self] in
            self?.navigationController?.popViewController(animated: animated)
        }
    }
}
