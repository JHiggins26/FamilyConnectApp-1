package familyconnect.familyconnect;


public class Activity {

    private String name, time, weather, category, groupName;

    public Activity(String name) {
        this.name = name;
        /*this.time = time;
        this.weather = weather;
        this.category = category;
        this.groupName = groupName;*/
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

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
        return name + "\n";
                /*", time='" + time + '\'' +
                ", weather='" + weather + '\'' +
                ", category='" + category + '\'' +
                ", groupName='" + groupName + '\'' +
                '}';*/
    }
}
