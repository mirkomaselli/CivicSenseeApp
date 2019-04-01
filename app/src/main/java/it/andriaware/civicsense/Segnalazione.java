package it.andriaware.civicsense;



public class Segnalazione {
    private String description;
    private String address;
    private double latitude;
    private double longitude;
    private int priority;
    private String photo;
    private String municipalityName;
    private String state;
    private int category_id;

    public Segnalazione(String description, String address, double latitude, double longitude, int priority, String photo, String municipalityName, String state, int category_id) {
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.priority = priority;
        this.photo = photo;
        this.municipalityName = municipalityName;
        this.state=state;
        this.category_id = category_id;
    }


}
