package com.example.distancereader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    String URL = "http://distance-saver.herokuapp.com/distance";

    String distance;
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }


    @Override
    protected void onStart(){
        super.onStart();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try  {
                            distance = getDistance();
                            runOnUiThread(new Runnable(){
                                @Override
                                public void run(){
                                    TextView textView = (TextView) findViewById(R.id.hematextbox);
                                    textView.setText(distance);

                                    t1.speak(distance, TextToSpeech.QUEUE_FLUSH, null);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();



                //your method
            }
        }, 0, 1000);//put here time 1000 milliseconds=1 second
    }


    String getDistance(){
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(URL));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                String responseString = out.toString();
                out.close();
                //..more logic
                JSONObject obj = new JSONObject(responseString);
                return obj.getJSONObject("data").getString("distance");

            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        }
        catch(Exception e){
            Log.e("DIST", e.toString());
            return "-1";
        }
    }
}