package com.tamin.taminhamrah.ui.base

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager


abstract class BaseActivity<V : BaseViewModel> : ContainerBaseActivity() {

    abstract val mViewModel: V?

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
//    abstract fun getBindingVariable(): Pair<Int, Any?>

    /**
     * @return layout resource id
     */
//    @LayoutRes
//    abstract fun getLayoutId(): Int

    abstract fun setupObserver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        performDataBinding()


        val w: Window = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setupObserver()
    }


    private fun performDataBinding() {
        //    viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        //    viewDataBinding!!.setVariable(getBindingVariable().first, getBindingVariable().second)
        //    viewDataBinding!!.lifecycleOwner = this
        //    viewDataBinding!!.executePendingBindings()

    }


//    override fun onDestroy() {
//        super.onDestroy()
    //   viewDataBinding =null
//        mViewModel?.onStop()
//    }

    abstract fun setTitle()

    /*override fun onResume() {
        setOnBackPressed()
        super.onResume()
    }

    override fun onPause() {
        removeOnBackPressed()
        super.onPause()
    }

    abstract fun setOnBackPressed()
    abstract fun removeOnBackPressed()
*/
    fun View.afterLayout(what: () -> Unit) {
        if (isLaidOut) {
            what.invoke()
        } else viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                what.invoke()
            }
        })
    }


}