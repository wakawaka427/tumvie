package jp.co.wakawaka.tumvie.activity;

import jp.co.wakawaka.tumvie.R;
import jp.co.wakawaka.tumvie.searchlist.CustomProgressDialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
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
        video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
                errorDialogFragment.show(getFragmentManager(), "tag");
                return false;
            }
        });
        video.setVideoURI(Uri.parse(videoUrl));
        video.setMediaController(new MediaController(this));
        video.requestFocus();
    }

    public static class ErrorDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            return builder
                    .setMessage(getString(R.string.not_support_video_message))
                    .setPositiveButton(getString(R.string.not_support_video_message_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().finish();
                        }
                    })
                    .create();
        }
    }
}
