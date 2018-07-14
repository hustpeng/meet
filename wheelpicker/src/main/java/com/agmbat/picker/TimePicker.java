package com.agmbat.picker;

import android.content.Context;

/**
 * 时间选择器
 */
public class TimePicker extends DateTimePicker {

    public TimePicker(Context context) {
        this(context, HOUR_24);
    }

    /**
     * @see #HOUR_24
     * @see #HOUR_12
     */
    public TimePicker(Context context, @TimeMode int mode) {
        super(context, NONE, mode);
    }

    /**
     * @deprecated use {@link #setLabel(String, String)} instead
     */
    @Deprecated
    @Override
    public final void setLabel(String yearLabel, String monthLabel, String dayLabel, String hourLabel, String minuteLabel) {
        super.setLabel(yearLabel, monthLabel, dayLabel, hourLabel, minuteLabel);
    }

    /**
     * 设置时间显示的单位
     */
    public void setLabel(String hourLabel, String minuteLabel) {
        super.setLabel("", "", "", hourLabel, minuteLabel);
    }

    /**
     * @deprecated nonsupport
     */
    @Deprecated
    @Override
    public final void setDateRangeStart(int startYear, int startMonth, int startDay) {
        throw new UnsupportedOperationException("Date range nonsupport");
    }

    /**
     * @deprecated nonsupport
     */
    @Deprecated
    @Override
    public final void setDateRangeEnd(int endYear, int endMonth, int endDay) {
        throw new UnsupportedOperationException("Date range nonsupport");
    }

    /**
     * @deprecated nonsupport
     */
    @Deprecated
    @Override
    public final void setDateRangeStart(int startYearOrMonth, int startMonthOrDay) {
        throw new UnsupportedOperationException("Date range nonsupport");
    }

    /**
     * @deprecated nonsupport
     */
    @Deprecated
    @Override
    public final void setDateRangeEnd(int endYearOrMonth, int endMonthOrDay) {
        throw new UnsupportedOperationException("Data range nonsupport");
    }

    /**
     * 设置范围：开始的时分
     */
    public void setRangeStart(int startHour, int startMinute) {
        super.setTimeRangeStart(startHour, startMinute);
    }

    /**
     * 设置范围：结束的时分
     */
    public void setRangeEnd(int endHour, int endMinute) {
        super.setTimeRangeEnd(endHour, endMinute);
    }

    /**
     * 设置默认选中的时间
     */
    public void setSelectedItem(int hour, int minute) {
        super.setSelectedItem(0, 0, hour, minute);
    }

    /**
     * 设置滑动监听器
     */
    public void setOnWheelListener(final OnWheelListener listener) {
        if (null == listener) {
            return;
        }
        super.setOnWheelListener(new DateTimePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
            }

            @Override
            public void onMonthWheeled(int index, String month) {
            }

            @Override
            public void onDayWheeled(int index, String day) {
            }

            @Override
            public void onHourWheeled(int index, String hour) {
                listener.onHourWheeled(index, hour);
            }

            @Override
            public void onMinuteWheeled(int index, String minute) {
                listener.onMinuteWheeled(index, minute);
            }
        });
    }

    public void setOnTimePickListener(final OnTimePickListener listener) {
        if (null == listener) {
            return;
        }
        super.setOnDateTimePickListener(new DateTimePicker.OnTimePickListener() {
            @Override
            public void onDateTimePicked(String hour, String minute) {
                listener.onTimePicked(hour, minute);
            }
        });
    }

    public interface OnTimePickListener {

        void onTimePicked(String hour, String minute);

    }

    public interface OnWheelListener {

        void onHourWheeled(int index, String hour);

        void onMinuteWheeled(int index, String minute);

    }

}
