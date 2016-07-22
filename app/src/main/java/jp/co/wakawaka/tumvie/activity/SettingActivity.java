package jp.co.wakawaka.tumvie.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import jp.co.wakawaka.tumvie.R;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    /**
     * 利用規約クリック処理
     * @param view
     */
    public void onClickTerms(View view) {
        startActivity(new Intent(this, TermsActivity.class));
    }

    /**
     * ライセンスクリック処理
     * @param view View
     */
    public void onClickLicense(View view) {
        startActivity(new Intent(this, LicenseActivity.class));
    }
}
