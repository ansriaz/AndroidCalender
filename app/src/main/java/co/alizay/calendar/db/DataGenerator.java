package co.alizay.calendar.db;

import java.util.List;

import co.alizay.calendar.APIController;
import co.alizay.calendar.api_service.ICareGiversService;
import co.alizay.calendar.helpers.AppExecutors;
import co.alizay.calendar.models.APIResponse;
import co.alizay.calendar.models.CareGiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataGenerator {

    public interface IDataGeneratorListener {
        void dataLoadingDone();
    }

    private final IDataGeneratorListener iDataGeneratorListener;

    public String[] patients = new String[] {
            "Edurado Luca", "Lee Chang", "Master IP", "Tom Cruise", "Elsa of Arendelle",
            "Spencer Lacy", "Jessica Elba", "Scarlet Johanson", "Robert Downey Jr.", "Mark Ruffalo"
    };

    public DataGenerator(IDataGeneratorListener iDataGeneratorListener) {
        this.iDataGeneratorListener = iDataGeneratorListener;
    }

    public void populateData(AppDatabase mAppDatabase, AppExecutors appExecutors) {

        ICareGiversService service = APIController.getRetrofitInstance().create(ICareGiversService.class);
        Call<APIResponse> call = service.ListCareGivers();

        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                if(response.isSuccessful()) {
                    APIResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        List<CareGiver> careGiverList = apiResponse.getCareGivers();

                        appExecutors.diskIO().execute(() -> {
                            DBController.generateData(mAppDatabase, patients, careGiverList);
                            iDataGeneratorListener.dataLoadingDone();
                        });

                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                t.printStackTrace();
                System.out.println(t.getMessage());
            }
        });
    }

}
