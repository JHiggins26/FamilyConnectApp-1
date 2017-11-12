package familyconnect.familyconnect;


public class Activity {

    private String name, weatherIcon, weatherSummary, tempLow, tempHigh, group, category;
    private long id;

    public Activity(long id, String name, String weatherIcon, String weatherSummary, String tempLow, String tempHigh, String category, String group) {
        this.id = id;
        this.name = name;
        this.weatherIcon = weatherIcon;
        this.weatherSummary = weatherSummary;
        this.tempLow = tempLow;
        this.tempHigh = tempHigh;
        this.category = category;
        this.group = group;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) { this.weatherIcon = weatherIcon; }

    public String getWeatherSummary() {
        return weatherSummary;
    }

    public void setWeatherSummary(String weatherSummary) { this.weatherSummary = weatherSummary; }

    public String getTempLow() {
        return tempLow;
    }

    public void setTempLow(String tempLow) { this.tempLow = tempLow; }

    public String getTempHigh() {
        return tempHigh;
    }

    public void setTempHigh(String tempHigh) { this.tempHigh = tempHigh; }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) { this.group = group; }

    @Override
    public String toString() {
        return "";
    }
}
