package com.tamin.taminhamrah.utils.myDatePicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tamin.taminhamrah.R;
import com.tamin.taminhamrah.utils.myDatePicker.interfaceDatePicker.Listener;
import com.tamin.taminhamrah.utils.myDatePicker.utils.MyPersianCalendar;
import com.tamin.taminhamrah.utils.myDatePicker.utils.MyPersianHelper;

import java.util.Date;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import timber.log.Timber;


public class MyPersianDatePickerDialog {

    public static final int THIS_YEAR = -1;
    public static final int THIS_MONTH = -2;
    public static final int THIS_DAY = -3;
    public static final int NO_TITLE = 0;
    public static final int DAY_MONTH_YEAR = 1;
    public static final int WEEKDAY_DAY_MONTH_YEAR = 2;

    private final Context context;
    private String positiveButtonString = "تایید";
    private String negativeButtonString = "انصراف";
    private Listener listener;
    private MyPersianPickerListener MyPersianPickerListener;
    private int maxYear = 0;
    private int maxMonth = 0;
    private int maxDay = 0;
    private int minYear = 0;
    private final MyPersianPickerDate initDate = new MyPersianDateImpl();
    public static Typeface typeFace;
    private String todayButtonString = "امروز";
    private boolean todayButtonVisibility = false;
    private int actionColor = Color.GRAY;
    private int actionTextSize = 12;
    private int negativeTextSize = 12;
    private int todayTextSize = 12;
    private int backgroundColor = Color.WHITE;
    private int titleColor = Color.parseColor("#111111");
    private boolean cancelable = true;
    private boolean forceMode;
    private int pickerBackgroundColor;
    private int pickerBackgroundDrawable;
    private int titleType = 0;
    private boolean showInBottomSheet;
    public static final int MONTH_YEAR = 3;
    private boolean isShowDayPicker = true;

    public MyPersianDatePickerDialog(Context context) {
        this.context = context;
    }

    @Deprecated
    public MyPersianDatePickerDialog setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public MyPersianDatePickerDialog setListener(MyPersianPickerListener listener) {
        this.MyPersianPickerListener = listener;
        return this;
    }

    public MyPersianDatePickerDialog setShowDayPicker(boolean showDayPicker) {
        this.isShowDayPicker = showDayPicker;
        return this;
    }

    public MyPersianDatePickerDialog setMaxYear(int maxYear) {
        this.maxYear = maxYear;
        return this;
    }

    public MyPersianDatePickerDialog setMaxMonth(int maxMonth) {
        if (maxMonth > 12) {
            throw new RuntimeException("max month is not valid");
        }
        this.maxMonth = maxMonth;
        return this;
    }

    public MyPersianDatePickerDialog setMaxDay(int maxDay) {
        if (maxDay > 31) {
            throw new RuntimeException("max day is not valid");
        }
        this.maxDay = maxDay;
        return this;
    }

    public MyPersianDatePickerDialog setTypeFace(Typeface typeFace) {
        MyPersianDatePickerDialog.typeFace = typeFace;
        return this;
    }

    public MyPersianDatePickerDialog setMinYear(int minYear) {
        this.minYear = minYear;
        return this;
    }

    public MyPersianDatePickerDialog setInitDate(Long timestamp) {
        this.initDate.setDate(timestamp);
        return this;
    }

    public MyPersianDatePickerDialog setInitDate(Date date) {
        this.initDate.setDate(date);
        return this;
    }

    public MyPersianDatePickerDialog setInitDate(int persianYear, int persianMonth, int persianDay) {
        this.initDate.setDate(persianYear, persianMonth, persianDay);
        return this;
    }

    public MyPersianDatePickerDialog setInitDate(MyPersianPickerDate initDate) {
        return setInitDate(initDate, false);
    }

    public MyPersianDatePickerDialog setInitDate(MyPersianPickerDate initDate, boolean force) {
        this.forceMode = force;
        this.initDate.setDate(initDate.getTimestamp());
        return this;
    }

    @Deprecated
    public MyPersianDatePickerDialog setInitDate(MyPersianCalendar initDate) {
        return setInitDate(initDate, false);
    }

    @Deprecated
    public MyPersianDatePickerDialog setInitDate(MyPersianCalendar initDate, boolean force) {
        this.forceMode = force;
        this.initDate.setDate(
                initDate.getPersianYear(),
                initDate.getPersianMonth(),
                initDate.getPersianDay()
        );
        return this;
    }

    public MyPersianDatePickerDialog setPositiveButtonString(String positiveButtonString) {
        this.positiveButtonString = positiveButtonString;
        return this;
    }

