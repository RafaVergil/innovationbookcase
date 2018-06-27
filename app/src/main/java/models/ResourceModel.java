package models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import utils.UTILS;

public class ResourceModel implements Serializable {

    private static final String TAG = "ResourceModel";

    private String id = "";
    private String title = "";
    private String subtitle = "";
    private List<String> authors = null;
    private String publisher = "";
    private String publishDate = "";
    private String description = "";
    private String printType = "";
    private List<String> categories = null;
    private String thumbnail = "";
    private String previewLink = "";
    private String infoLink = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(JSONArray authors) {
        List<String> _authors = new ArrayList<>();

        if(authors != JSONObject.NULL && authors != null) {
            int size = authors.length();

            for (int i = 0; i < size; i++) {
                try {
                    _authors.add(authors.getString(i));
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
            }
        }

        this.authors = _authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrintType() {
        return printType;
    }

    public void setPrintType(String printType) {
        this.printType = printType;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(JSONArray categories) {
        List<String> _categories = new ArrayList<>();

        if(categories != JSONObject.NULL && categories != null) {
            int size = categories.length();
            for (int i = 0; i < size; i++) {
                try {
                    _categories.add(categories.getString(i));
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
            }
        }

        this.categories = _categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPreviewLink() {
        return previewLink;
    }

    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }

    public String getInfoLink() {
        return infoLink;
    }

    public void setInfoLink(String infoLink) {
        this.infoLink = infoLink;
    }

    public static final ResourceModel parseResource(JSONObject item){
        ResourceModel model = new ResourceModel();
        boolean success = false;

        try{

            model.setId( (item.has("id") && item.get("id") != JSONObject.NULL) ? item.getString("id") : "" );

            if(item.has("volumeInfo") && item.get("volumeInfo") != JSONObject.NULL){
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                model.setTitle( (volumeInfo.has("title") && volumeInfo.get("title") != JSONObject.NULL) ? volumeInfo.getString("title") : "" );
                model.setSubtitle( (volumeInfo.has("subtitle") && volumeInfo.get("subtitle") != JSONObject.NULL) ? volumeInfo.getString("subtitle") : "" );
                model.setPublisher( (volumeInfo.has("publisher") && volumeInfo.get("publisher") != JSONObject.NULL) ? volumeInfo.getString("publisher") : "" );
                model.setPublishDate( (volumeInfo.has("publishedDate") && volumeInfo.get("publishedDate") != JSONObject.NULL) ? volumeInfo.getString("publishedDate") : "" );
                model.setDescription( (volumeInfo.has("description") && volumeInfo.get("description") != JSONObject.NULL) ? volumeInfo.getString("description") : "" );
                model.setPrintType( (volumeInfo.has("printType") && volumeInfo.get("printType") != JSONObject.NULL) ? volumeInfo.getString("printType") : "" );
                model.setPreviewLink( (volumeInfo.has("previewLink") && volumeInfo.get("previewLink") != JSONObject.NULL) ? volumeInfo.getString("previewLink") : "" );
                model.setInfoLink( (volumeInfo.has("infoLink") && volumeInfo.get("infoLink") != JSONObject.NULL) ? volumeInfo.getString("infoLink") : "" );
                model.setAuthors( (volumeInfo.has("authors") && volumeInfo.get("authors") != JSONObject.NULL) ? volumeInfo.getJSONArray("authors") : null );
                model.setCategories( (volumeInfo.has("categories") && volumeInfo.get("categories") != JSONObject.NULL) ? volumeInfo.getJSONArray("categories") : null );

                if(volumeInfo.has("imageLinks") && volumeInfo.get("imageLinks") != JSONObject.NULL) {
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    model.setThumbnail( (imageLinks.has("thumbnail") && imageLinks.get("thumbnail") != JSONObject.NULL) ? imageLinks.getString("thumbnail") : "" );
                }
            }

            success = true;

        }
        catch (JSONException e){
            UTILS.DebugLog(TAG, e);
        }

        return success ? model : null;
    }

    public static final List<ResourceModel> parseResourceList(JSONArray items){
        List<ResourceModel> list = new ArrayList<>();
        int failCounter = 0;

        int size = items.length();

        for(int i = 0; i < size; i++){
            try {
                JSONObject item = items.getJSONObject(i);
                ResourceModel model = ResourceModel.parseResource(item);
                if(model != null) {
                    list.add(model);
                }
                else {
                    failCounter++;
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
            }
        }

        if(failCounter > 0){
            UTILS.DebugLog(TAG, failCounter + " " + ((failCounter == 1) ? "book" : "books") + " could not be parsed.");
        }

        return list;
    }
}
