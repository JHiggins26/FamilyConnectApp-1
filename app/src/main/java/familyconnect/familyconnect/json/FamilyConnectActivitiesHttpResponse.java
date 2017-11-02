
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

}
