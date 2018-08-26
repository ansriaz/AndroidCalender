package co.alizay.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import co.alizay.calendar.adapter.SearchViewAdapter;
import co.alizay.calendar.api_service.ICareGiversService;
import co.alizay.calendar.db.AppDatabase;
import co.alizay.calendar.db.DBController;
import co.alizay.calendar.db.entity.WorkEntity;
import co.alizay.calendar.helpers.AppExecutors;
import co.alizay.calendar.models.APIResponse;
import co.alizay.calendar.models.CareGiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCareGiverActivity extends AppCompatActivity implements SearchViewAdapter.OnItemClickListener {

    Context mContext;
    Activity mActivity;
    List<CareGiver> careGiverList = new ArrayList<>();
    SearchViewAdapter searchViewAdapter;
    RecyclerView recyclerView;
    EditText editTextCareGiver, editTextPatientName, editTextRoomNumber;
    Button btnAdd, btnCancel, btnStartDate, btnEndDate;
    CareGiver selectedCareGiver;
    Calendar selectedDate, endDate;

    boolean isStartDateSelected = false;

    private Application mApplication = null;
    private AppDatabase mAppDatabase = null;
    private AppExecutors appExecutors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_caregiver);

        mApplication = this.getApplication();
        mAppDatabase = ((App) mApplication).getDatabase();

        getCareGiversData();

        appExecutors = new AppExecutors();

        Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty()) {
            long time = bundle.getLong("TIME");
            System.out.println("time =====> " + time);
            selectedDate = Calendar.getInstance();
            selectedDate.setTimeInMillis(time);
        }

        endDate = selectedDate;

        mContext = this;
        mActivity = this;
        searchViewAdapter = new SearchViewAdapter(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        editTextCareGiver = (EditText) findViewById(R.id.etCaregiver);
        editTextPatientName = (EditText) findViewById(R.id.etPatientName);
        editTextRoomNumber = (EditText) findViewById(R.id.etRoomNumber);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setVisibility(View.INVISIBLE);
        recyclerView.setAdapter(searchViewAdapter);

        btnAdd = (Button) findViewById(R.id.btnAddAddCareGiver);
        btnCancel = (Button) findViewById(R.id.btnCancelAddCareGiver);
        btnStartDate = (Button) findViewById(R.id.btnStartDateAddCareGiver);
        btnEndDate = (Button) findViewById(R.id.btnEndDateAddCareGiver);

        editTextCareGiver.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                System.out.println(charSequence.toString());
//                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                System.out.println(editable.toString());
                filter(editable.toString());
            }
        });

        editTextRoomNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editTextRoomNumber.getText().toString() != null && !editTextRoomNumber.getText().toString().equals("")) {
                    if (Integer.parseInt(editTextRoomNumber.getText().toString()) < 1
                            || Integer.parseInt(editTextRoomNumber.getText().toString()) > 10) {
                        Toast.makeText(mContext, "Please choose the room number between 1-10", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedCareGiver != null) {

                    System.out.println("selectedDate.get(Calendar.HOUR_OF_DAY) " + selectedDate.get(Calendar.HOUR_OF_DAY));
                    if(selectedDate.get(Calendar.HOUR_OF_DAY) > 17) {
                        Toast.makeText(mContext, "Please choose the time between working hours (9AM-5PM)", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(selectedDate.get(Calendar.HOUR_OF_DAY) < 9) {
                        Toast.makeText(mContext, "Please choose the time between working hours (9AM-5PM)", Toast.LENGTH_LONG).show();
                        return;
                    }

                    WorkEntity workEntity = new WorkEntity();
                    workEntity.setPatientName(editTextPatientName.getText().toString());
                    workEntity.setRoom(Integer.parseInt(editTextRoomNumber.getText().toString()));

                    selectedDate.set(Calendar.MINUTE, 0);
                    selectedDate.set(Calendar.SECOND, 0);
                    selectedDate.set(Calendar.MILLISECOND, 0);

                    System.out.println("selectedDate.getTime().toString() => " + selectedDate.getTime().toString());
                    workEntity.setStartTime(selectedDate.getTimeInMillis());
                    Calendar endtime = (Calendar) selectedDate.clone();
                    endtime.set(Calendar.HOUR_OF_DAY, selectedDate.get(Calendar.HOUR_OF_DAY) + 1);
                    workEntity.setEndTime(endtime.getTimeInMillis());

                    long[] time = DBController.getWeekStartAndEnd(selectedDate);

                    appExecutors.diskIO().execute(() -> {

                        String result = DBController.addWork(mAppDatabase, workEntity, selectedDate,
                                selectedCareGiver);

                        if(result.equals("RoomNotAvailable")) {
                            mActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(mContext, workEntity.getRoom() +
                                                    " is not free at this time. Please select any other room between 1-10. "
                                            , Toast.LENGTH_LONG).show();
                                    return;
                                }
                            });
                            return;
                        } else if(result.equals("SaveAsOvertime")) {
                            saveAsOvertimeDialog(workEntity, time[0], time[1]);
                        } else if(result.equals("WorkAdded")) {
                            mActivity.finish();
                        }
                    });
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.finish();
            }
        });

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStartDateSelected = true;
                displayDatePicker(selectedDate);
            }
        });

        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStartDateSelected = false;
                displayDatePicker(endDate);
            }
        });
    }

    void getCareGiversData() {
        ICareGiversService service = APIController.getRetrofitInstance().create(ICareGiversService.class);
        Call<APIResponse> call = service.ListCareGivers();
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                if(response.isSuccessful()) {
                    APIResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        careGiverList = apiResponse.getCareGivers();
//                        careGiverList.forEach(cg -> System.out.println(cg.getName().getFirst()));
                        searchViewAdapter.setCareGivers(careGiverList);
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(mContext, t.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String text) {
        if(recyclerView.getVisibility() == View.INVISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
        }

        ArrayList<CareGiver> filtered = new ArrayList<>();

        if(careGiverList.size() > 0) {
            for (CareGiver cg : careGiverList) {
                if (cg.getName().getFirst().toLowerCase().contains(text.toLowerCase())
                        || cg.getName().getLast().toLowerCase().contains(text.toLowerCase())) {
                    filtered.add(cg);
                }
            }
        }

        searchViewAdapter.filterList(filtered);
    }

    @Override
    public void onItemClick(CareGiver careGiver) {
        if (careGiver != null) {
            System.out.println(careGiver.getName().getFirst());
            selectedCareGiver = careGiver;
            editTextCareGiver.setText(careGiver.getName().getFirst()
                    + " " + careGiver.getName().getLast());
        }
        recyclerView.setVisibility(View.INVISIBLE);
    }

    public void displayDatePicker(Calendar cal) {
        // Get Current Date
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);

        Calendar finalStartData = cal;
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

//                        txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        finalStartData.set(Calendar.YEAR, year);
                        finalStartData.set(Calendar.MONTH, monthOfYear);
                        finalStartData.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        displayTimePicker(finalStartData);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void displayTimePicker(Calendar cal) {
        // Get Current Date
        int mYear = cal.get(Calendar.HOUR_OF_DAY);
        int mMonth = cal.get(Calendar.MINUTE);

        Calendar finalStartDate = cal;
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        finalStartDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        finalStartDate.set(Calendar.MINUTE, 0);
                        finalStartDate.set(Calendar.SECOND, 0);
                        finalStartDate.set(Calendar.MILLISECOND, 0);

                        if(isStartDateSelected) {
                            Calendar temp = (Calendar) finalStartDate.clone();
                            temp.set(Calendar.HOUR_OF_DAY, hourOfDay + 1);
                            selectedDate = (Calendar) finalStartDate.clone();
                            endDate = temp;
                        } else {
                            Calendar temp = (Calendar) finalStartDate.clone();
                            temp.set(Calendar.HOUR_OF_DAY, hourOfDay - 1);
                            selectedDate = temp;
                            endDate = (Calendar) finalStartDate.clone();
                        }
                    }
                }, mYear, mMonth, true);
        timePickerDialog.show();
    }

    void saveAsOvertimeDialog(WorkEntity workEntity, long startTime, long endTime) {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Alert")
                    .setMessage("Selected care-giver has already finished his working hours of this week. " +
                            "Do you want to add as overtime?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            appExecutors.diskIO().execute(() -> {

                                String result = DBController.addOvertime(mAppDatabase, workEntity, startTime, endTime);

                                if(result.equals("OvertimeFinished")) {
                                    mActivity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(mContext, "Selected care-giver has finished his working hours of this week. " +
                                                    "Please select other caregiver. ", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    });
                                } else if (result.equals("OvertimeAdded")) {
                                    mActivity.finish();
                                }
                            });
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(mContext, "Please choose other care giver if you want to book. ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            }
        });
    }
}
