package com.appdev.schoudhary.wittylife.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;


@Entity(tableName = "destination_result")
public class Result implements Parcelable {

    public final static Parcelable.Creator<Result> CREATOR = new Creator<Result>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        public Result[] newArray(int size) {
            return (new Result[size]);
        }

    };

    @PrimaryKey
    @ColumnInfo(name = "unsplash_id")
    @NonNull
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("width")
    @Expose
    private Integer width;
    @SerializedName("height")
    @Expose
    private Integer height;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("description")
    @Expose
    @Ignore
    private Object description;
    @SerializedName("alt_description")
    @Expose
    private String altDescription;
    @SerializedName("urls")
    @Expose
    private Urls urls;
    @SerializedName("links")
    @Expose
    @Ignore
    private Links links;
    @SerializedName("categories")
    @Expose
    @Ignore
    private List<Object> categories = null;
    @SerializedName("sponsored")
    @Expose
    private Boolean sponsored;
    @SerializedName("sponsored_by")
    @Expose
    @Ignore
    private Object sponsoredBy;
    @SerializedName("sponsored_impressions_id")
    @Expose
    @Ignore
    private Object sponsoredImpressionsId;
    @SerializedName("likes")
    @Expose
    private Integer likes;
    @SerializedName("liked_by_user")
    @Expose
    @Ignore
    private Boolean likedByUser;
    @SerializedName("current_user_collections")
    @Expose
    @Ignore
    private List<Object> currentUserCollections = null;
    @SerializedName("user")
    @Expose
    @Ignore
    private User user;
    @SerializedName("tags")
    @Expose
    @Ignore
    private List<Tag> tags = null;
    @SerializedName("photo_tags")
    @Expose
    @Ignore
    private List<PhotoTag> photoTags = null;

    @Ignore
    private Result(Parcel in) {
        this.id = ((String) Objects.requireNonNull(in.readValue((String.class.getClassLoader()))));
        this.createdAt = ((String) in.readValue((String.class.getClassLoader())));
        this.updatedAt = ((String) in.readValue((String.class.getClassLoader())));
        this.width = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.height = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.color = ((String) in.readValue((String.class.getClassLoader())));
        this.description = ((Object) in.readValue((Object.class.getClassLoader())));
        this.altDescription = ((String) in.readValue((String.class.getClassLoader())));
        this.urls = ((Urls) in.readValue((Urls.class.getClassLoader())));
        this.links = ((Links) in.readValue((Links.class.getClassLoader())));
        in.readList(this.categories, (java.lang.Object.class.getClassLoader()));
        this.sponsored = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.sponsoredBy = ((Object) in.readValue((Object.class.getClassLoader())));
        this.sponsoredImpressionsId = ((Object) in.readValue((Object.class.getClassLoader())));
        this.likes = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.likedByUser = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        in.readList(this.currentUserCollections, (java.lang.Object.class.getClassLoader()));
        this.user = ((User) in.readValue((User.class.getClassLoader())));
        in.readList(this.tags, (com.appdev.schoudhary.wittylife.model.Tag.class.getClassLoader()));
        in.readList(this.photoTags, (com.appdev.schoudhary.wittylife.model.PhotoTag.class.getClassLoader()));
    }

    /**
     * @param tags
     * @param currentUserCollections
     * @param urls
     * @param width
     * @param altDescription
     * @param links
     * @param id
     * @param updatedAt
     * @param height
     * @param color
     * @param createdAt
     * @param description
     * @param likes
     * @param photoTags
     * @param sponsoredImpressionsId
     * @param categories
     * @param likedByUser
     * @param sponsoredBy
     * @param sponsored
     * @param user
     */
    public Result(@NonNull String id, String createdAt, String updatedAt, Integer width, Integer height, String color, Object description, String altDescription, Urls urls, Links links, List<Object> categories, Boolean sponsored, Object sponsoredBy, Object sponsoredImpressionsId, Integer likes, Boolean likedByUser, List<Object> currentUserCollections, User user, List<Tag> tags, List<PhotoTag> photoTags) {
        super();
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.width = width;
        this.height = height;
        this.color = color;
        this.description = description;
        this.altDescription = altDescription;
        this.urls = urls;
        this.links = links;
        this.categories = categories;
        this.sponsored = sponsored;
        this.sponsoredBy = sponsoredBy;
        this.sponsoredImpressionsId = sponsoredImpressionsId;
        this.likes = likes;
        this.likedByUser = likedByUser;
        this.currentUserCollections = currentUserCollections;
        this.user = user;
        this.tags = tags;
        this.photoTags = photoTags;
    }

    public Result() {
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public String getAltDescription() {
        return altDescription;
    }

    public void setAltDescription(String altDescription) {
        this.altDescription = altDescription;
    }

    public Urls getUrls() {
        return urls;
    }

    public void setUrls(Urls urls) {
        this.urls = urls;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public List<Object> getCategories() {
        return categories;
    }

    public void setCategories(List<Object> categories) {
        this.categories = categories;
    }

    public Boolean getSponsored() {
        return sponsored;
    }

    public void setSponsored(Boolean sponsored) {
        this.sponsored = sponsored;
    }

    public Object getSponsoredBy() {
        return sponsoredBy;
    }

    public void setSponsoredBy(Object sponsoredBy) {
        this.sponsoredBy = sponsoredBy;
    }

    public Object getSponsoredImpressionsId() {
        return sponsoredImpressionsId;
    }

    public void setSponsoredImpressionsId(Object sponsoredImpressionsId) {
        this.sponsoredImpressionsId = sponsoredImpressionsId;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Boolean getLikedByUser() {
        return likedByUser;
    }

    public void setLikedByUser(Boolean likedByUser) {
        this.likedByUser = likedByUser;
    }

    public List<Object> getCurrentUserCollections() {
        return currentUserCollections;
    }

    public void setCurrentUserCollections(List<Object> currentUserCollections) {
        this.currentUserCollections = currentUserCollections;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<PhotoTag> getPhotoTags() {
        return photoTags;
    }

    public void setPhotoTags(List<PhotoTag> photoTags) {
        this.photoTags = photoTags;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(createdAt);
        dest.writeValue(updatedAt);
        dest.writeValue(width);
        dest.writeValue(height);
        dest.writeValue(color);
        dest.writeValue(description);
        dest.writeValue(altDescription);
        dest.writeValue(urls);
        dest.writeValue(links);
        dest.writeList(categories);
        dest.writeValue(sponsored);
        dest.writeValue(sponsoredBy);
        dest.writeValue(sponsoredImpressionsId);
        dest.writeValue(likes);
        dest.writeValue(likedByUser);
        dest.writeList(currentUserCollections);
        dest.writeValue(user);
        dest.writeList(tags);
        dest.writeList(photoTags);
    }

    public int describeContents() {
        return 0;
    }

}