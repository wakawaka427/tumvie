package jp.co.wakawaka.tumvie;

import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.enableUrlBarHiding();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(BuildConfig.TUMBLR_OAUTH_URL));
        finish();
    }
}
