package co.alizay.calendar.api_service;

import co.alizay.calendar.models.APIResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ICareGiversService {

    //https://randomuser.me/api/?seed=empatica&page=1&results=10
    @GET("/api/?seed=empatica&page=1&results=100")
    Call<APIResponse> ListCareGivers();

}

