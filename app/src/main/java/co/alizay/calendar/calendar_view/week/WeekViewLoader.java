package co.alizay.calendar.calendar_view.week;

import java.util.Calendar;
import java.util.List;

public interface WeekViewLoader {

    double toWeekViewPeriodIndex(Calendar instance);

    List<? extends WeekViewEvent> onLoad(int periodIndex);
}
