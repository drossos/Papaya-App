package papaya.bayviewglen.com.papayatest;

import android.Manifest;
import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.android.volley.Request;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by arunj on 5/25/2018.
 */

public class ScanBarcodeActivity extends Activity {
    SurfaceView cameraPreview;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan_barcode);

        cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
        createCameraSource();
    }

    private void createCameraSource() {
        final BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();
        final CameraSource cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true).setRequestedPreviewSize(1600, 1024).build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(ScanBarcodeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //TODO after camera open for a while, it registers false positive
            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                //TODO add search function and pass in existing values if id exists

                if(barcodes.size() > 0){
                    String tempID ="";
                    String tempInstrument="";
                    String tempStatus="";
                    String tempLoanee="";
                    String tempSerial="";
                    String restMethod = Request.Method.POST +"";
                    String[] tempTags = {};

                    Intent intent = new Intent();
                    try {
                        //TODO MAKE SURE THE DATABASE IS CONFIGED FOR THE QRID DATA FIELD
                        JSONObject fetchedJSON = RestfulMethods.checkIfInDB(barcodes.valueAt(0).displayValue);
                        if(fetchedJSON != null){
                            tempID = fetchedJSON.getString("_id");
                            tempInstrument = fetchedJSON.getString("instrument");
                            tempStatus = fetchedJSON.getString("status");
                            tempLoanee = fetchedJSON.getString("loanee");
                            tempSerial = fetchedJSON.getString("serial");
                            restMethod = Request.Method.PUT+"";
                            //converting JSONArray to String[]
                            JSONArray temp = (JSONArray) fetchedJSON.get("tags");
                            tempTags = new String[temp.length()];
                            for (int i=0; i < temp.length();i++){
                                tempTags[i] = temp.getString(i);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    intent.putExtra("barcode", barcodes.valueAt(0)); //get Latest Barcode from the array
                    intent.putExtra("id",tempID);
                    intent.putExtra("instrument",tempInstrument);
                    intent.putExtra("status",tempStatus);
                    intent.putExtra("loanee",tempLoanee);
                    intent.putExtra("serial",tempSerial);
                    intent.putExtra("restMethod",restMethod);
                    intent.putExtra("tags",tempTags);

                    setResult(CommonStatusCodes.SUCCESS, intent);
                    finish();
                }
            }
        });
    }
}
