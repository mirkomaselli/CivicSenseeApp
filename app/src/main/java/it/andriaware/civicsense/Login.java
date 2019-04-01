package it.andriaware.civicsense;

import android.support.annotation.Nullable;

public class Login {
    private int id=0;
    private String name="";
    private String email="";
    private String email_verified_at="";
    private String api_token="";
    private int role =0;
    private String created_at = "";
    private String updated_at="";

    private String password="";

    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Login(int id, String name, String email, String email_verified_at, String api_token, int role, String created_at, String updated_at) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.email_verified_at = email_verified_at;
        this.api_token = api_token;
        this.role = role;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getApi_token() {
        return api_token;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
