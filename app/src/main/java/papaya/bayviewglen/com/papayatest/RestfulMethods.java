package papaya.bayviewglen.com.papayatest;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class RestfulMethods {

    //Used for all requests to the server
    //call request GET with JSON object to fetch single post + addExtension to the url of the id
    //call request PUT with JSON with object to edit + addExtension to the url of the id
    //call request GET with no object to fetch all
    //call request POST with JSON object to add new object

    public static void JSONObjectRequest (RequestQueue requestQueue, String baseUrl, JSONObject jsonObj, int method){
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                method,
                baseUrl,
                jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("RestResponse", response.toString());
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


}

