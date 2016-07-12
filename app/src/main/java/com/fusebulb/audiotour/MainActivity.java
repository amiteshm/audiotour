package com.fusebulb.audiotour;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;

import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import android.graphics.Color;

import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.net.Uri;
import java.io.BufferedReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MAIN_ACTVITY";
    public static final String HOST_NAME = "http://phitoor.com/fuseblub/";
    public static String APP_FOLDER = "";
    private NotificationManager mNotifierManager;
    private Builder mBuilder;
    int notificationId = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button buttonStart = (Button) findViewById(R.id.download_audio_button);
        buttonStart.setBackgroundColor(0xFFC82506);
        APP_FOLDER = getFilesDir().toString()+"/";



        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg) {
                Log.d(TAG, "Button clicked");

                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    mNotifierManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder = new NotificationCompat.Builder(MainActivity.this);
                    mBuilder.setContentTitle(("Download"))
                            .setContentText("Download in progress")
                            .setSmallIcon(R.drawable.ic_download);

                    new Downloader().execute();

                }else{
                    displayAlert();
                }
            }
        });
    }

    private class Downloader extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display the progress bar for the first time.
            // Disable the button
            // Change the text
            mBuilder.setProgress(0, 0, true);
            //determinate progress
            //mBuilder.setProgress(100, 0, false);
            mNotifierManager.notify(notificationId, mBuilder.build());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //update progress
            mBuilder.setProgress(100, values[0], false);
            mNotifierManager.notify(notificationId, mBuilder.build());
            super.onProgressUpdate(values);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Log.d(TAG, "Connected to network");
            MainActivity.downloadFile(HOST_NAME, APP_FOLDER, "tour_info.xml");
            XmlParser parser = new XmlParser();
            DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
            InputStream is;
            try {
                File fileIn= new File(APP_FOLDER, "tour_info.xml");
                is = new FileInputStream(fileIn);
                parser.parse(dbHelper, is);
                is.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            //Remove the progress bar
            mBuilder.setContentText("Download complete");
            mBuilder.setProgress(0, 0, false);
            mNotifierManager.notify(notificationId, mBuilder.build());
            try {

                File fileIn= new File(APP_FOLDER, "tour_info.xml");

                StringBuilder text = new StringBuilder();

                try {
                    BufferedReader br = new BufferedReader(new FileReader(fileIn));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();
                }
                catch (IOException e) {
                    //You'll need to add proper error handling here
                }

                Toast.makeText(getBaseContext(), text.toString(), Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    public static void downloadFile(String host_name, String app_folder,  String file_path){
        try{
            Log.e(TAG, host_name+file_path);
            URL url = new URL(host_name + file_path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.connect();


            File file = new File(app_folder + file_path);
            File mydir = file.getParentFile();

            if (!mydir.exists())
            {
                mydir.mkdirs();
            }


            FileOutputStream fileOutput = new FileOutputStream(file);

            //Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            int downloadedSize = 0;


            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;

            }
            //close the output stream when complete //
            fileOutput.close();

        }catch (final Exception e){
            //showError("Error in downloading the file. Please try again");
            e.printStackTrace();
        }
    }


//    private void showError(final String err){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(MainActivity.this, err, Toast.LENGTH_LONG);
//            }
//        });
//    }

    public void displayAlert()
    {
        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setMessage("Please connect to the internet")
                .setTitle("No Internet Connection")
                .setCancelable(true)
                .setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton){
                               // finish(); Finish is closing the app
                            }
                        })
                .show();

    }
}
