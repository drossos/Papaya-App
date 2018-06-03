package papaya.bayviewglen.com.papayatest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by arunj on 5/23/2018.
 */

public class MainActivity extends Activity {
    private static final int CAMERA_REQUEST = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        Map<String, Object> test = new HashMap();
        String baseUrl = "https://backend-papaya.herokuapp.com/instruments";
        test.put("instrument", "cream");
        test.put("status", "yes");
        test.put("loanee", "jeff");
        test.put("serial", "357");
        String [] tags = {"jewish folk", "German Metal"};
        test.put("tags", tags);
        JSONObject testJSon = new JSONObject(test);

        RestfulMethods.JSONObjectRequest(requestQueue, baseUrl, testJSon, Request.Method.POST);

        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST);
        }

    }

    //added click event to barcode button
    public void scanBarcode(View v) {
        Intent intent = new Intent(this, ScanBarcodeActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 0){
            if(resultCode == CommonStatusCodes.SUCCESS){
                if(data != null){
                    Barcode barcode = data.getParcelableExtra("barcode");
                    String barcodeVal = barcode.displayValue;

                    //Switches to instrument logging page once barcode is scanned
                    Intent logInstrument = new Intent(this, InstrumentLogging.class);
                    logInstrument.putExtra("BARCODE_VALUE", barcodeVal);
                    startActivity(logInstrument);
                }else{
                    //Pops up with error if barcode value not found
                    Toast error = Toast.makeText(getApplicationContext(), "No Barcode Found", Toast.LENGTH_SHORT);
                    error.show();
                }
            }
        }else{

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
