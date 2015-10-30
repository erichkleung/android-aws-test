package com.fivestars.awstest;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final File f = new File("data/data/com.fivestars.awstest/test4.txt");
        Log.d("testing", f.toString());
        Log.d("testing", f.getPath());
        if (!f.exists()) {
            try {
                f.createNewFile();
                PrintWriter writer = new PrintWriter("data/data/com.fivestars.awstest/test4.txt", "UTF-8");
                for (int i = 0; i < 100000; i++) {
                    writer.println("The first line");
                    writer.println("The second line");
                }
                writer.close();
            } catch (IOException e) {
                //meh
            }
        }


        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:882fddd0-eebe-4611-a265-b47ce9d8fd98", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );
        Log.d("testing", credentialsProvider.toString());

        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        Log.d("testing", s3.toString());


        //s3.createBucket("fivestars-android-test2");
        final TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
        Log.d("testing", transferUtility.toString());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_OK);

                final TextView tv = (TextView) findViewById(R.id.tv_hello_world);

                Log.d("testing", "before upload");
                TransferObserver observer = transferUtility.upload(
                        "fivestars-android-test",     /* The bucket to upload to */
                        "test-file",    /* The key for the uploaded object */
                        f        /* The file where the data to upload exists */
                );
                Log.d("testing", "after upload");

                Log.d("testing", String.valueOf(observer.getBytesTotal()));
                Log.d("testing", String.valueOf(observer.getBytesTransferred()));
                Log.d("testing", observer.getState().toString());

                observer.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state.equals(TransferState.COMPLETED)) {
                            Snackbar.make(view, "all fucking done", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        Log.d("testing", String.valueOf(id));
                        Log.d("testing", state.toString());
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        Log.d("testing", String.valueOf(bytesCurrent));
                        tv.setText(String.format("%d/%d: %f", bytesCurrent, bytesTotal, (float) bytesCurrent / (float) bytesTotal));
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        Log.d("testing", String.valueOf(id));
                        ex.printStackTrace();
                    }
                });
                observer.refresh();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
