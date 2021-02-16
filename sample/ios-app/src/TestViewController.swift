/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import UIKit
import MultiPlatformLibrary

class TestViewController: UIViewController {
    
    @IBOutlet private var textView: UITextView!
    
    private var viewModel: TestViewModel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        viewModel = TestViewModel()
//        viewModel.exceptionHandler.bind(viewController: self)
        
//        viewModel.petInfo.addObserver { [weak self] info in
//            self?.textView.text = info as String?
//        }
    }
    
    @IBAction func onRefreshPressed() {
        viewModel.onRefreshPressed()
    }

    @IBAction func onCustomScopePressed() {
        viewModel.onSimpleRequestWithCustomScopePressed()
    }

    @IBAction func onSimpleRequest() {
        viewModel.onSimpleRequestPressed()
    }
    
    override func didMove(toParentViewController parent: UIViewController?) {
        if parent == nil {
            viewModel.onCleared()
            viewModel = nil
        }
    }
    
//    deinit {
//        viewModel.onCleared()
//    }
}
