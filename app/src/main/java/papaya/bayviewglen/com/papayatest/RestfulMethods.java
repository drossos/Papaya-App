package papaya.bayviewglen.com.papayatest;

import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static papaya.bayviewglen.com.papayatest.MainActivity.TAGS_URL;

//todo add an error to pop up if no netowrk
public class RestfulMethods {
    static JSONObject dbConetents;
    static JSONObject tags;
    static String tempURL;
    //Used for all requests to the server
    //call request GET with JSON object to fetch single post + addExtension to the url of the id
    //call request PUT with JSON with object to edit + addExtension to the url of the id
    //call request GET with no object to fetch all
    //call request POST with JSON object to add new object

    public static void JSONObjectRequest(RequestQueue requestQueue, String baseUrl, JSONObject jsonObject, int method) {
        tempURL = baseUrl;
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                method,
                baseUrl,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("RestResponse", response.toString());
                        if (tempURL.equals(TAGS_URL)){
                            tags = response;
                        } else {
                            dbConetents = response;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("RestResponse", error.toString());
                    }
                }

        );
        requestQueue.add(objectRequest);

    }

    //checks if id exists within DB already
    public static JSONObject checkIfInDB(String qrID) throws JSONException {
        JSONObject contents = dbConetents;
        JSONArray contentArr = contents.getJSONArray("instruments");
        int index = -1;
        //TODO If time impliment binary serach to improve data
        for (int i=0; i<contentArr.length();i++){
            JSONObject temp = contentArr.getJSONObject(i);
            if (qrID.equals(temp.getString("qrID")))
                index = i;
        }

        if (index != -1)
            return contentArr.getJSONObject(index);
        else
            return null;
    }
}

