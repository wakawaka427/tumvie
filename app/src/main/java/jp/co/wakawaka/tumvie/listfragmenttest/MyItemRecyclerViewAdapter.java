package jp.co.wakawaka.tumvie.listfragmenttest;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;

import jp.co.wakawaka.tumvie.R;
import jp.co.wakawaka.tumvie.listfragmenttest.ItemFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Item} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Item> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context mContext;

    public MyItemRecyclerViewAdapter(List<Item> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mUserName.setText(mValues.get(position).sourceBlogName);
//        holder.mVideoCapture.setImageBitmap(mValues.get(position).videoCaptureImageBitmap);
        playVideo(holder, position);


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    private void playVideo(final ViewHolder holder, int position) {
        Item item = mValues.get(position);

        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        DataSource dataSource = new DefaultUriDataSource(mContext, "userAgent");
        SampleSource sampleSource = new ExtractorSampleSource(
                Uri.parse(item.videoUrl), dataSource, allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
        TrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(
                mContext, sampleSource, MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        TrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(
                sampleSource, MediaCodecSelector.DEFAULT);
        TrackRenderer[] rendererArray = {videoRenderer, audioRenderer};

        ExoPlayer exoPlayer = ExoPlayer.Factory.newInstance(rendererArray.length);
        exoPlayer.prepare(rendererArray);
        exoPlayer.sendMessage(videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, holder.mVideoSurface.getHolder().getSurface());
        exoPlayer.setPlayWhenReady(true);
    }

    private void _playVideo(final ViewHolder holder, int position) {
        holder.mThumbnail.setImageBitmap(mValues.get(position).videoThumbnailBitmap);
        holder.mTestVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                holder.mThumbnail.setVisibility(View.GONE);
                mp.start();
            }
        });
//        holder.mTestVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//            }
//        });

        holder.mTestVideo.setVideoURI(Uri.parse(holder.mItem.videoUrl));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mUserName;
        public final ImageView mThumbnail;
        public final VideoView mTestVideo;
        public final SurfaceView mVideoSurface;
        public Item mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mUserName = (TextView) view.findViewById(R.id.user_name);
            mThumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            mTestVideo = (VideoView) view.findViewById(R.id.test_video);
            mVideoSurface = (SurfaceView) view.findViewById(R.id.video_surface);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
