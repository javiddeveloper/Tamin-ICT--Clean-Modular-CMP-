package com.tamin.taminhamrah.utils.myDatePicker;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.tamin.taminhamrah.R;
import com.tamin.taminhamrah.utils.myDatePicker.utils.MyPersianCalendar;
import com.tamin.taminhamrah.utils.myDatePicker.utils.MyPersianCalendarConstants;
import com.tamin.taminhamrah.utils.myDatePicker.utils.MyPersianCalendarUtils;
import com.tamin.taminhamrah.utils.myDatePicker.utils.MyPersianHelper;
import com.tamin.taminhamrah.utils.myDatePicker.widgets.MyPersianNumberPicker;

import java.util.Date;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;



public class MyPersianDatePicker extends LinearLayout {

    private final MyPersianPickerDate persianDate;
    private int selectedMonth;
    private int selectedYear;
    private int selectedDay;
    private boolean displayMonthNames;
    private OnDateChangedListener mListener;
    private final MyPersianNumberPicker yearNumberPicker;
    private final MyPersianNumberPicker monthNumberPicker;
    private final MyPersianNumberPicker dayNumberPicker;

    private int minYear;
    private int maxYear;
    private int maxMonth;
    private int maxDay;

    private boolean displayDescription;
    private final TextView descriptionTextView;
    private Typeface typeFace;
    private int dividerColor;
    private int yearRange;

    public MyPersianDatePicker(Context context) {
        this(context, null, -1);
    }

    public MyPersianDatePicker(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    private OnTouchListener otl = new OnTouchListener() {
        public boolean onTouch (View v, MotionEvent event) {
            return true; // the listener has consumed the event
        }
    };


    public MyPersianDatePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // inflate views
        View view = LayoutInflater.from(context).inflate(R.layout.my_sl_persion_date_picker, this);

        // get views
        yearNumberPicker = view.findViewById(R.id.yearNumberPicker);
        monthNumberPicker = view.findViewById(R.id.monthNumberPicker);
        dayNumberPicker = view.findViewById(R.id.dayNumberPicker);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);

        monthNumberPicker.setFocusableInTouchMode(true);
        yearNumberPicker.setFocusableInTouchMode(true);

        //These items have been added to prevent the keyboard
        // from opening when user
        // .0click on EditTexts
        //day
        dayNumberPicker.setFocusableInTouchMode(true);
        if (dayNumberPicker.getChildAt(0) != null ) {
            dayNumberPicker.getChildAt(0).setClickable(false);
            dayNumberPicker.getChildAt(0).setFocusable(false);
        }

        //month
        monthNumberPicker.setFocusableInTouchMode(true);
        if (monthNumberPicker.getChildAt(0) != null ) {
            monthNumberPicker.getChildAt(0).setClickable(false);
            monthNumberPicker.getChildAt(0).setFocusable(false);
        }

