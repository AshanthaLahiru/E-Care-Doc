package com.example.ashantha.ecaredoc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    JSONParser jsonParser = new JSONParser();

    ArrayList<Patient> patients=new ArrayList<Patient>();
    LinearLayout myLinearLayout;
    int count=0;
    ScrollView sv;
    List<TextView> myList=new ArrayList<TextView>();
    List<TextView> myHBList=new ArrayList<TextView>();

    TextView patientStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myLinearLayout = (LinearLayout) findViewById(R.id.ll1);
        sv = (ScrollView)findViewById(R.id.sv1);

        patientStatus=(TextView) findViewById(R.id.patientStatus);

        callAsynchronousTask();



    }


    public void callAsynchronousTask()
    {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try
                        {

                            new MainActivity.AuthenticateLogin().execute();
                        }
                        catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 3000); //execute in every 50000 ms
    }

    public  void showPatients(){

        patientStatus.setText("Status : Normal");

        for (int i = 0; i < patients.size(); i++) {
            // create a new textview
            final TextView rowTextView = new TextView(this);
            final TextView rowHB = new TextView(this);
            final TextView space = new TextView(this);
            // set some properties of rowTextView or something

            rowHB.setTextColor(Color.parseColor("#ffffff"));
            rowTextView.setTextColor(Color.parseColor("#ffffff"));
            rowTextView.setText(patients.get(i).pName);
            rowTextView.setTextSize(30);
            rowHB.setText("\t\t\tHeratBeat:" +patients.get(i).pHB+" BPM \n\t\t\tPressure: "+ patients.get(i).pPressure+" mm Hg");
            rowHB.setTextSize(18);
            space.setText("  ");
            space.setTextSize(30);

            if(Integer.parseInt(patients.get(i).pHB)>100 || Integer.parseInt(patients.get(i).pHB)<60 || Integer.parseInt(patients.get(i).pPressure)>120 || Integer.parseInt(patients.get(i).pPressure)<90) {
                patientStatus.setText("Status : Critical Situation");
                rowTextView.setTextColor(Color.parseColor("#ff0000"));
                rowHB.setTextColor(Color.parseColor("#ff0000"));
            }
            /*if(Integer.parseInt(patients.get(i).pPressure)>120 || Integer.parseInt(patients.get(i).pPressure)<90) {
                visionStatus.setText("Status : Critical Situation");
                visionPressure.setTextColor(Color.parseColor("#ff0000"));
                visionStatus.setTextColor(Color.parseColor("#ff0000"));
            }*/

            //rowTextView.setId(i);
            // add the textview to the linearlayout
            myLinearLayout.addView(rowTextView);
            myLinearLayout.addView(rowHB);
            myLinearLayout.addView(space);

            myList.add(rowTextView);
            myHBList.add(rowHB);
            // save a reference to the textview for later

        }

    }

    public  void updatePatients(){

        patientStatus.setText("Status : Normal");

        for (int i = 0; i < myList.size(); i++) {
            // create a new textview




            TextView rowTextView=myList.get(i);
            TextView rowHB=myHBList.get(i);
            // set some properties of rowTextView or something

            rowHB.setTextColor(Color.parseColor("#ffffff"));
            rowTextView.setTextColor(Color.parseColor("#ffffff"));
            rowTextView.setText(patients.get(i).pName);
            rowHB.setText("\t\t\tHeratBeat:" +patients.get(i).pHB+" BPM \n\t\t\tPressure: "+ patients.get(i).pPressure+" mm Hg");

            // save a reference to the textview for later

            if(Integer.parseInt(patients.get(i).pHB)>100 || Integer.parseInt(patients.get(i).pHB)<60 || Integer.parseInt(patients.get(i).pPressure)>120 || Integer.parseInt(patients.get(i).pPressure)<90) {
                patientStatus.setText("Status : Critical Situation");
                rowTextView.setTextColor(Color.parseColor("#ff0000"));
                rowHB.setTextColor(Color.parseColor("#ff0000"));
            }

        }

    }

    class AuthenticateLogin extends AsyncTask<String, String, String>
    {
        String alert;
        String success;

        @Override
        protected String doInBackground(String... args)
        {
            String API_URL="http://testapi.moracodex.com/GetPatients.php";

            List<NameValuePair> params = new ArrayList<NameValuePair>();
        /*
            params.add(new BasicNameValuePair("email",email));
            params.add(new BasicNameValuePair("password",password));
        */

            try
            {
                JSONObject json = jsonParser.makeHttpRequest(API_URL,"POST", params);

                JSONArray a = json.getJSONArray("patient");

                    patients.clear();


                for (int i = 0; i < a.length(); i++) {

                    Patient tempPatient=new Patient(a.getJSONObject(i).getString("name"),a.getJSONObject(i).getString("hb"),a.getJSONObject(i).getString("pressure"));
                    System.out.println("Patient----- "+a.getJSONObject(i).getString("name") );
                    patients.add(tempPatient);

                    String hb = a.getJSONObject(i).getString("hb");
                    String pres = a.getJSONObject(i).getString("pressure");
                    if((Integer.parseInt(hb)>100 || Integer.parseInt(hb)<60) || (Integer.parseInt(pres)>120 || Integer.parseInt(pres)<90))
                    {

                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            r.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }



                }



            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {



            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(count==0) {
                        showPatients();
                        count++;
                    }
                    else
                        updatePatients();
//stuff that updates ui

                }
            });

        }
    }



    public void OpenDialogBox()
    {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Invalid Credentials")
                .setMessage("Entered email or password is incorrect!")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }



/*
    public void Register(View view){

        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }*/


}

class Patient{

    public String pName;
    public String pHB;
    public String pPressure;


    public Patient(String name,String hb,String pre){
        pName=name;
        pHB=hb;
        pPressure=pre;

    }

}

