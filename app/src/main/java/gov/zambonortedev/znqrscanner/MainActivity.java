package gov.zambonortedev.znqrscanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.List;

import gov.zambonortedev.znqrscanner.adapter.ListViewQPAdapter;
import gov.zambonortedev.znqrscanner.adapter.ListViewRCAdapter;
import gov.zambonortedev.znqrscanner.barcode.BarcodeCaptureActivity;
import gov.zambonortedev.znqrscanner.helpers.DBHelper;
import gov.zambonortedev.znqrscanner.models.Member;
import gov.zambonortedev.znqrscanner.models.Person;
import gov.zambonortedev.znqrscanner.models.RationHistory;
import gov.zambonortedev.znqrscanner.task.CheckConnectionTask;
import gov.zambonortedev.znqrscanner.task.CheckQrCodeTask;

public class MainActivity extends AppCompatActivity {

    public final static String BASE_URL = "https://www.google.com/";
    private static int BARCODE_READER_REQUEST_CODE = 1;

    // Views
    private ConstraintLayout btnCapture;
    private ListView mListview;

    private ImageView btnSettings;
    private TextView nodataText;

    // Pass Information View
    private TextView mainInformationLabel;
    private TextView passControlNo;
    private TextView passHouseholdID;
    private TextView passFullname;
    private TextView passBrgy;
    private TextView passAddress;
    private TextView passStatus;
    private ImageView passImageView;

    private DBHelper db;
    private Handler mainHandler;

    // Adapter
    private ListViewQPAdapter QPadapter;
    private ListViewRCAdapter RCadapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBHelper(MainActivity.this);
        mainHandler = new Handler(getMainLooper());

