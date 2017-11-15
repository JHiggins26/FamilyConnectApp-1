
package familyconnect.familyconnect.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FamilyConnectActivitiesHttpResponse {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("activitie_name")
    @Expose
    private String activitieName;
    @SerializedName("user_id")
    @Expose
    private long userId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("condition")
    @Expose
    private String condition;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("tempHi")
    @Expose
    private String tempHi;
    @SerializedName("tempLow")
    @Expose
    private String tempLow;
    @SerializedName("url")
    @Expose
    private String url;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getActivitieName() {
        return activitieName;
    }

    public void setActivitieName(String activitieName) {
        this.activitieName = activitieName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTempHi() {
        return tempHi;
    }

    public void setTempHi(String tempHi) {
        this.tempHi = tempHi;
    }

    public String getTempLow() {
        return tempLow;
    }

    public void setTempLow(String tempLow) {
        this.tempLow = tempLow;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
