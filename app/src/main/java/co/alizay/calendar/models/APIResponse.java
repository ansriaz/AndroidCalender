package co.alizay.calendar.models;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.List;

public class APIResponse {

    @SerializedName("results")
    List<CareGiver> careGivers;

    @SerializedName("info")
    JSONObject info;

    public List<CareGiver> getCareGivers() {
        return careGivers;
    }

    public void setCareGivers(List<CareGiver> careGivers) {
        this.careGivers = careGivers;
    }

    public JSONObject getInfo() {
        return info;
    }

    public void setInfo(JSONObject info) {
        this.info = info;
    }
}
