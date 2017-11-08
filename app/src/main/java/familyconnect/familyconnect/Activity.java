package familyconnect.familyconnect;


public class Activity {

    private String name, date, time, weatherDegrees, weatherSummary, weatherIcon, category, groupName;

    public Activity(String name, String date, String time, String weatherDegrees, String weatherSummary, String weatherIcon, String category, String groupName) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.weatherDegrees = weatherDegrees;
        this.weatherSummary = weatherSummary;
        this.weatherIcon = weatherIcon;
        this.category = category;
        this.groupName = groupName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) { this.date = date; }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWeatherDegrees() {
        return weatherDegrees;
    }

    public void setWeatherDegrees(String weatherDegrees) {
        this.weatherDegrees = weatherDegrees;
    }

    public String getWeatherSummary() {
        return weatherSummary;
    }

    public void setWeatherSummary(String weatherSummary) {
        this.weatherSummary = weatherSummary;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) { this.weatherIcon = weatherIcon; }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "";
    }
}