    public MyPersianDatePickerDialog setPositiveButtonResource(@StringRes int positiveButton) {
        this.positiveButtonString = context.getString(positiveButton);
        return this;
    }

    public MyPersianDatePickerDialog setTodayButtonVisible(boolean todayButtonVisiblity) {
        this.todayButtonVisibility = todayButtonVisiblity;
        return this;
    }

    public MyPersianDatePickerDialog setTodayButton(String todayButton) {
        this.todayButtonString = todayButton;
        return this;
    }

    public MyPersianDatePickerDialog setTodayButtonResource(@StringRes int todayButton) {
        this.todayButtonString = context.getString(todayButton);
        return this;
    }

    public MyPersianDatePickerDialog setTodayTextSize(int sizeInt) {
        this.todayTextSize = sizeInt;
        return this;
    }

    public MyPersianDatePickerDialog setNegativeButton(String negativeButton) {
        this.negativeButtonString = negativeButton;
        return this;
    }

    public MyPersianDatePickerDialog setNegativeButtonResource(@StringRes int negativeButton) {
        this.negativeButtonString = context.getString(negativeButton);
        return this;
    }

    public MyPersianDatePickerDialog setNegativeTextSize(int sizeInt) {
        this.negativeTextSize = sizeInt;
        return this;
    }

    public MyPersianDatePickerDialog setActionTextColor(@ColorInt int colorInt) {
        this.actionColor = colorInt;
        return this;
    }


    public MyPersianDatePickerDialog setActionTextColorResource(@ColorRes int colorInt) {
        this.actionColor = ContextCompat.getColor(context, colorInt);
        return this;
    }

    public MyPersianDatePickerDialog setActionTextSize(int sizeInt) {
        this.actionTextSize = sizeInt;
        return this;
    }

    public MyPersianDatePickerDialog setAllButtonsTextSize(int sizeInt) {
        this.actionTextSize = sizeInt;
        this.negativeTextSize = sizeInt;
        this.todayTextSize = sizeInt;
        return this;
    }

    public MyPersianDatePickerDialog setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public MyPersianDatePickerDialog setBackgroundColor(@ColorInt int bgColor) {
        this.backgroundColor = bgColor;
        return this;
    }

    public MyPersianDatePickerDialog setTitleColor(@ColorInt int titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    public MyPersianDatePickerDialog setPickerBackgroundColor(@ColorInt int color) {
        this.pickerBackgroundColor = color;
        return this;
    }

    public MyPersianDatePickerDialog setPickerBackgroundDrawable(@DrawableRes int drawableBg) {
        this.pickerBackgroundDrawable = drawableBg;
        return this;
    }

    public MyPersianDatePickerDialog setTitleType(int titleType) {
        this.titleType = titleType;
        return this;
    }

    public MyPersianDatePickerDialog setShowInBottomSheet(boolean b) {
        this.showInBottomSheet = b;
        return this;
    }

    public void show() {

        View v = View.inflate(context, R.layout.my_dialog_picker, null);
        final MyPersianDatePicker datePickerView = v.findViewById(R.id.datePicker);
        final TextView dateText = v.findViewById(R.id.dateText);
        final AppCompatButton positiveButton = v.findViewById(R.id.positive_button);
        final AppCompatButton negativeButton = v.findViewById(R.id.negative_button);
        final AppCompatButton todayButton = v.findViewById(R.id.today_button);
        final LinearLayout container = v.findViewById(R.id.container);

        // container.setBackgroundColor(backgroundColor);
        dateText.setTextColor(titleColor);
        datePickerView.setDayVisibility(isShowDayPicker);

      /*  if (pickerBackgroundColor != 0) {
            datePickerView.setBackgroundColor(pickerBackgroundColor);
        } else if (pickerBackgroundDrawable != 0) {
            datePickerView.setBackgroundDrawable(pickerBackgroundDrawable);
        }*/

        if (maxYear > 0) {
            datePickerView.setMaxYear(maxYear);
        } else if (maxYear == THIS_YEAR) {
            maxYear = new MyPersianDateImpl().getPersianYear();
            datePickerView.setMaxYear(maxYear);
        }

        if (maxMonth > 0) {
            datePickerView.setMaxMonth(maxMonth);
        } else if (maxMonth == THIS_MONTH) {
            maxMonth = new MyPersianDateImpl().getPersianMonth();
            datePickerView.setMaxMonth(maxMonth);
        }

        if (maxDay > 0) {
            datePickerView.setMaxDay(maxDay);
        } else if (maxDay == THIS_DAY) {
            maxDay = new MyPersianDateImpl().getPersianDay();
            datePickerView.setMaxDay(maxDay);
        }

        if (minYear > 0) {
            datePickerView.setMinYear(minYear);
        } else if (minYear == THIS_YEAR) {
            minYear = new MyPersianDateImpl().getPersianYear();
            datePickerView.setMinYear(minYear);
        }

        if (initDate != null) {
            int initYear = initDate.getPersianYear();
            if (initYear > maxYear || initYear < minYear) {
                Timber.tag("PERSIAN CALENDAR").e("init year is more/less than minYear/maxYear");
                if (forceMode) {
                    datePickerView.setDisplayPersianDate(initDate);
                }
            } else {
                datePickerView.setDisplayPersianDate(initDate);
            }

        }

        if (typeFace != null) {
            dateText.setTypeface(typeFace);
            positiveButton.setTypeface(typeFace);
            negativeButton.setTypeface(typeFace);
            todayButton.setTypeface(typeFace);
            datePickerView.setTypeFace(typeFace);
        }

        positiveButton.setTextSize(actionTextSize);
        negativeButton.setTextSize(negativeTextSize);
        todayButton.setTextSize(todayTextSize);

        positiveButton.setTextColor(actionColor);
        negativeButton.setTextColor(actionColor);
        todayButton.setTextColor(actionColor);

        positiveButton.setText(positiveButtonString);
        negativeButton.setText(negativeButtonString);
        todayButton.setText(todayButtonString);

        if (todayButtonVisibility) {
            todayButton.setVisibility(View.VISIBLE);
        }

        updateView(dateText, datePickerView.getPersianDate());

        datePickerView.setOnDateChangedListener(new MyPersianDatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(int newYear, int newMonth, int newDay) {
                updateView(dateText, datePickerView.getPersianDate());
            }
        });


        final AppCompatDialog dialog;
        if (showInBottomSheet) {
            dialog = new BottomSheetDialog(context,    R.style.BottomSheetDialog);
            dialog.setContentView(v);
            dialog.setCancelable(cancelable);
        } else {
            dialog = new AlertDialog.Builder(context)
                    .setView(v)
                    .setCancelable(cancelable)
                    .create();
        }

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onDismissed();
                }
                dialog.dismiss();
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // for backward compatibility, still support this
                if (listener != null) {
                    listener.onDateSelected(datePickerView.getDisplayPersianDate());
                }