        //year
        if (yearNumberPicker.getChildAt(0) != null ) {
            yearNumberPicker.getChildAt(0).setClickable(false);
            yearNumberPicker.getChildAt(0).setFocusable(false);
        }
        yearNumberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return MyPersianHelper.toPersianNumber(i + "");
            }
        });


        monthNumberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return MyPersianHelper.toPersianNumber(i + "");
            }
        });
        dayNumberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return MyPersianHelper.toPersianNumber(i + "");
            }
        });

        // init calendar
        persianDate = new MyPersianDateImpl();

        // update variables from xml
        updateVariablesFromXml(context, attrs);

        // update view
        updateViewData();


    /*    et.setRawInputType(InputType.TYPE_NULL);
        et.setTextColor(context.getResources().getColor(R.color.red));

        InputMethodManager im = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(et.getWindowToken(), 0);*/

   /*     et.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
                //    et.setEnabled(false);
    }

    private void updateVariablesFromXml(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyPersianDatePicker, 0, 0);
        yearRange = a.getInteger(R.styleable.MyPersianDatePicker_yearRange, 10);
        /*
         * Initializing yearNumberPicker min and max values If minYear and
         * maxYear attributes are not set, use (current year - 10) as min and
         * (current year + 10) as max.
         */
        minYear = a.getInt(R.styleable.MyPersianDatePicker_minYear, persianDate.getPersianYear() - yearRange);
        maxYear = a.getInt(R.styleable.MyPersianDatePicker_maxYear, persianDate.getPersianYear() + yearRange);
        displayMonthNames = a.getBoolean(R.styleable.MyPersianDatePicker_displayMonthNames, false);
        /*
         * displayDescription
         */
        displayDescription = a.getBoolean(R.styleable.MyPersianDatePicker_displayDescription, false);
        selectedDay = a.getInteger(R.styleable.MyPersianDatePicker_selectedDay, persianDate.getPersianDay());
        selectedYear = a.getInt(R.styleable.MyPersianDatePicker_selectedYear, persianDate.getPersianYear());
        selectedMonth = a.getInteger(R.styleable.MyPersianDatePicker_selectedMonth, persianDate.getPersianMonth());

        // if you pass selected year before min year, then we need to push min year to before that
        if (minYear > selectedYear) {
            minYear = selectedYear - yearRange;
        }

        if (maxYear < selectedYear) {
            maxYear = selectedYear + yearRange;
        }

        a.recycle();
    }

    public void setBackgroundColor(@ColorInt int color) {
        yearNumberPicker.setBackgroundColor(color);
        monthNumberPicker.setBackgroundColor(color);
        dayNumberPicker.setBackgroundColor(color);
    }

    public void setBackgroundDrawable(@DrawableRes int drawableBg) {
        yearNumberPicker.setBackgroundResource(drawableBg);
        monthNumberPicker.setBackgroundResource(drawableBg);
        dayNumberPicker.setBackgroundResource(drawableBg);
    }

    public void setMaxYear(int maxYear) {
        this.maxYear = maxYear;
        updateViewData();
    }

    public void setMaxMonth(int maxMonth) {
        this.maxMonth = maxMonth;
        updateViewData();
    }

    public void setMaxDay(int maxDay) {
        this.maxDay = maxDay;
        updateViewData();
    }

    public void setMinYear(int minYear) {
        this.minYear = minYear;
        updateViewData();
    }

    public void setTypeFace(Typeface typeFace) {
        this.typeFace = typeFace;
        updateViewData();
    }

    public void setDividerColor(@ColorInt int color) {
        this.dividerColor = color;
        updateViewData();
    }

    private void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    private void updateViewData() {

        if (typeFace != null) {
            yearNumberPicker.setTypeFace(typeFace);
            monthNumberPicker.setTypeFace(typeFace);
            dayNumberPicker.setTypeFace(typeFace);
        }

        if (dividerColor > 0) {
            setDividerColor(yearNumberPicker, dividerColor);
            setDividerColor(monthNumberPicker, dividerColor);
            setDividerColor(dayNumberPicker, dividerColor);
        }

        yearNumberPicker.setMinValue(minYear);
        yearNumberPicker.setMaxValue(maxYear);


        if (selectedYear > maxYear) {
            selectedYear = maxYear;
        }
        if (selectedYear < minYear) {
            selectedYear = minYear;
        }

        yearNumberPicker.setValue(selectedYear);
        yearNumberPicker.setOnValueChangedListener(dateChangeListener);

        /*
         * initialing monthNumberPicker
         */

        monthNumberPicker.setMinValue(1);
        monthNumberPicker.setMaxValue(maxMonth > 0 ? maxMonth : 12);
        if (displayMonthNames) {
            monthNumberPicker.setDisplayedValues(MyPersianCalendarConstants.persianMonthNames);
        }

        if (selectedMonth < 1 || selectedMonth > 12) {
            throw new IllegalArgumentException(String.format("Selected month (%d) must be between 1 and 12", selectedMonth));
        }
        monthNumberPicker.setValue(selectedMonth);
        monthNumberPicker.setOnValueChangedListener(dateChangeListener);

        /*
         * initializing dayNumberPicker
         */
        dayNumberPicker.setMinValue(1);
        setDayNumberPickerMaxValue(31);
        if (selectedDay > 31 || selectedDay < 1) {
            throw new IllegalArgumentException(String.format("Selected day (%d) must be between 1 and 31", selectedDay));
        }
        if (selectedMonth > 6 && selectedMonth < 12 && selectedDay == 31) {
            selectedDay = 30;
        } else {
            boolean isLeapYear = MyPersianCalendarUtils.isPersianLeapYear(selectedYear);
            if (isLeapYear && selectedDay == 31) {
                selectedDay = 30;
            } else if (selectedDay > 29) {
                selectedDay = 29;
            }
        }
        dayNumberPicker.setValue(selectedDay);
        dayNumberPicker.setOnValueChangedListener(dateChangeListener);


        if (displayDescription) {
            descriptionTextView.setVisibility(View.VISIBLE);
            descriptionTextView.setText(persianDate.getPersianLongDate());
        }
    }

    NumberPicker.OnValueChangeListener dateChangeListener = new NumberPicker.OnValueChangeListener() {

        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            int year = yearNumberPicker.getValue();
            boolean isLeapYear = MyPersianCalendarUtils.isPersianLeapYear(year);

            int month = monthNumberPicker.getValue();
            int day = dayNumberPicker.getValue();

            if (month < 7) {
                dayNumberPicker.setMinValue(1);
                setDayNumberPickerMaxValue(31);
            } else if (month < 12) {
                if (day == 31) {
                    dayNumberPicker.setValue(30);
                }
                dayNumberPicker.setMinValue(1);
                setDayNumberPickerMaxValue(30);
            } else if (month == 12) {
                if (isLeapYear) {
                    if (day == 31) {
                        dayNumberPicker.setValue(30);
                    }
                    dayNumberPicker.setMinValue(1);
                    setDayNumberPickerMaxValue(30);
                } else {
                    if (day > 29) {
                        dayNumberPicker.setValue(29);
                    }
                    dayNumberPicker.setMinValue(1);
                    setDayNumberPickerMaxValue(29);
                }
            }

            persianDate.setDate(
                    year,
                    month,
                    day
            );

            // Set description
            if (displayDescription) {
                descriptionTextView.setText(persianDate.getPersianLongDate());
            }

            if (mListener != null) {
                mListener.onDateChanged(year, month, day);
            }

        }

    };

    public void setDayVisibility(boolean visibility) {
        if (visibility) {
            dayNumberPicker.setVisibility(View.VISIBLE);
        }else {
            dayNumberPicker.setVisibility(View.GONE);
        }

        invalidate();
    }

    public void setDayNumberPickerMaxValue(int value) {
        if (monthNumberPicker.getValue() == maxMonth) {
            if (maxDay > 0) {
                dayNumberPicker.setMaxValue(maxDay);
            } else {
                dayNumberPicker.setMaxValue(value);
            }
        } else {
            dayNumberPicker.setMaxValue(value);
        }
    }

    public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
        mListener = onDateChangedListener;
    }

    /**
     * The callback used to indicate the user changed the date.
     * A class that wants to be notified when the date of PersianDatePicker
     * changes should implement this interface and register itself as the
     * listener of date change events using the PersianDataPicker's
     * setOnDateChangedListener method.
     */
    public interface OnDateChangedListener {

        /**
         * Called upon a date change.
         *
         * @param newYear  The year that was set.
         * @param newMonth The month that was set (1-12)
         * @param newDay   The day of the month that was set.
         */
        void onDateChanged(int newYear, int newMonth, int newDay);
    }

    public Date getDisplayDate() {
        return persianDate.getGregorianDate();
    }

    public void setDisplayDate(Date displayDate) {
        persianDate.setDate(displayDate);
        setDisplayPersianDate(persianDate);
    }

    /**
     * @return {@link MyPersianCalendar} that indicate current calendar state
     * @Deprecated Use getPersianDate() instead
     */
    @Deprecated
    public MyPersianCalendar getDisplayPersianDate() {
        MyPersianCalendar persianCalendar = new MyPersianCalendar();
        persianCalendar.setPersianDate(
                persianDate.getPersianYear(),
                persianDate.getPersianMonth(),
                persianDate.getPersianDay()
        );
        return persianCalendar;
    }

    public MyPersianPickerDate getPersianDate() {
        return persianDate;
    }

    /**
     * @Deprecated Use setDisplayPersianDate(MyPersianPickerDate displayPersianDate) instead
     */
    @Deprecated
    public void setDisplayPersianDate(MyPersianCalendar displayPersianDate) {
        MyPersianPickerDate MyPersianPickerDate = new MyPersianDateImpl();
        MyPersianPickerDate.setDate(
                displayPersianDate.getPersianYear(),
                displayPersianDate.getPersianMonth(),
                displayPersianDate.getPersianDay()
        );
        setDisplayPersianDate(MyPersianPickerDate);
    }

    public void setDisplayPersianDate(MyPersianPickerDate displayPersianDate) {

        persianDate.setDate(displayPersianDate.getTimestamp());

        final int year = persianDate.getPersianYear();
        final int month = persianDate.getPersianMonth();
        final int day = persianDate.getPersianDay();

        selectedYear = year;
        selectedMonth = month;
        selectedDay = day;

        // if you pass selected year before min year, then we need to push min year to before that
        if (minYear > selectedYear) {
            minYear = selectedYear - yearRange;
            yearNumberPicker.setMinValue(minYear);
        }

        // if you pass selected year after max year, then we need to push max year to after that
        if (maxYear < selectedYear) {
            maxYear = selectedYear + yearRange;
            yearNumberPicker.setMaxValue(maxYear);
        }

        yearNumberPicker.post(new Runnable() {
            @Override
            public void run() {
                yearNumberPicker.setValue(year);
            }
        });
        monthNumberPicker.post(new Runnable() {
            @Override
            public void run() {
                monthNumberPicker.setValue(month);

            }
        });
        dayNumberPicker.post(new Runnable() {
            @Override
            public void run() {
                dayNumberPicker.setValue(day);
            }
        });
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        // begin boilerplate code that allows parent classes to save state
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        // end

        ss.datetime = this.getDisplayDate().getTime();
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // begin boilerplate code so parent classes can restore state
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        // end

        setDisplayDate(new Date(ss.datetime));
    }

    static class SavedState extends BaseSavedState {
        long datetime;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.datetime = in.readLong();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(this.datetime);
        }

        // required field that makes Parcelables from a Parcel
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
