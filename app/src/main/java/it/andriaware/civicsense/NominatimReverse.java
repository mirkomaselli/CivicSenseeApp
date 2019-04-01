package it.andriaware.civicsense;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NominatimReverse {

    private final String NominatimInstance = "https://nominatim.openstreetmap.org";
    private int zoomLevel = 18;

  /*  public static void main(String[] args){

    } */

    public NominatimReverse(){}

    public Indirizzo getAdress(double lat, double lon){
        Indirizzo result = null;
        String urlString = NominatimInstance + "/reverse?format=json&addressdetails=1&lat=" + String.valueOf(lat) + "&lon=" + String.valueOf(lon) + "&zoom=" + zoomLevel ;
        try {
            result =new Indirizzo(getJSON(urlString), zoomLevel);
        } catch (IOException e) {
            System.err.println("Can't connect to server.");
            e.printStackTrace();
        }
        return result;
    }

    private String getJSON(String urlString) throws IOException{
        URL url = new URL(urlString);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.addRequestProperty("User-Agent", "Mozilla/4.76");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String text;
        StringBuilder result = new StringBuilder();
        while ((text = in.readLine()) != null)
            result.append(text);

        in.close();
        return result.toString();
    }

}
