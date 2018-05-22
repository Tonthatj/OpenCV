package edu.wit.mobileapp.opencv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.IOUtils;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.TextAnnotation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "";
    public ImageView imageView;
    public Button btnScan;
    public TextView txtResult;
    public Bitmap myBitmap;
    private TextRecognizer textRecognizer;
    private String RecDate ="nodatefound";
    public String someString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);
        btnScan = (Button) findViewById(R.id.btnScan);
        txtResult = (TextView)findViewById(R.id.txtResult);

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
        txtResult.setText("");

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

                txtResult.setText(txtResult.getText() + "----------" + "\n");
                txtResult.setText(txtResult.getText() + "Total is: " + "\n");
                txtResult.setText(txtResult.getText() + Collections.max(numbers).toString() + "\n");
                txtResult.setText(txtResult.getText() + "Date is: " + "\n");
                txtResult.setText(txtResult.getText() + RecDate + "\n");
                txtResult.setText(txtResult.getText() + "----------" + "\n");

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
        txtResult.setText("");

    }

    public void doyes(View view) {
        Toast.makeText(MainActivity.this, "Sending to Server", Toast.LENGTH_SHORT).show();
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
}
