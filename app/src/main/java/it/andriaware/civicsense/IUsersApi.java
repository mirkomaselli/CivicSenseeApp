package it.andriaware.civicsense;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IUsersApi {
    @POST("signup")
    Call<User> createUser(@Body User user);

    @POST("login")
    Call<Login> loginUser(@Body Login login);

    @POST("tickets")
    Call<Segnalazione> creaSegnalazione(@Body Segnalazione segnalazione);

    @GET("tickets")
    Call<ArrayList<SegnalazioneItem>> getTickets();

    @GET("tickets/{id}")
    Call<Tickets> getTicket(@Path("id") int id);

    @POST("tickets/{id}")
    Call<ModificaSegnalazioneItem> updateTicket(@Path("id") int id, @Body ModificaSegnalazioneItem modificaSegnalazioneItem);

    @DELETE("tickets/{id}")
    Call<Void> deletePost(@Path("id") int id);

    @POST("forgot/password")
    Call<Email> forgotPassword(@Body Email email);

    @POST("user/update")
    Call<User> editUser(@Body User user);
}
