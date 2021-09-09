package gov.zambonortedev.znqrscanner.task;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import gov.zambonortedev.znqrscanner.MainActivity;
import gov.zambonortedev.znqrscanner.R;
import gov.zambonortedev.znqrscanner.api.CheckQrCodeAPI;
import gov.zambonortedev.znqrscanner.helpers.DBHelper;
import gov.zambonortedev.znqrscanner.helpers.Helper;
import gov.zambonortedev.znqrscanner.models.Member;
import gov.zambonortedev.znqrscanner.models.Person;
import gov.zambonortedev.znqrscanner.models.QRResponse;
import gov.zambonortedev.znqrscanner.models.RationHistory;
import okhttp3.MediaType;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckQrCodeTask extends AsyncTask<String, Void, Void> {

    private static final String TAG = "CheckQrCodeTask";

    private Context context;
    private MainActivity mainActivity;
    private Dialog dialog;
    private Handler mainHandler;
    private DBHelper db;

    public CheckQrCodeTask(Context context, DBHelper db, Dialog dialog, Handler mainHandler) {
        this.context = context;
        mainActivity = (MainActivity) context;
        this.dialog = dialog;
        this.mainHandler = mainHandler;
        this.db = db;
    }

    @Override
    protected Void doInBackground(String... strings) {
        checkQRCodeOnline(strings[0], Integer.parseInt(strings[1]));
        return null;
    }

    private void checkQRCodeOnline(String qrcode, int mode) {
        try {
            //Create retrofit bodies
            RequestBody key = RequestBody.create(MediaType.parse("text/plain"), Helper.HashString("dcermapikey"));
            RequestBody qrcode_data = RequestBody.create(MediaType.parse("text/plain"), qrcode);


            Log.d(TAG, "checkQRCodeOnline: Code to Request: " + qrcode);
            Log.d(TAG, "checkQRCodeOnline: Mode: "+mode);
            //The gson builder
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            //creating retrofit object
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(db.getBaseURL())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            // MODE RC
            //creating our api
            CheckQrCodeAPI api = retrofit.create(CheckQrCodeAPI.class);
            if (mode == MainActivity.CODE_RC) {
                Call<QRResponse> call = api.checkQRCodeRC(
                        key,
                        qrcode_data);

                call.enqueue(new Callback<QRResponse>() {
                    @Override
                    public void onResponse(Call<QRResponse> call, Response<QRResponse> response) {
                        final Person person = response.body().getPerson();
                        final List<RationHistory> rationHistories = response.body().getRationHistories();
                        Log.d(TAG, "onResponse: Response Raw "+response.raw().body().toString());
                        Log.d(TAG, "onResponse: Response Body "+response.body().toString());
                        Log.d(TAG, "onResponse: Response Code "+response.code());
                        Log.d(TAG, "onResponse: Person: "+person.getName());
                        Log.d(TAG, "onResponse: Ration size: "+rationHistories.size());

                        // Set list in Main Activity
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mainActivity.setPassInformation(person, 1);
                                mainActivity.setListRation(rationHistories);
                                dialog.dismiss();
                                mainActivity.setInformationLabel("FAMILY RATION INFORMATION");
                            }
                        });


                    }

                    @Override
                    public void onFailure(Call<QRResponse> call, Throwable t) {
                        Log.d(TAG, "onFailure: Error " + t.getMessage());
                        t.printStackTrace();
                        if (t instanceof UnknownHostException) {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    Toast.makeText(mainActivity, "Unknown Host! Can't find server.", Toast.LENGTH_LONG).show();
                                    mainActivity.setPassInformation(null, 0);
                                    mainActivity.setListRation(new ArrayList<RationHistory>());
                                }
                            });

                        } else if (t instanceof ConnectException) {

                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    Toast.makeText(mainActivity, "Failed to connect! Please check Internet Connection.", Toast.LENGTH_LONG).show();
                                    mainActivity.setPassInformation(null, 0);
                                    mainActivity.setListRation(new ArrayList<RationHistory>());
                                }
                            });
                        } else if (t instanceof SocketTimeoutException) {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    Toast.makeText(mainActivity, "Server is not responding! Please try again later.", Toast.LENGTH_LONG).show();
                                    mainActivity.setPassInformation(null, 0);
                                    mainActivity.setListRation(new ArrayList<RationHistory>());
                                }
                            });
                        } else {

                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    Toast.makeText(mainActivity, "An error has occurred. Please try again later.", Toast.LENGTH_LONG).show();
                                    mainActivity.setPassInformation(null, 1);
                                    mainActivity.setListRation(new ArrayList<RationHistory>());
                                }
                            });
                        }
                    }
                });

            }
            // MODE QP
            else {

                Call<QRResponse> call = api.checkQRCodeQP(
                        key,
                        qrcode_data);

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView dialog_text = dialog.findViewById(R.id.dialog_text2);
                        dialog_text.setText("Downloading Data, Please wait . .");
                    }
                });

                call.enqueue(new Callback<QRResponse>() {
                    @Override
                    public void onResponse(Call<QRResponse> call, Response<QRResponse> response) {
                        final Person person = response.body().getPerson();
                        final List<Member> members = response.body().getMembers();

                        // Set list in Main Activity
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mainActivity.setPassInformation(person, 1);
                                mainActivity.setListMember(members);
                                dialog.dismiss();
                                mainActivity.setInformationLabel("PASS INFORMATION");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<QRResponse> call, Throwable t) {
                        Log.d(TAG, "onFailure: Error " + t.getMessage());
                        t.printStackTrace();
                        if (t instanceof UnknownHostException) {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    Toast.makeText(mainActivity, "Unknown Host! Can't find server.", Toast.LENGTH_LONG).show();
                                    mainActivity.setPassInformation(null, 0);
                                    mainActivity.setListMember(new ArrayList<Member>());
                                }
                            });

                        } else if (t instanceof ConnectException) {

                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    Toast.makeText(mainActivity, "Failed to connect! Please check Internet Connection.", Toast.LENGTH_LONG).show();
                                    mainActivity.setPassInformation(null, 0);
                                    mainActivity.setListMember(new ArrayList<Member>());
                                }
                            });
                        } else if (t instanceof SocketTimeoutException) {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    Toast.makeText(mainActivity, "Server is not responding! Please try again later.", Toast.LENGTH_LONG).show();
                                    mainActivity.setPassInformation(null, 0);
                                    mainActivity.setListMember(new ArrayList<Member>());
                                }
                            });
                        } else {

                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    Toast.makeText(mainActivity, "An error has occurred. Please try again later.", Toast.LENGTH_LONG).show();
                                    mainActivity.setPassInformation(null, 1);
                                    mainActivity.setListMember(new ArrayList<Member>());
                                }
                            });
                        }
                    }
                });

            }


        } catch (IllegalArgumentException ex) {

            // Invalid URL FORMAT
            ex.printStackTrace();
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Toast.makeText(mainActivity, "Invalid URL Format! Note: URL must end with '/' ", Toast.LENGTH_LONG).show();
                    mainActivity.setPassInformation(null, 1);
                    mainActivity.setListMember(new ArrayList<Member>());
                }
            });


        } catch (Exception ex) {

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Toast.makeText(mainActivity, "An error has occurred. Please try again later.", Toast.LENGTH_LONG).show();
                    mainActivity.setPassInformation(null, 1);
                    mainActivity.setListMember(new ArrayList<Member>());
                }
            });
        }
    }

}
