package it.andriaware.civicsense;

public class Tickets {
    private int id=0;
    private int user_id=0;
    private String description="";
    private String address="";
    private int municipality_id=0;
    private double latitude=0;
    private double longitude=0;
    private String photo="";
    private int priority=0;
    private int status=0;
    private int category_id=0;
    private int body_id=0;
    private String created_at="";
    private String updated_at="";
    private String municipality_name="";
    private String body_name="";

    public Tickets(int id, int user_id, String description, String address, int municipality_id, double latitude, double longitude, String photo, int priority, int status, int category_id, int body_id, String created_at, String updated_at, String municipality_name, String body_name) {
        this.id = id;
        this.user_id = user_id;
        this.description = description;
        this.address = address;
        this.municipality_id = municipality_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo = photo;
        this.priority = priority;
        this.status = status;
        this.category_id = category_id;
        this.body_id = body_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.municipality_name = municipality_name;
        this.body_name = body_name;
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public int getMunicipality_id() {
        return municipality_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPhoto() {
        return photo;
    }

    public int getPriority() {
        return priority;
    }

    public int getStatus() {
        return status;
    }

    public int getCategory_id() {
        return category_id;
    }

    public int getBody_id() {
        return body_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getMunicipality_name() {
        return municipality_name;
    }

    public String getBody_name() {
        return body_name;
    }
}
