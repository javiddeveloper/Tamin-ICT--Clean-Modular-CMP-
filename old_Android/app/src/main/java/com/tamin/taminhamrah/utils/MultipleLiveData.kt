package com.tamin.taminhamrah.utils

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import timber.log.Timber
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author  : Javid
 * @since   : 9/23/2021 -- 02:34
 * @summary : this class used generic live data in view models
 * @param VM usually is [DataSet] by Entity model
 */
class MultipleLiveData<VM> : MutableLiveData<VM>() {
    private val mPending = AtomicBoolean(false)
    private val values: Queue<VM?> = LinkedList()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in VM>) {
        if (hasActiveObservers()) {
            Timber.d("Multiple observers registered but only one will be notified of changes.")
        }
        super.observe(owner) { vm: VM ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(vm)
                //call next value processing if have such
                if (values.isNotEmpty())
                    pollValue()
            }
        }
    }

    private fun pollValue() {
        Handler(Looper.getMainLooper()).post { setValue(value) }
    }

    @MainThread
    override fun setValue(t: VM?) {
        mPending.set(true)
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @Suppress("unused")
    @MainThread
    fun call() {
        value = null
    }
}