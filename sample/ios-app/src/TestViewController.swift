/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import UIKit
import MultiPlatformLibrary
import SwiftyGif

class TestViewController: UIViewController {
    
    @IBOutlet private var imageView: UIImageView!
    
    private var viewModel: TestViewModel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        viewModel = TestViewModel()
        
        viewModel.gifUrl.addObserver { [weak self] url in
            guard let url = url as String? else {
                self?.imageView.image = nil
                return
            }
            
            self?.imageView.setGifFromURL(URL(string: url)!)
        }
    }
    
    @IBAction func onRefreshPressed() {
        viewModel.onRefreshPressed()
    }
    
    deinit {
        viewModel.onCleared()
    }
}
