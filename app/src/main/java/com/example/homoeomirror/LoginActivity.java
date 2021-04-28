package com.example.homoeomirror;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homoeomirror.ApiInterface;
import com.example.homoeomirror.CountryToPhonePrefix;
import com.example.homoeomirror.LoginResponse;
import com.example.homoeomirror.OtpReceivedInterface;
import com.example.homoeomirror.R;
import com.example.homoeomirror.RetrofitService;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity implements OtpReceivedInterface {

    String country;
    int countryPos;
    Spinner spinnerCountryName;
    EditText editTextPhone, editPhoneId;
    CountryToPhonePrefix countryToPhonePrefix;
    Button btnContinue;
    String phoneNumber,phoneId;
    Toolbar toolbar;
    ProgressDialog pDialog;
    TextView textView;

    private static final String TAG = "LoginActivity1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);





        countryToPhonePrefix = new CountryToPhonePrefix();

        btnContinue = findViewById(R.id.btnContinue);
        editPhoneId = findViewById(R.id.editPhoneId);
        editTextPhone = findViewById(R.id.editPhone1);
        spinnerCountryName=findViewById(R.id.spinnerCountry);
        textView=findViewById(R.id.text1);



        ArrayList<CountryToPhonePrefix> list = (ArrayList<CountryToPhonePrefix>) countryToPhonePrefix.getAll();
        ArrayAdapter<CountryToPhonePrefix> adapter =
                new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCountryName.setAdapter(adapter);

        spinnerCountryName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                country = spinnerCountryName.getSelectedItem().toString();
                countryPos = spinnerCountryName.getSelectedItemPosition();

                CountryToPhonePrefix categories = (CountryToPhonePrefix) parent.getItemAtPosition(position);
                editPhoneId.setText("+" + categories.getPrefix());


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        textView.setClickable(true);
        textView.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle("We will be Verifying the phone number:");
                alertDialog.setMessage("Is this OK,or would you like to edit the number?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "EDIT",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#0099FF"));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#0099FF"));

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
                layoutParams.weight = 10;
                btnPositive.setLayoutParams(layoutParams);
                btnNegative.setLayoutParams(layoutParams);
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber =editTextPhone.getText().toString().trim();
                phoneId=editPhoneId.getText().toString().trim();

                if (phoneNumber.trim().isEmpty()) {
                    editTextPhone.setError("Enter Phone No");
                } else {
                    /*Intent i = new Intent(LoginActivity.this, OtpActivity.class);
                    i.putExtra("phoneId", phoneId);
                    i.putExtra("phoneNumber", phoneNumber);
                    startActivity(i);*/

                    send_otp();

                }
            }
        });





        /*textView.setClickable(true);
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle("We will be Verifying the phone number:");
                alertDialog.setMessage("Is this OK,or would you like to edit the number?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "EDIT",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#0099FF"));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#0099FF"));

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
                layoutParams.weight = 10;
                btnPositive.setLayoutParams(layoutParams);
                btnNegative.setLayoutParams(layoutParams);
            }
        });*/

    }


    public void send_otp() {

        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        phoneId = editPhoneId.getText().toString().trim();
        phoneNumber = editTextPhone.getText().toString().trim();



        ApiInterface service = RetrofitService.getRetrofitInstance().create(ApiInterface.class);
        Call<LoginResponse> call = service.getOTP(phoneId,phoneNumber);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull retrofit2.Response<LoginResponse> response) {

                // Check if the Response is successful
                if (response.isSuccessful()){


                    LoginResponse users = response.body();

                    assert users != null;
                    if (users.getStatus().equalsIgnoreCase("true")){
                        assert response.body() != null;
                        pDialog.dismiss();

                        String otp = users.getValue();
                        Log.d(TAG,""+otp);
                        startSMSListener();
                    }else {
                        pDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Invalid", Toast.LENGTH_SHORT).show();
                    }



                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                pDialog.dismiss();
                Log.d("ResponseF",""+t);
            }
        });




    }

    @Override
    public void onOtpReceived(String otp) {
        //Toast.makeText(this, "Otp Received " + otp, Toast.LENGTH_LONG).show();
        Log.d(TAG,""+otp);
        //binding.editTextOTP.setText(otp);

    }



    @Override
    public void onOtpTimeout() {
        Toast.makeText(this, "Time out, please resend", Toast.LENGTH_LONG).show();
    }



    public void startSMSListener() {
        SmsRetrieverClient mClient = SmsRetriever.getClient(this);
        Task<Void> mTask = mClient.startSmsRetriever();
        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               /* layoutInput.setVisibility(View.GONE);
                layoutVerify.setVisibility(View.VISIBLE);*/
                Toast.makeText(LoginActivity.this, "Otp Sent Successfully", Toast.LENGTH_LONG).show();


            }
        });
        mTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }
}