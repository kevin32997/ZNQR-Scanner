package gov.zambonortedev.znqrscanner.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QRResponse {

    @SerializedName("person")
    @Expose
    private Person person;

    @SerializedName("members")
    @Expose
    private List<Member> members;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }


    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }


    @SerializedName("ration_history")
    public List<RationHistory> rationHistories;

    public List<RationHistory> getRationHistories() {
        return rationHistories;
    }

    public void setRationHistories(List<RationHistory> rationHistories) {
        this.rationHistories = rationHistories;
    }

    @Override
    public String toString() {
        return "QRResponse{" +
                "person=" + person +
                ", members=" + members +
                ", rationHistories=" + rationHistories +
                '}';
    }
}
