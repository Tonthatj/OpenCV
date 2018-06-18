package edu.wit.mobileapp.opencv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    private EditText musername;
    private EditText mpassword;
    String finalResult ;
    String HttpURL = "http://35.196.62.65/mobile/UserLogin.php";
    Boolean CheckEditText ;
    ProgressDialog progressDialog;
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    public static final String UserEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        musername = findViewById(R.id.signinname);
        mpassword = findViewById(R.id.signinpass);
    }

    public void gotoMain(View view) {

        String EmailHolder = musername.getText().toString();
        String PasswordHolder = mpassword.getText().toString();

        if (EmailHolder.isEmpty() || PasswordHolder.isEmpty())
        {
            Toast.makeText(Login.this, "Fill in Text Fields" ,Toast.LENGTH_LONG).show();
        }
        else {
            UserLoginFunction(EmailHolder, PasswordHolder);
        }



        //Intent intent = new Intent(Login.this,MainActivity.class);
        //startActivity(intent);

    }

    public void UserLoginFunction(final String email, final String password){

        class UserLoginClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(Login.this,"Loading Data",null,true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                if(httpResponseMsg.equalsIgnoreCase("Login Successful")){

                    finish();

                    Intent intent = new Intent(Login.this, MainActivity.class);

                    intent.putExtra(UserEmail,email);

                    startActivity(intent);

                }
                else{

                    Toast.makeText(Login.this, httpResponseMsg + "didnt work" ,Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("email",params[0]);

                hashMap.put("password",params[1]);

                finalResult = httpParse.postRequest(hashMap, HttpURL);

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(email,password);
    }



}

