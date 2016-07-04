package jp.co.wakawaka.tumvie;

import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.authentication).setOnClickListener(authenticationOnClickListener);
        findViewById(R.id.rightnow).setOnClickListener(rightnowOnClickListener);
    }

    View.OnClickListener authenticationOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Token.existToken(MainActivity.this)) {
                Intent intent = new Intent(MainActivity.this, CallbackActivity.class);
                startActivity(intent);
            } else {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.enableUrlBarHiding();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(MainActivity.this, Uri.parse(BuildConfig.TUMBLR_OAUTH_URL));
            }
            MainActivity.this.finish();
        }
    };

    View.OnClickListener rightnowOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "coming soon!", Toast.LENGTH_SHORT).show();
        }
    };
}
