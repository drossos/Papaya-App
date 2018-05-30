package papaya.bayviewglen.com.papayatest;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class RestfulMethods {

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

