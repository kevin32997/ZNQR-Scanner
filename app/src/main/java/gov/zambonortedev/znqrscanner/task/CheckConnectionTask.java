package gov.zambonortedev.znqrscanner.task;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.ConnectException;
import java.net.UnknownHostException;

import gov.zambonortedev.znqrscanner.R;
import gov.zambonortedev.znqrscanner.api.helloAPI;
import gov.zambonortedev.znqrscanner.helpers.DBHelper;
import gov.zambonortedev.znqrscanner.helpers.Helper;
import gov.zambonortedev.znqrscanner.models.ServerResponse;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckConnectionTask extends AsyncTask<String, Void, Void> {
    private static final String TAG = "CheckConnectionTask";

    private Context context;
    private DBHelper dbHelper;
    private Dialog dialog;
    private Handler mainHandler;

    public CheckConnectionTask(Context context, DBHelper dbHelper, Dialog dialog, Handler mainHandler) {

        this.context = context;
        this.dbHelper = dbHelper;
        this.dialog = dialog;
        this.mainHandler = mainHandler;
    }

    @Override
    protected Void doInBackground(String... strings) {
        checkConnectivity(strings[0]);
        return null;
    }


    private void checkConnectivity(final String url) {
        Log.d(TAG, "onResponse: I AM HERE!!!!");
        try {

            RequestBody key = RequestBody.create(MediaType.parse("text/plain"), Helper.HashString("dcermapikey"));
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            helloAPI helloAPI = retrofit.create(helloAPI.class);
            Call<ServerResponse> call = helloAPI.checkConnection(key);
            Log.d(TAG, "onResponse: I AM HERE!!!!");
            call.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, final Response<ServerResponse> response) {
                    Log.d(TAG, "onResponse: I AM HERE2!!!!");
                    if (response.body() != null) {

                        ServerResponse serverResponse = response.body();
                        if (serverResponse.getStatus() != null) {

                            if (serverResponse.getStatus().equals("true")) {
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        ProgressBar progressBar = dialog.findViewById(R.id.settings_progress);
                                        progressBar.setVisibility(View.GONE);
                                        TextView textView = dialog.findViewById(R.id.settings_alert);
                                        textView.setTextColor(context.getResources().getColor(R.color.colorDarkSuccess));
                                        textView.setText("Connected");
                                        textView.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                //Show Error

                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        ProgressBar progressBar = dialog.findViewById(R.id.settings_progress);
                                        progressBar.setVisibility(View.GONE);
                                        TextView textView = dialog.findViewById(R.id.settings_alert);
                                        textView.setTextColor(context.getResources().getColor(R.color.colorDanger));
                                        textView.setText("Can't connect to server!");
                                        textView.setVisibility(View.VISIBLE);

                                    }
                                });
                            }
                        } else {


                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressBar progressBar = dialog.findViewById(R.id.settings_progress);
                                    progressBar.setVisibility(View.GONE);
                                    TextView textView = dialog.findViewById(R.id.settings_alert);
                                    textView.setTextColor(context.getResources().getColor(R.color.colorDanger));
                                    textView.setText("Unautorize Access");
                                    textView.setVisibility(View.VISIBLE);
                                }
                            });

                        }
                    } else {
                        if (response.code() == 404) {

                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressBar progressBar = dialog.findViewById(R.id.settings_progress);
                                    progressBar.setVisibility(View.GONE);
                                    TextView textView = dialog.findViewById(R.id.settings_alert);
                                    textView.setTextColor(context.getResources().getColor(R.color.colorDanger));
                                    textView.setText("Server not found! (" + response.code() + ")");
                                    textView.setVisibility(View.VISIBLE);

                                }
                            });
                        }

                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure:Cause " + (Exception) t);

                    if(t instanceof UnknownHostException){
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ProgressBar progressBar = dialog.findViewById(R.id.settings_progress);
                                progressBar.setVisibility(View.GONE);
                                TextView textView = dialog.findViewById(R.id.settings_alert);
                                textView.setTextColor(context.getResources().getColor(R.color.colorDanger));
                                textView.setText("Unknown Host! Can't find Server.");
                                textView.setVisibility(View.VISIBLE);
                            }
                        });

                    }else if(t instanceof ConnectException){

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ProgressBar progressBar = dialog.findViewById(R.id.settings_progress);
                                progressBar.setVisibility(View.GONE);
                                TextView textView = dialog.findViewById(R.id.settings_alert);
                                textView.setTextColor(context.getResources().getColor(R.color.colorDanger));
                                textView.setText("Can't connect to server, Please check internet connection.");
                                textView.setVisibility(View.VISIBLE);
                            }
                        });
                    }else{

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ProgressBar progressBar = dialog.findViewById(R.id.settings_progress);
                                progressBar.setVisibility(View.GONE);
                                TextView textView = dialog.findViewById(R.id.settings_alert);
                                textView.setTextColor(context.getResources().getColor(R.color.colorDanger));
                                textView.setText("An error has occured. Please try again later.");
                                textView.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                }
            });


        } catch (IllegalArgumentException ex) {

            // Invalid URL FORMAT
            ex.printStackTrace();

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    ProgressBar progressBar = dialog.findViewById(R.id.settings_progress);
                    progressBar.setVisibility(View.GONE);
                    TextView textView = dialog.findViewById(R.id.settings_alert);
                    textView.setTextColor(context.getResources().getColor(R.color.colorDanger));
                    textView.setText("Invalid URL Format! Note: URL must end with '/' ");
                    textView.setVisibility(View.VISIBLE);
                }
            });

        } catch (Exception ex) {

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    ProgressBar progressBar = dialog.findViewById(R.id.settings_progress);
                    progressBar.setVisibility(View.GONE);
                    TextView textView = dialog.findViewById(R.id.settings_alert);
                    textView.setTextColor(context.getResources().getColor(R.color.colorDanger));
                    textView.setText("An error has occured. Please try again later.");
                    textView.setVisibility(View.VISIBLE);
                }
            });

        }
    }
}
