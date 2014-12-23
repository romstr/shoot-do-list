package lv.tsi.romstr.todolist.sharing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executor;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Roman on 13.12.14..
 */
public class TwitterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer("FTWBzjrYgMp9Nr0psEjMRuZl3", "Yfn2w14TZ7VUbjmJYC6VEYWtbcdqD7oqr34mkm5svAxTAR6VQf");
        RequestToken requestToken = null;
        try {
            requestToken = twitter.getOAuthRequestToken();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        AccessToken accessToken = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (null == accessToken) {
            System.out.println("Open the following URL and grant access to your account:");
            System.out.println(requestToken.getAuthorizationURL());
            System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
            String pin = null;
            try {
                pin = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                if(pin.length() > 0){
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                }else{
                    accessToken = twitter.getOAuthAccessToken();
                }
            } catch (TwitterException te) {
                if(401 == te.getStatusCode()){
                    System.out.println("Unable to get the access token.");
                }else{
                    te.printStackTrace();
                }
            }
        }
        */
        Intent intent = getIntent();
        final String latestStatus = intent.getStringExtra("status");
        final String photoPath = intent.getStringExtra("path");
        File image = null;
        if (photoPath != null) {
            image = new File(photoPath);
        }

        if (android.os.Build.VERSION.SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("consumer-key-here")
                .setOAuthConsumerSecret("consumer-secret-here")
                .setOAuthAccessToken("access-token-here")
                .setOAuthAccessTokenSecret("access-token-secret-here");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();


        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {


            try {
                StatusUpdate status = new StatusUpdate(latestStatus);
                if (image != null) {
                    status.setMedia(image);
                }
                twitter.updateStatus(status);
                //Status status = twitter.updateStatus(latestStatus);
                setResult(RESULT_OK);
                System.out.println("-----------------------------------" + status);
                finish();
            } catch (TwitterException e) {
                setResult(RESULT_CANCELED);
                e.printStackTrace();
                finish();
            }
        } else {
            finish();
        }
    }
}
