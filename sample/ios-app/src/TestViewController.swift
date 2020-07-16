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
        ExceptionStorageKt.doInitExceptionStorage()
        
        viewModel = TestViewModel(errorEventsDispatcher: EventsDispatcher())
        viewModel.exceptionHandler.bind(viewController: self)
        
        viewModel.petInfo.addObserver { [weak self] info in
            self?.textView.text = info as? String
        }
    }
    
    @IBAction func onRefreshPressed() {
        viewModel.onRefreshPressed()
    }
    
    deinit {
        viewModel.onCleared()
    }
}
