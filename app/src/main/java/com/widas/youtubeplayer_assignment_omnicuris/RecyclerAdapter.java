package com.widas.youtubeplayer_assignment_omnicuris;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import static com.widas.youtubeplayer_assignment_omnicuris.MainActivity.API_KEY;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.VideoInfoHolder> {

    //these ids are the unique id for each video
    String[] VideoID = {"U-pCzco4OXE", "Y9VgmhxtJFk", "pxoIufVFjOk", "Qp390kefqm4","pnYcKvGIagI","go70IsJ2WZ8"};
    String[] Ttitles = {"Song", "Amitabh-Corona", "Corona Message", "Fitness", "Cricket","KabirSingh"};
    Context ctx;
    LoadVodeofromList loadVodeofromList;

    public RecyclerAdapter(Context context,  LoadVodeofromList loadVodeofromList) {
        this.ctx = context;
        this.loadVodeofromList = loadVodeofromList;
    }

    @Override
    public VideoInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new VideoInfoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VideoInfoHolder holder, final int position) {


        final YouTubeThumbnailLoader.OnThumbnailLoadedListener onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
            @Override
            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

            }

            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                youTubeThumbnailView.setVisibility(View.VISIBLE);
                holder.relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
            }
        };

        holder.youTubeThumbnailView.initialize(API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {

                youTubeThumbnailLoader.setVideo(VideoID[position]);

                youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
                holder.videosTitleTextView.setText(Ttitles[position]);
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                //write something for failure
            }
        });
    }

    @Override
    public int getItemCount() {
        return VideoID.length;
    }

    public class VideoInfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        YouTubeThumbnailView youTubeThumbnailView;
        protected ImageView playButton;
        protected TextView videosTitleTextView;

        public VideoInfoHolder(View itemView) {
            super(itemView);
            playButton = (ImageView) itemView.findViewById(R.id.btnYoutube_player);
            videosTitleTextView = (TextView) itemView.findViewById(R.id.videosTitle_tv);
            playButton.setOnClickListener(this);
            relativeLayoutOverYouTubeThumbnailView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_thumbnail);
        }

        @Override
        public void onClick(View v) {
            if (loadVodeofromList != null) {

                loadVodeofromList.passvideoURL(VideoID[getLayoutPosition()]);
            }


          //  Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) ctx, API_KEY, VideoID[getLayoutPosition()]);
          //  ctx.startActivity(intent);
        }
    }
    interface  LoadVodeofromList {
        void passvideoURL(String url);
    }
}