package jp.co.wakawaka.tumvie.player;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.MediaController;

/**
 * Created by wakabayashieisuke on 2016/07/06.
 */
public class KeyCompatibleMediaController  extends MediaController {

    private MediaController.MediaPlayerControl playerControl;

    public KeyCompatibleMediaController(Context context) {
        super(context);
    }

    @Override
    public void setMediaPlayer(MediaController.MediaPlayerControl playerControl) {
        super.setMediaPlayer(playerControl);
        this.playerControl = playerControl;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (playerControl.canSeekForward() && (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
                || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                playerControl.seekTo(playerControl.getCurrentPosition() + 15000); // milliseconds
                show();
            }
            return true;
        } else if (playerControl.canSeekBackward() && (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND
                || keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                playerControl.seekTo(playerControl.getCurrentPosition() - 5000); // milliseconds
                show();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
