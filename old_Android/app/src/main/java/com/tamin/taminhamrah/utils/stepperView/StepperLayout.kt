package com.tamin.taminhamrah.utils.stepperView

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.viewbinding.ViewBinding
import timber.log.Timber

class StepperLayout(context: Context?, attrs: AttributeSet?) :
    LinearLayout(context, attrs, VERTICAL), VerticalStepperItemView.StepActionClickListener {
    init {
        orientation = VERTICAL
        invalidate()
    }

    var currentStepIndex = 1
    var totalStep = 0

    var onNextStepClickListener: NextStepClickListener? = null
    var onPreviousStepClickListener: PreviousStepClickListener? = null

    var steps: MutableList<VerticalStepperItemView> = ArrayList()
    var stepsLayouts: MutableList<ViewBinding> = ArrayList()

    fun getStepLayoutBindingByStep(step: Int): ViewBinding? {
        val index = step - 1
        if (index < 0 || index >= stepsLayouts.size)
            return null

        return stepsLayouts[index]
    }

    fun initial(stepLayoutBindings: List<ViewBinding>, initialStep: Int = 1) {
        Timber.tag("ErrorReCreateContract").e("initialStep=$initialStep")
        steps.clear()
        stepsLayouts.clear()
        removeAllViews()

        stepsLayouts.addAll(stepLayoutBindings)
        for (i in stepLayoutBindings.indices)
            addView(stepLayoutBindings[i].root)

        for (i in 0 until childCount)
            steps.add(
                i,
                getChildAt(i) as? VerticalStepperItemView
                    ?: throw Exception("child item is not instance of VerticalStepperItemView")
            )

        for (step in steps)
            step.setStepActionClickListener(this)

        VerticalStepperItemView.bindSteppers2(steps)

        currentStepIndex = 1

        for (i in 1 until initialStep)
            nextStep()


    }


    override fun onNextStepClickListener(stepIndex: Int) {

        onNextStepClickListener?.onNextStepClickListener(stepIndex, steps[stepIndex - 1])

    }

    override fun onPreviousStepClickListener(stepIndex: Int) {
        onPreviousStepClickListener?.onPreviousStepClickListener(stepIndex, steps[stepIndex - 1])

    }

    fun nextStep() {
        if (currentStepIndex == steps.size)
            return

        steps[currentStepIndex - 1].nextStep()
        currentStepIndex += 1
    }

    fun previousStep() {
        if (currentStepIndex == 1)
            return

        steps[currentStepIndex - 1].prevStep()
        currentStepIndex -= 1
    }

    fun getCurrentStep() = steps[currentStepIndex - 1]
    fun getStepByIndex(index: Int) = steps[index - 1]

    interface NextStepClickListener {
        fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView)
    }

    interface PreviousStepClickListener {
        fun onPreviousStepClickListener(stepIndex: Int, step: VerticalStepperItemView)
    }

}