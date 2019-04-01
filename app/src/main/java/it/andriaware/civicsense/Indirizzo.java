package it.andriaware.civicsense;

import org.json.JSONException;
import org.json.JSONObject;


public class Indirizzo {

        private String city = "";
        private String display_name = "";
        private String county = "";


        public Indirizzo(String json, int lod){
            try {
                JSONObject jObject = new JSONObject(json);

                if (jObject.has("error")) {
                    System.err.println(jObject.get("error"));
                    return;
                }
                display_name = jObject.getString("display_name");
                JSONObject addressObject = jObject.getJSONObject("address");

                if(addressObject.has("city")){
                    city = addressObject.getString("city");
                }

                else if(addressObject.has("town")){
                    city= addressObject.getString("town");
                }

                else if(addressObject.has("village")){
                    city= addressObject.getString("village");
                }

               if(addressObject.has("county")){
                    county= addressObject.getString("county");
               }

            } catch (JSONException e) {
                System.err.println("Can't parse JSON string");
                e.printStackTrace();
            }
        }

        public String getDisplayName(){
            return display_name;
        }

        public String getCity(){
            return city;
        }

        public String getCounty() {
            return county;
        }
}
