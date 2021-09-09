package gov.zambonortedev.znqrscanner.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class Person {

    public static final int STATUS_UNASSOCIATED=0;
    public static final int STATUS_VALID=1;
    public static final int STATUS_CANCEL=2;
    public static final int STATUS_OFFLINE=3;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("full_name")
    @Expose
    private String fullname;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("barangay")
    @Expose
    private String barangay;

    @SerializedName("household_id")
    @Expose
    private String household_id;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("address")
    @Expose
    private String address;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getHousehold_id() {
        return household_id;
    }

    public void setHousehold_id(String household_id) {
        this.household_id = household_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
