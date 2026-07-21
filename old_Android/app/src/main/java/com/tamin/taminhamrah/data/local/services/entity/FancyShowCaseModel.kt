package com.tamin.taminhamrah.data.local.services.entity

import android.view.View
class FancyShowCaseModel(
    var view: View,
    var title: Int,
    var detail: Int,
    var shape: Shape = Shape.CIRCLE,
    var clickable : Boolean=false

    )
enum class Shape {
    CIRCLE, RECT
}





