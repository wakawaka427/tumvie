package jp.co.wakawaka.tumvie.favoritelist;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wakabayashieisuke on 2016/07/05.
 */
public class Favorite extends RealmObject {
    @PrimaryKey
    private long id;
    private String postId;
    private String sourceBlogName;
    private String videoThumbnailUrl;
    private String caption;
    private String videoUrl;
    private long currentTimeMillis;

    public void setValue(String postId, String sourceBlogName, String videoThumbnailUrl, String caption, String videoUrl) {
        this.id = Realm.getDefaultInstance().where(Favorite.class).max("id").intValue() + 1;
        this.postId = postId;
        this.sourceBlogName = sourceBlogName;
        this.videoThumbnailUrl = videoThumbnailUrl;
        this.caption = caption;
        this.videoUrl = videoUrl;
        this.currentTimeMillis = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public String getPostId() {
        return postId;
    }

    public String getSourceBlogName() {
        return sourceBlogName;
    }

    public String getVideoCaptionThumbnailUrl() {
        return videoThumbnailUrl;
    }

    public String getCaption() {
        return caption;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }
}