                if (MyPersianPickerListener != null) {
                    MyPersianPickerListener.onDateSelected(datePickerView.getPersianDate());
                }
                dialog.dismiss();
            }
        });

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerView.setDisplayDate(new Date());

                if (maxYear > 0) {
                    datePickerView.setMaxYear(maxYear);
                }

                if (minYear > 0) {
                    datePickerView.setMinYear(minYear);
                }

                dateText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateView(dateText, datePickerView.getPersianDate());
                    }
                }, 100);
            }
        });

        dialog.show();
    }

    private void updateView(TextView dateText, MyPersianPickerDate persianDate) {
        String date;
        switch (titleType) {
            case NO_TITLE:
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dateText.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, 30);
                dateText.setLayoutParams(layoutParams);
                break;
            case DAY_MONTH_YEAR:
                date = persianDate.getPersianDay() + " " +
                        persianDate.getPersianMonthName() + " " +
                        persianDate.getPersianYear();

                dateText.setText(MyPersianHelper.toPersianNumber(date));
                break;
            case WEEKDAY_DAY_MONTH_YEAR:
                date = persianDate.getPersianDayOfWeekName() + " " +
                        persianDate.getPersianDay() + " " +
                        persianDate.getPersianMonthName() + " " +
                        persianDate.getPersianYear();
                dateText.setText(MyPersianHelper.toPersianNumber(date));
                break;
            case MONTH_YEAR:
                date = persianDate.getPersianMonthName() + " " +
                        persianDate.getPersianYear();
                dateText.setText(MyPersianHelper.toPersianNumber(date));
                break;
            default:
                Timber.tag("PersianDatePickerDialog").d("never should be here");
                break;
        }

    }

    public String getTimeString(MyPersianPickerDate datePicker) {
        String persianDayStr = "";
        if (datePicker.getPersianDay() > 0 && datePicker.getPersianDay() < 10) {
            persianDayStr = "0" + datePicker.getPersianDay();
        } else {
            persianDayStr = datePicker.getPersianDay()+"";
        }
        String persianMonth = "";
        if (datePicker.getPersianMonth() > 0 && datePicker.getPersianMonth() < 10) {
            persianMonth = "0" + datePicker.getPersianMonth();
        } else {
            persianMonth = datePicker.getPersianMonth()+"";
        }
        return datePicker.getPersianYear() +"/"+persianMonth+"/"+persianDayStr;

    }
}