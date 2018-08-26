package co.alizay.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.RectF;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.alizay.calendar.calendar_view.data.DateTimeInterpreter;
import co.alizay.calendar.calendar_view.month.MonthLoader;
import co.alizay.calendar.calendar_view.week.WeekView;
import co.alizay.calendar.calendar_view.week.WeekViewEvent;
import co.alizay.calendar.db.AppDatabase;
import co.alizay.calendar.db.DBController;
import co.alizay.calendar.db.DataGenerator;
import co.alizay.calendar.db.entity.CareGiverEntity;
import co.alizay.calendar.db.entity.WorkEntity;
import co.alizay.calendar.dialog.AddCareGiverDialog;
import co.alizay.calendar.helpers.AppExecutors;
import co.alizay.calendar.helpers.Util;

public class MainActivity extends AppCompatActivity implements WeekView.EventClickListener,
        MonthLoader.MonthChangeListener, WeekView.EventLongPressListener,
        WeekView.EmptyViewLongPressListener, DataGenerator.IDataGeneratorListener {

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_WEEK_VIEW = 2;
    private int mWeekViewType = TYPE_DAY_VIEW;
    private WeekView mWeekView;

    Context mContext;
    Activity mActivity;
    AppDatabase mAppDatabase;
    AppExecutors appExecutors;

    List<WeekViewEvent> events = new ArrayList<>();
    List<WorkEntity> workEntities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mActivity = this;

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.calendarWeekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);

        mAppDatabase = ((App) this.getApplication()).getDatabase();
        appExecutors = new AppExecutors();

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.addEvengMain);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCareGiver(Calendar.getInstance());
            }
        });

        DataGenerator dataGenerator = new DataGenerator(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("DataPopulated", false)) {
            dataGenerator.populateData(mAppDatabase, appExecutors);
            // run your one time code here
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("DataPopulated", true);
            editor.commit();
        } else {
            dataLoadingDone();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id){
            case R.id.action_today:
                mWeekView.goToToday();
                return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            10, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            10, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.authfill:
                appExecutors.diskIO().execute(() -> {
                    mAppDatabase.runInTransaction(new Runnable() {
                        @Override
                        public void run() {
                            DBController.autoFillWithoutAnd(mAppDatabase);
                            mActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    mWeekView.goToToday();
                                    Calendar current = Calendar.getInstance();
                                    mWeekView.getMonthChangeListener().onMonthChange(current.get(Calendar.YEAR), current.get(Calendar.MONTH) + 1);
                                }
                            });
                        }
                    });
                });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        dataLoadingDone();
    }

    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
//        Toast.makeText(this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
        for(WorkEntity w: workEntities) {
            if (w.getId() == event.getId()) {
                Intent i = new Intent(this, EventDetailActivity.class);
                i.putExtra("WorkEvent", w);
                startActivity(i);
            }
        }
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
//        Toast.makeText(this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
//        getEventTitle(time)
//        Toast.makeText(this, "Empty view long pressed: " + time.getTime().toString(), Toast.LENGTH_SHORT).show();
        addCareGiver(time);
    }

    public WeekView getWeekView() {
        return mWeekView;
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        System.out.println("NewYear: " + newYear + " NewMonth: " + newMonth + " No of events: " + events.size());
        return events;
    }

    public void addCareGiver(Calendar time) {
        Intent i = new Intent(this, AddCareGiverActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("TIME", time.getTimeInMillis());
        i.putExtras(bundle);
        startActivity(i);
    }

    @Override
    public void dataLoadingDone() {
        events = new ArrayList<>();
        appExecutors.diskIO().execute(() -> {
            workEntities = new ArrayList<>();
            workEntities = mAppDatabase.daoWork().loadAll();
            if(workEntities.size() > 0) {
                mAppDatabase.runInTransaction(new Runnable() {
                    @Override
                    public void run() {
                        for (WorkEntity w : workEntities) {
                            CareGiverEntity careGiver = mAppDatabase.daoCareGiver().loadCareGiver(w.getCareGiverId());

                            Calendar startTime = Calendar.getInstance();
                            startTime.setTimeInMillis(w.getStartTime());
                            Calendar endTime = (Calendar) startTime.clone();
                            endTime.setTimeInMillis(w.getEndTime());

                            WeekViewEvent event = new WeekViewEvent(w.getId(), careGiver.getName().getFirst()
                                    + " " + careGiver.getName().getLast().substring(0, 1), startTime,
                                    endTime, w.getRoom() + "", careGiver.getPicture().getThumbnail());
//                            System.out.println("Util.getColor() " + Util.getColor());
                            event.setColor(Util.getColor());
                            events.add(event);
                        }

                        mActivity.runOnUiThread(new Runnable() {
                            public void run() {
//                                Calendar current = Calendar.getInstance();
//                                mWeekView.getMonthChangeListener().onMonthChange(current.get(Calendar.YEAR), current.get(Calendar.MONTH) + 1);
                                mWeekView.goToToday();
                            }
                        });
                    }
                });
            }
        });
    }
}
