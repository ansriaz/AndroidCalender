package co.alizay.calendar;

import android.app.Application;

import co.alizay.calendar.db.AppDatabase;
import co.alizay.calendar.db.DataGenerator;
import co.alizay.calendar.helpers.AppExecutors;

public class App extends Application {

    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppExecutors = new AppExecutors();

//        DataGenerator dataGenerator = new DataGenerator();
//        dataGenerator.populateData(getDatabase(), mAppExecutors);
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this, mAppExecutors);
    }
}
