package edu.wit.mobileapp.opencv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import static edu.wit.mobileapp.opencv.Login.UserEmail;
import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "";
    public ImageView imageView;
    public Button btnScan;
    public TextView txtResult;
    public EditText txtcost;
    public EditText txtdate;
    public EditText txtname;


    String finalResult ;


    String HttpURL = "http://35.196.62.65/mobile/AddItem.php";
    Boolean CheckEditText ;
    ProgressDialog progressDialog;
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();

    public String email;
    public TextView helloemail;
    public Bitmap myBitmap;
    private TextRecognizer textRecognizer;
    private String RecDate ="nodatefound";
    public String someString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         email = getIntent().getExtras().getString(UserEmail);



        imageView = (ImageView)findViewById(R.id.imageView);
        btnScan = (Button) findViewById(R.id.btnScan);
        txtcost = (EditText) findViewById(R.id.recieptCost);
        txtdate = (EditText)findViewById(R.id.recieptDate);
        txtname = (EditText)findViewById(R.id.recieptName);


        helloemail = (TextView)findViewById(R.id.helloname);
        helloemail.setText("Welcome " + email);

        textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        myBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.sampler);
        imageView.setImageBitmap(myBitmap);


    }

    public Double stringToNum(String totals) {
        totals= totals.toUpperCase();
        totals= totals.replace("G","6");
        totals= totals.replace("Q","9");
        totals= totals.replace("S","5");
        totals= totals.replace("L","1");
        totals= totals.replace("O","0");
        String[] parts= totals.split("\\.");
        Double x;
        Double y;

        try {
            if(parts.length == 1)
            {
                x = Double.valueOf(parts[0]);
                y = 0.0;
            }
            else {
                x = Double.valueOf(parts[0]);
                y = Double.valueOf(parts[1]);
            }
        }
        catch(NumberFormatException e)
        {
             x = 0.0;
             y = 0.0;
        }
        return (x+(y/100));
    }

    public void doThis(View view) {
        txtdate.setText("");
        txtcost.setText("");
        txtname.setText("");

        if(!textRecognizer.isOperational())
        {
            Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();

        }
        else
        {
            Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
            SparseArray<TextBlock> textBlocks = textRecognizer.detect(frame);
            String blocks = "";
            String lines = "";
            String words = "";
            ArrayList<Double> numbers =new ArrayList<Double>();
            for (int index = 0; index < textBlocks.size(); index++) {
                //extract scanned text blocks here
                TextBlock tBlock = textBlocks.valueAt(index);
                blocks = blocks + tBlock.getValue() + "\n" + "\n";
                for (Text line : tBlock.getComponents()) {
                    //extract scanned text lines here
                    lines = lines + line.getValue() + "\n";
                    for (Text element : line.getComponents()) {
                        //extract scanned text words here
                        words = words + element.getValue() + ", ";

                        // gets largest integer value on screen
                        if (element.getValue().contains(".") == true && element.getValue().length() > 1 )
                        {
                            if (element.getValue().contains("$") == true)
                            {
                                String temp = element.getValue().substring(1);
                                Double nums = stringToNum(temp);
                                numbers.add(nums);
                            }
                            else
                            {
                                Double nums = stringToNum(element.getValue());
                                numbers.add(nums);
                            }
                        }

                        //  gets the date
                        if ((element.getValue().contains("/") == true ||  element.getValue().contains("\\") == true) && element.getValue().length() > 1 )
                        {
                            int count = 0;
                            if(element.getValue().contains("/") == true)
                            {
                                someString = element.getValue();
                                char someChar = '/';


                                for (int i = 0; i < someString.length(); i++) {
                                    if (someString.charAt(i) == someChar) {
                                        count++;
                                    }
                                }
                            }
                            else if(element.getValue().contains("\\") == true)
                            {
                                someString = element.getValue();
                                char someChar = '\\';


                                for (int i = 0; i < someString.length(); i++) {
                                    if (someString.charAt(i) == someChar) {
                                        count++;
                                    }
                                }
                            }

                            if (count == 2)
                            {
                                if(isValidDate1(someString)==true || isValidDate2(someString)==true || isValidDate3(someString)==true || isValidDate4(someString)==true)
                                {
                                    RecDate = someString;
                                }
                            }
                        }





                    }
                }
            }
            if (textBlocks.size() == 0) {
                txtResult.setText("Scan Failed: Found nothing to scan");
            } else {

                if( RecDate == "nodatefound")
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    RecDate = sdf.format(new Date());
                }

                txtcost.setText(Collections.max(numbers).toString());
                txtdate.setText(RecDate);


            }
        }

        //textRecognizer.release();
    }


    public void changepic(View view) {
        myBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.amerr);
        imageView.setImageBitmap(myBitmap);

    }

    public void changepic2(View view) {
        myBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.swissr);
        imageView.setImageBitmap(myBitmap);
    }

    public void changepic3(View view) {
        myBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.sampler);
        imageView.setImageBitmap(myBitmap);

    }
    public boolean isValidDate1(String dateString) {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        try {
            df.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean isValidDate2(String dateString) {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
        try {
            df.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean isValidDate3(String dateString) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            df.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean isValidDate4(String dateString) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
        try {
            df.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public void dono(View view) {
        Toast.makeText(MainActivity.this, "Lets correct the data", Toast.LENGTH_SHORT).show();
        txtdate.setText("");
        txtcost.setText("");
        txtname.setText("");

    }

    public void doyes(View view) {
        if(txtname.getText().toString().isEmpty() || txtcost.getText().toString().isEmpty() || txtdate.getText().toString().isEmpty())
        {
            Toast.makeText(MainActivity.this, "Fill in blank fields", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(MainActivity.this, "Sending to Server", Toast.LENGTH_SHORT).show();
            String name = txtname.getText().toString();
            String cost = "-" + txtcost.getText().toString();
            String[] dateparts = txtdate.getText().toString().split("/");
            String month = getMonth(parseInt(dateparts[0])).toLowerCase();
            String day = dateparts[1];


            AddItemFunction(email, month, day, name, cost);

        }




    }


    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1].toLowerCase();
    }

    public void takepicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        myBitmap = (Bitmap)data.getExtras().get("data");
        imageView.setImageBitmap(myBitmap);
    }


    public void AddItemFunction(final String email, final String month, final String day, final String itemname, final String itemvalue){

        class AddItemClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(MainActivity.this,"Sending Data",null,true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                if(httpResponseMsg.equalsIgnoreCase("Item Successfully Added.")){

                    txtdate.setText("");
                    txtcost.setText("");
                    txtname.setText("");

                }
                else{

                    Toast.makeText(MainActivity.this, httpResponseMsg,Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("month",params[0]);

                hashMap.put("email",params[1]);

                hashMap.put("day",params[2]);

                hashMap.put("itemname",params[3]);

                hashMap.put("itemvalue",params[4]);

                finalResult = httpParse.postRequest(hashMap, HttpURL);

                return finalResult;
            }
        }

        AddItemClass userLoginClass = new AddItemClass();

        userLoginClass.execute(month,email,day, itemname, itemvalue);
    }

    public void logout(View view) {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }
}
