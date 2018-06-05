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

import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;
import java.util.List;

public class InstrumentLogging extends AppCompatActivity {
    String id;
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

    private void setTagDialog() {
        builder = new AlertDialog.Builder(InstrumentLogging.this);
        tagView = getLayoutInflater().inflate(R.layout.dialog_list, null);

        //TODO get list of tags from DB and add to tags ArrayList
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");
        tags.add("test");

        //Creates Array Adapter and sets ListView contents to tag ArrayList
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, tags);
        tagsList = (ListView) tagView.findViewById(R.id.tagList);
        tagsList.setAdapter(adapter);

        //Adds multiple selection function and ui to listView
        //Listener checks chosenTags for selected tag and adds/removes accordingly
        tagsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        tagsList.setItemsCanFocus(false);
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
        String instrumentTypeVal = instrumentType.getText().toString().toUpperCase();
        String serialNumberVal = serialNumber.getText().toString().toUpperCase();
        String loaneeNameVal = loaneeName.getText().toString().toUpperCase();
        //TODO dont forget that there is an arrayList of selected tags universally available

        //Contains id from qr code
        id = getIntent().getStringExtra("BARCODE_VALUE");

        if(instrumentTypeVal.equals(""))
            instrumentType.setError("This field cannot be blank");

        if(serialNumberVal.equals(""))
            serialNumber.setError("This field cannot be blank.");


        if(isLoanedOut && loaneeNameVal.equals(""))
            loaneeName.setError("This field cannot be blank");

        if(!(instrumentTypeVal.equals("") || serialNumberVal.equals("") || (isLoanedOut && loaneeNameVal.equals("")))){
            //TODO Log stuff in DB here
            Toast confirm = Toast.makeText(getApplicationContext(), "Item Logged", Toast.LENGTH_SHORT);
            confirm.show();

            Intent reset = new Intent(this, MainActivity.class);
            startActivity(reset);
        }
    }
}
