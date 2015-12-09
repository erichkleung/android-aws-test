package com.fivestars.awstest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
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
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.Grantee;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;

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

        String uid = "eric";
        final String bucketName = "awesomestartup-user-photos";
        String idPool = "us-east-1:21a69e7a-2dc0-4aef-a7dc-4426aff37d35";

        final DeveloperAuthenticationProvider developerProvider = new DeveloperAuthenticationProvider(
                uid,
                idPool, // Identity Pool ID
                Regions.US_EAST_1);

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                developerProvider,
                Regions.US_EAST_1);


        Log.d("testing", credentialsProvider.toString());

        final AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        final TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                /*
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_OK);
                */

                final TextView tv = (TextView) findViewById(R.id.tv_hello_world);

                Log.d("testing", "before upload");

                TransferObserver observer = transferUtility.upload(
                        bucketName,     /* The bucket to upload to */
                        developerProvider.getIdentityId(),  /* The key for the uploaded object */
                        //"abc",
                        f      /* The file where the data to upload exists */
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
                            /*Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    s3.setObjectAcl(bucketName, developerProvider.getIdentityId(), CannedAccessControlList.PublicRead);
                                }
                            };
                            AsyncTask.execute(r);
                            */
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
                        Log.d("testing!!", String.valueOf(id));
                        Log.d("testing!!", ex.getMessage());
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
