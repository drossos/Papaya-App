package papaya.bayviewglen.com.papayatest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class InstrumentLogging extends AppCompatActivity {
    String id;
    EditText instrumentType;
    Switch loanedOutSwitch;
    boolean isLoanedOut = false;
    EditText loaneeName;
    Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument_logging);

        instrumentType = (EditText) findViewById(R.id.instrumentType);

        loanedOutSwitch = (Switch) findViewById(R.id.loanedOutSwitch);
        loaneeName = (EditText) findViewById(R.id.loaneeName);
        doneButton = (Button) findViewById(R.id.doneButton);

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
    }

    //Method for submit button
    //TODO add if all values are not blank
    public void submitData(View v){
        //TODO Log stuff in DB
        String instrumentTypeVal = instrumentType.getText().toString();
        String loaneeNameVal = loaneeName.getText().toString();

        //Contains id from qr code
        id = getIntent().getStringExtra("BARCODE_VALUE");

        if(instrumentTypeVal.equals(""))
            instrumentType.setError("This field cannot be blank");

        else if(isLoanedOut && loaneeNameVal.equals(""))
            loaneeName.setError("This field cannot be blank");

        else{
            //Send stuff to table

            Toast confirm = Toast.makeText(getApplicationContext(), "Item Logged", Toast.LENGTH_SHORT);
            confirm.show();

            Intent reset = new Intent(this, MainActivity.class);
            startActivity(reset);
        }
    }
}
