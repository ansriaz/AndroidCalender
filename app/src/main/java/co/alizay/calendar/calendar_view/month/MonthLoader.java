package co.alizay.calendar.calendar_view.month;

import java.util.Calendar;
import java.util.List;

import co.alizay.calendar.calendar_view.week.WeekViewEvent;
import co.alizay.calendar.calendar_view.week.WeekViewLoader;

public class MonthLoader implements WeekViewLoader {

    private MonthChangeListener mOnMonthChangeListener;

    public MonthLoader(MonthChangeListener listener){
        this.mOnMonthChangeListener = listener;
    }

    @Override
    public double toWeekViewPeriodIndex(Calendar instance){
        return instance.get(Calendar.YEAR) * 12 + instance.get(Calendar.MONTH) + (instance.get(Calendar.DAY_OF_MONTH) - 1) / 30.0;
    }

    @Override
    public List<? extends WeekViewEvent> onLoad(int periodIndex){
        return mOnMonthChangeListener.onMonthChange(periodIndex / 12, periodIndex % 12 + 1);
    }

    public MonthChangeListener getOnMonthChangeListener() {
        return mOnMonthChangeListener;
    }

    public void setOnMonthChangeListener(MonthChangeListener onMonthChangeListener) {
        this.mOnMonthChangeListener = onMonthChangeListener;
    }

    public interface MonthChangeListener {
        List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth);
    }
}
