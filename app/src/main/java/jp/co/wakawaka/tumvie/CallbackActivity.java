package jp.co.wakawaka.tumvie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.User;

public class CallbackActivity extends AppCompatActivity {

    public String mToken;
    public String mSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callback);

        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");
        mSecret = intent.getStringExtra("secret");

        JumblrClient jumblrClient = new JumblrClient(
                BuildConfig.CONSUMER_KEY
                , BuildConfig.CONSUMER_SECRET
                , mToken
                , mSecret
        );

        User user = jumblrClient.user();

        TextView name = (TextView) findViewById(R.id.name);
        name.setText(user.getName());
    }
}
