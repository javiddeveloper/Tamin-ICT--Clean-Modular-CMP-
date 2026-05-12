// NOTE: This file belongs to the TaminHamrahWidget extension target.
// Create the target in Xcode: File > New > Target > Widget Extension,
// then add this file and TaminHamrahWidget.swift to that target.

import WidgetKit
import SwiftUI

@main
struct TaminHamrahWidgetBundle: WidgetBundle {
    var body: some Widget {
        TaminHamrahTrendingWidget()
    }
}
