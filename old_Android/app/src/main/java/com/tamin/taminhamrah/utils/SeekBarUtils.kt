package com.tamin.taminhamrah.utils


private var mTickEnd =5
private var mTickInterval =1
private var mTickStart = 0
private var mTickCount = ((mTickEnd - mTickStart) / mTickInterval) + 1

object SeekBarUtils {
   /* fun RangeBar.setTickStart(tickStart: Int){
        val tickCount = ((mTickEnd - tickStart) / mTickInterval).toInt() + 1
        if (tickCount > 1) {
            mTickCount = tickCount
            mTickStart = tickStart

            // Prevents resetting the indices when creating new activity, but
            // allows it on the first setting.
            if (mFirstSetTickCount) {
                mLeftIndex = 0
                mRightIndex = mTickCount - 1
                if (mListener != null) {
                    mListener.onRangeChangeListener(
                        this, mLeftIndex, mRightIndex,
                        getPinValue(mLeftIndex),
                        getPinValue(mRightIndex)
                    )
                }
            }
            if (indexOutOfRange(mLeftIndex, mRightIndex)) {
                mLeftIndex = 0
                mRightIndex = mTickCount - 1
                if (mListener != null) {
                    mListener.onRangeChangeListener(
                        this, mLeftIndex, mRightIndex,
                        getPinValue(mLeftIndex),
                        getPinValue(mRightIndex)
                    )
                }
            }
            createBar()
            createPins()
        } else {
            Log.e(RangeBar.TAG, "tickCount less than 2; invalid tickCount.")
            throw IllegalArgumentException("tickCount less than 2; invalid tickCount.")
        }    }

    fun RangeBar.setTickEnd(tickEnd: Int){
        Log.i( "tickEnd: ","end =______      $tickEnd ")

    }
    fun RangeBar.setRangePinsByValue(leftPinValue :Int, rightPinValue : Int){
        Log.i( "setRangePinsByValue: ","leftPinValue =$leftPinValue ------------- rightPinValue=$rightPinValue")

    }*/
}