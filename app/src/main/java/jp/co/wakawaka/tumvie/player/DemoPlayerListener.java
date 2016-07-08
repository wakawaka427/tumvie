package jp.co.wakawaka.tumvie.player;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;

/**
 * Created by wakabayashieisuke on 2016/07/06.
 */
public class DemoPlayerListener implements DemoPlayer.Listener {
    private AspectRatioFrameLayout mAspectRatioFrameLayout;
    public DemoPlayerListener(AspectRatioFrameLayout aspectRatioFrameLayout) {
        mAspectRatioFrameLayout = aspectRatioFrameLayout;
    }
    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        String text = "playWhenReady=" + playWhenReady + ", playbackState=";
        switch(playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                break;
            case ExoPlayer.STATE_ENDED:
                text += "ended";
                break;
            case ExoPlayer.STATE_IDLE:
                text += "idle";
                break;
            case ExoPlayer.STATE_PREPARING:
                text += "preparing";
                break;
            case ExoPlayer.STATE_READY:
                text += "ready";
                break;
            default:
                text += "unknown";
                break;
        }
    }

    @Override
    public void onError(Exception e) {
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                   float pixelWidthAspectRatio) {
        mAspectRatioFrameLayout.setAspectRatio(
                height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
    }
}