        // Button Scan
        btnCapture = findViewById(R.id.btn_scan_code);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });

        // Main Listview

        mListview = findViewById(R.id.mainListview);

        List<Member> members = new ArrayList<>();
        List<RationHistory> rationHistories = new ArrayList<>();
        //  members.add(new Member("MIP_1239087", "KEVIN DALMAN ERNAS", "RELOCATION SITE, LAOY, OLINGAN, DIPOLOG CITY", Member.TYPE_LEADER));
        //  members.add(new Member("MIP_1239233", "MACO CORTES", "LINGASAD, POLANCO, ZAMBOAGA DEL NORTE", Member.TYPE_MEMBER));

        // Initialize Adapter
        QPadapter = new ListViewQPAdapter(MainActivity.this, members);
        RCadapter = new ListViewRCAdapter(MainActivity.this,rationHistories);


        mListview.setAdapter(QPadapter);

        // Settings
        btnSettings = findViewById(R.id.btn_settings);

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_settings_layout);

                final TextView serverAddField = dialog.findViewById(R.id.settings_server_address);
                serverAddField.setText(db.getConfig("BASE_URL").getValue());

                dialog.show();

                Button connect = dialog.findViewById(R.id.settings_btn_connect);

                connect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!serverAddField.getText().toString().equals("")) {
                            ProgressBar progressBar = dialog.findViewById(R.id.settings_progress);
                            progressBar.setVisibility(View.VISIBLE);
                            TextView alert = dialog.findViewById(R.id.settings_alert);
                            alert.setVisibility(View.GONE);
                            new CheckConnectionTask(MainActivity.this, db, dialog, mainHandler).execute(serverAddField.getText().toString());
                        } else {
                            TextView alert = dialog.findViewById(R.id.settings_alert);
                            alert.setText("Field cannot be empty!");
                            alert.setTextColor(getResources().getColor(R.color.colorDanger));
                            alert.setVisibility(View.VISIBLE);
                            alert.setText(db.getBaseURL());
                        }
                    }
                });

                Button save = dialog.findViewById(R.id.settings_btn_save);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!serverAddField.getText().toString().equals("")) {
                            dialog.dismiss();
                            db.updateConfig("BASE_URL", serverAddField.getText().toString());
                            Toast.makeText(MainActivity.this, "URL Saved!", Toast.LENGTH_SHORT).show();
                        } else {
                            TextView alert = dialog.findViewById(R.id.settings_alert);
                            alert.setText("Field cannot be empty!");
                            alert.setTextColor(getResources().getColor(R.color.colorDanger));
                            alert.setVisibility(View.VISIBLE);
                            alert.setText(db.getBaseURL());
                        }
                    }
                });


                ImageView btnCancel = dialog.findViewById(R.id.settings_img_cancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        // No Data Available text
        nodataText = findViewById(R.id.main_no_data_text);

        // Pass Information View Init
        mainInformationLabel=findViewById(R.id.main_information);
        passControlNo = findViewById(R.id.main_control_no);
        passHouseholdID = findViewById(R.id.main_household_no);
        passFullname = findViewById(R.id.main_fullname);
        passBrgy = findViewById(R.id.main_barangay);
        passAddress = findViewById(R.id.main_address);
        passStatus = findViewById(R.id.main_validation_label);
        passImageView = findViewById(R.id.main_validation_img);


        // FOR DEBUGGING
        //db.updateConfig("BASE_URL", "http://10.10.100w.56/dipologcityemergencyreliefmanagement/");

    }

    private static final int STATUS_ONLINE = 1;
    private static final int STATUS_OFFLINE = 0;

    // Fills data for Pass Information view
    public void setPassInformation(Person person, int status) {
        if (status == STATUS_ONLINE) {

            if (person != null) {
                passControlNo.setText(person.getId());
                passHouseholdID.setText(person.getHousehold_id());
                passFullname.setText(person.getFullname());
                passBrgy.setText(person.getBarangay());
                passAddress.setText(person.getAddress());
                setStatus(Person.STATUS_VALID);

            } else {
                passControlNo.setText("No Available Info");
                passHouseholdID.setText("No Available Info");
                passFullname.setText("No Available Info");
                passBrgy.setText("No Available Info");
                passAddress.setText("No Available Info");
                setStatus(-1);

            }
        } else {
            passControlNo.setText("");
            passHouseholdID.setText("");
            passFullname.setText("");
            passBrgy.setText("");
            passAddress.setText("");
            setStatus(Person.STATUS_OFFLINE);
        }
    }

    // Set Image Status
    private void setStatus(int status) {
        passStatus.setVisibility(View.VISIBLE);
        passImageView.setVisibility(View.VISIBLE);
        switch (status) {
            case Person.STATUS_UNASSOCIATED:
                passStatus.setText("UNASSOCIATED");
                passStatus.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.background_warning));
                passImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning));
                break;
            case Person.STATUS_VALID:
                passStatus.setText("VALID");
                passStatus.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.background_success));
                passImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                break;
            case Person.STATUS_CANCEL:
                passStatus.setText("BLOCKED");
                passStatus.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.background_danger));
                passImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_cancel));
                break;
            case Person.STATUS_OFFLINE:
                passStatus.setText("CAN'T CONNECT");
                passStatus.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.background_plain));
                passImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_no_signal));
                break;
            default:
                passStatus.setText("INVALID");
                passStatus.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.background_danger));
                passImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_cancel));
                break;
        }

    }

    public void setInformationLabel(String text){
        mainInformationLabel.setText(text);
    }

    // Fills data for Household member listview
    public void setListMember(List<Member> list) {
        mListview.setAdapter(QPadapter);
        if (list != null) {
            if (list.size() > 0) {
                QPadapter.setList(list);
                nodataText.setVisibility(View.GONE);
            } else {
                QPadapter.setList(list);
                nodataText.setVisibility(View.VISIBLE);
            }
        } else {
            QPadapter.setList(new ArrayList<Member>());
            nodataText.setVisibility(View.VISIBLE);
        }
    }

    // Fills data for Household member listview
    public void setListRation(List<RationHistory> list) {
        mListview.setAdapter(RCadapter);

        if (list != null) {
            if (list.size() > 0) {
                RCadapter.setList(list);
                nodataText.setVisibility(View.GONE);
            } else {
                RCadapter.setList(list);
                nodataText.setVisibility(View.VISIBLE);
            }
        } else {
            QPadapter.setList(new ArrayList<Member>());
            nodataText.setVisibility(View.VISIBLE);
        }
    }

    public static final int CODE_QP = 0;
    public static final int CODE_RC = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.dialog_progression);
                    dialog.show();

                    String[] text_barcode = barcode.displayValue.split("-");

                    if (text_barcode.length > 1) {
                        if (text_barcode[0] != null && text_barcode[0].equals("RC")) {

                            new CheckQrCodeTask(MainActivity.this, db, dialog, mainHandler).execute(text_barcode[1], "" + CODE_RC);
                        } else {
                            new CheckQrCodeTask(MainActivity.this, db, dialog, mainHandler).execute(text_barcode[1], "" + CODE_QP);
                        }
                    }


                }
            } else {
                //error message
                Toast.makeText(this, "An Error has Occurred!", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
