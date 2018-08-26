package co.alizay.calendar;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import co.alizay.calendar.db.AppDatabase;
import co.alizay.calendar.db.entity.CareGiverEntity;
import co.alizay.calendar.db.entity.WorkEntity;
import co.alizay.calendar.helpers.AppExecutors;
import co.alizay.calendar.models.CareGiver;

public class EventDetailActivity extends AppCompatActivity {

    Context mContext;
    Activity mActivity;
    TextView tvPatientNameEventDetail, tvRoomNoEventDetail, tvCareGiverNameEventDetail;
    TextView tvStartTimeEventDetail, tvEndTimeEventDetail;
    Button btnEdit, btnDelete;
    ImageView imageEventDetail;
    Calendar selectedDate;
    WorkEntity workEntity;

    private Application mApplication = null;
    private AppDatabase mAppDatabase = null;
    private AppExecutors appExecutors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        mApplication = this.getApplication();
        mAppDatabase = ((App) mApplication).getDatabase();

        appExecutors = new AppExecutors();

        selectedDate = Calendar.getInstance();
        workEntity = (WorkEntity) getIntent().getSerializableExtra("WorkEvent");
        System.out.println(workEntity.toString());

        getEventDetail();

        mContext = this;
        mActivity = this;

        tvPatientNameEventDetail = (TextView) findViewById(R.id.tvPatientNameEventDetail);
        tvRoomNoEventDetail = (TextView) findViewById(R.id.tvRoomNoEventDetail);
        tvCareGiverNameEventDetail = (TextView) findViewById(R.id.tvCareGiverNameEventDetail);
        tvStartTimeEventDetail = (TextView) findViewById(R.id.tvStartTimeEventDetail);
        tvEndTimeEventDetail = (TextView) findViewById(R.id.tvEndTimeEventDetail);
        imageEventDetail = (ImageView) findViewById(R.id.imageEventDetail);
        btnEdit = (Button) findViewById(R.id.btnEditAddCareGiverEventDetail);
        btnDelete = (Button) findViewById(R.id.btnDeleteAddCareGiverEventDetail);

        tvPatientNameEventDetail.setText(workEntity.getPatientName());
        tvRoomNoEventDetail.setText(workEntity.getRoom() + "");

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY hh:mm");

        cal.setTimeInMillis(workEntity.getStartTime());
        tvStartTimeEventDetail.setText(dateFormat.format(cal.getTime()));
        cal.setTimeInMillis(workEntity.getEndTime());
        tvEndTimeEventDetail.setText(dateFormat.format(cal.getTime()));

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteWorkEvent();
            }
        });
    }

    void getEventDetail() {
        appExecutors.diskIO().execute(() -> {
            CareGiverEntity careGiverEntity = mAppDatabase.daoWork().loadCaregiverByWork(workEntity.getId());

            tvCareGiverNameEventDetail.setText(careGiverEntity.getName().getFirst() + " " + careGiverEntity.getName().getLast());
            System.out.println(careGiverEntity.getId() + " " + workEntity.toString());
            setImage(careGiverEntity.getPicture().getThumbnail());
        });

    }

    void deleteWorkEvent() {
        if(workEntity != null) {
            appExecutors.diskIO().execute(() -> {
                mAppDatabase.daoWork().delete(workEntity);
                mActivity.finish();
            });
        }
    }

    void setImage(String imageUrl) {
        if(workEntity != null) {
            appExecutors.mainThread().execute(() -> {
                Picasso.with(this).load(imageUrl).into(imageEventDetail);
            });
        }
    }
}
