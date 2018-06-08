package papaya.bayviewglen.com.papayatest;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import static papaya.bayviewglen.com.papayatest.MainActivity.BASE_URL;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.text.Text;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static papaya.bayviewglen.com.papayatest.MainActivity.BASE_URL;
import static papaya.bayviewglen.com.papayatest.MainActivity.TAGS_URL;

public class InstrumentLogging extends AppCompatActivity {
    String qrID;
    EditText instrumentType;
    Switch loanedOutSwitch; //TODO change switch track color to same orange
    EditText serialNumber;
    boolean isLoanedOut = false;
    EditText loaneeName;
    Button doneButton;

    AlertDialog.Builder builder;
    View tagView;
    AlertDialog dialog;
    ListView tagsList;
    Button submitTagsBtn;
    ArrayList<String> chosenTags = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument_logging);

        //UI Components and fields
        instrumentType = (EditText) findViewById(R.id.instrumentType);
        serialNumber = (EditText) findViewById(R.id.serialNumber);
        loanedOutSwitch = (Switch) findViewById(R.id.loanedOutSwitch);
        loaneeName = (EditText) findViewById(R.id.loaneeName);
        doneButton = (Button) findViewById(R.id.doneButton);

        fillFields();

        //Listener for switch that hides/shows loaneeName EditText when toggled
        loanedOutSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    loaneeName.setVisibility(View.VISIBLE);
                    isLoanedOut = true;
                }
                else {
                    loaneeName.setVisibility(View.GONE);
                    isLoanedOut = false;
                }
            }
        });

        setTagDialog();
    }

    //fill existing fields with content from db
    private void fillFields() {
        if (getIntent().getStringExtra("id") != ""){
            String fillIns = getIntent().getStringExtra("instrument");
            instrumentType.setText(getIntent().getStringExtra("instrument"), TextView.BufferType.EDITABLE);
            serialNumber.setText(getIntent().getStringExtra("serial"), TextView.BufferType.EDITABLE);
            loaneeName.setText(getIntent().getStringExtra("loanee"), TextView.BufferType.EDITABLE);
            if (getIntent().getStringExtra("loanee") != ""){
                loaneeName.setVisibility(View.VISIBLE);
                loanedOutSwitch.setChecked(true);
            }
        }
    }

    //initialzing tags, and check them if already in db
    private void setTagDialog() {
        String[] temp = getIntent().getStringArrayExtra("tags");
        builder = new AlertDialog.Builder(InstrumentLogging.this);
        tagView = getLayoutInflater().inflate(R.layout.dialog_list, null);

        //TODO when getting list of tags from DB, capitalize for UI
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Brass");
        tags.add("Woodwinds");
        tags.add("Strings");
        tags.add("Percussion");

        //Creates Array Adapter and sets ListView contents to tag ArrayList
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, tags);
        tagsList = (ListView) tagView.findViewById(R.id.tagList);
        tagsList.setAdapter(adapter);

        //Adds multiple selection function and ui to listView
        //Listener checks chosenTags for selected tag and adds/removes accordingly
        tagsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        tagsList.setItemsCanFocus(false);

        for (int i =0; i < temp.length;i++){
            for (int k =0; k < tags.size();k++){
                if (tags.get(k).equals(temp[i])){
                    tagsList.setItemChecked(k,true);
                    chosenTags.add(temp[i]);

                }
            }
        }

        //listener for selecting the tags
        tagsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) tagsList.getItemAtPosition(position);
                if(!chosenTags.contains(selected))
                    chosenTags.add(selected);
                else
                    chosenTags.remove(selected);
            }
        });

        //Button closes dialog
        submitTagsBtn = (Button) tagView.findViewById(R.id.submitTagsButton);
        submitTagsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        builder.setView(tagView);
        dialog = builder.create();
    }

    //Shows Tag List Dialog from clicking "Set Tags" button
    public void showTags(View v){
        dialog.show();
    }

    //Method for submit button
    public void submitData(View v){
        String instrumentTypeVal = instrumentType.getText().toString().toLowerCase();
        String serialNumberVal = serialNumber.getText().toString().toLowerCase();
        String loaneeNameVal = loaneeName.getText().toString().toLowerCase();

        //Contains id from qr code
        qrID = getIntent().getStringExtra("barcode");

        if(instrumentTypeVal.equals(""))
            instrumentType.setError("This field cannot be blank");

        if(serialNumberVal.equals(""))
            serialNumber.setError("This field cannot be blank.");


        if(isLoanedOut && loaneeNameVal.equals(""))
            loaneeName.setError("This field cannot be blank");

        if(!(instrumentTypeVal.equals("") || serialNumberVal.equals("") || (isLoanedOut && loaneeNameVal.equals("")))){
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            //set loan name to nothing if is in
            if (getLoanStatus().equals("in"))
                loaneeNameVal = "";

            //create new json
            Map<String, Object> tempMap = new HashMap();
            tempMap.put("qrID", qrID);
            tempMap.put("instrument",instrumentTypeVal);
            tempMap.put("status", getLoanStatus());
            tempMap.put("loanee", loaneeNameVal);
            tempMap.put("serial", serialNumberVal);
            tempMap.put("tags", getSelectedTags());
            JSONObject tempJSON = new JSONObject(tempMap);
            String url = BASE_URL;
            //get which rest method to use
            if(Integer.parseInt(getIntent().getStringExtra("restMethod")) == Request.Method.PUT){
                url += "/" + getIntent().getStringExtra("id");
            }
            //send/edit json file then send to dbsss
            RestfulMethods.JSONObjectRequest(requestQueue, url, tempJSON, Integer.parseInt(getIntent().getStringExtra("restMethod")));
            RestfulMethods.JSONObjectRequest(requestQueue, BASE_URL, null, Request.Method.GET);
            Toast confirm = Toast.makeText(getApplicationContext(), "Item Logged", Toast.LENGTH_SHORT);
            confirm.show();
            finish();
            Intent reset = new Intent(this, MainActivity.class);
            startActivity(reset);
        }
    }

    //return array of the selected tags
    public String[] getSelectedTags() {
        String[] temp = new String[chosenTags.size()];
        for (int i=0; i < temp.length;i++){
            temp[i]= chosenTags.get(i).toLowerCase();
        }
        return temp;
    }

    //get status of loan
    public String getLoanStatus() {
        if (loanedOutSwitch.isChecked()){
            return "out";
        } else
            return "in";
    }
}
