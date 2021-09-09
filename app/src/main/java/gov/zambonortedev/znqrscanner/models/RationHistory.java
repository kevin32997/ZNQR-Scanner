package gov.zambonortedev.znqrscanner.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RationHistory {

    @SerializedName("created_at")
    @Expose
    private String ration_date;

    @SerializedName("claimant")
    @Expose
    private String claimant;

    public String getRation_date() {
        return ration_date;
    }

    public void setRation_date(String ration_date) {
        this.ration_date = ration_date;
    }

    public String getClaimant() {
        return claimant;
    }

    public void setClaimant(String claimant) {
        this.claimant = claimant;
    }
}
