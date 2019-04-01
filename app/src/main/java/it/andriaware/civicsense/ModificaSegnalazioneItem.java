package it.andriaware.civicsense;

public class ModificaSegnalazioneItem {
    private String description="";
    private int priority=0;
    private String photo="";
    private int category_id=0;

    public ModificaSegnalazioneItem(String description, int priority, String photo, int category_id) {
        this.description = description;
        this.priority = priority;
        this.photo = photo;
        this.category_id = category_id;
    }
}
