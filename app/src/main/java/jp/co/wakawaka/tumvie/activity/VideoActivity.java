package jp.co.wakawaka.tumvie.activity;

import jp.co.wakawaka.tumvie.R;
import jp.co.wakawaka.tumvie.searchlist.CustomProgressDialog;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        final CustomProgressDialog progressDialog = new CustomProgressDialog(this);
        progressDialog.setCancelable(false);

        progressDialog.show();
        String videoUrl = getIntent().getStringExtra("videoUrl");
        VideoView video = (VideoView) findViewById(R.id.videoView);
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                progressDialog.dismiss();
                mediaPlayer.start();
                View placeholder = (View) findViewById(R.id.placeholder);
                placeholder.setVisibility(View.GONE);
            }
        });
        video.setVideoURI(Uri.parse(videoUrl));
        video.setMediaController(new MediaController(this));
        video.requestFocus();
    }
}
