/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import UIKit
import MultiPlatformLibrary

class TestViewController: UIViewController {
    
    @IBOutlet private var restText: UITextView!
    @IBOutlet private var webSocketText: UITextView!
    
    private var viewModel: TestViewModel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        viewModel = TestViewModel()
        viewModel.exceptionHandler.bind(viewController: self)
        
        viewModel.petInfo.addObserver { [weak self] info in
            self?.restText.text = info as String?
        }
        
        viewModel.websocketInfo.addObserver { [weak self] info in
            self?.webSocketText.text = info as String?
        }
    }
    
    @IBAction func onRefreshPressed() {
        viewModel.onRefreshPetPressed()
    }
    
    @IBAction func onRefreshWebsocketPressed() {
        viewModel.onRefreshWebsocketPressed()
    }
    
    deinit {
        viewModel.onCleared()
    }
}
