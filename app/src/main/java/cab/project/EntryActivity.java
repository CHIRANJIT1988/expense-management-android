package cab.project;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.text.DecimalFormat;
import java.util.List;

import cab.project.model.Transaction;
import cab.project.network.InternetConnectionDetector;
import cab.project.sqlite.SQLiteDatabaseHelper;
import cab.project.syncapi.SyncTransaction;


public class EntryActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener
{

    private AppCompatSpinner spinnerTransactionType, spinnerTransactionPurpose;
    private EditText editAmount, editMessage, editDate;
    private ImageButton ib_pick_date;

    private static final String[] TRANSACTION_TYPE = { "Transaction Type", "Debited (-)", "Credited (+)" };
    private static List<String> TRANSACTION_PURPOSE;

    private static String transaction_purpose;

    private Transaction transactionObject;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        setTitle("Transaction");


        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinnerTransactionType = (AppCompatSpinner) findViewById(R.id.spinnerTransactionType);
        spinnerTransactionPurpose = (AppCompatSpinner) findViewById(R.id.spinnerTransactionPurpose);
        editAmount = (EditText) findViewById(R.id.editAmount);
        editMessage = (EditText) findViewById(R.id.editMessage);
        editDate = (EditText) findViewById(R.id.editDate);
        ib_pick_date = (ImageButton) findViewById(R.id.ib_pick_date);


        spinnerTransactionType.setOnItemSelectedListener(this);
        spinnerTransactionPurpose.setOnItemSelectedListener(this);
        ib_pick_date.setOnClickListener(this);


        TRANSACTION_PURPOSE = new SQLiteDatabaseHelper(this).getAllTransactionPurpose();
        TRANSACTION_PURPOSE.add(0, "Transaction Purpose");


        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter a = new ArrayAdapter(this,android.R.layout.simple_spinner_item, TRANSACTION_TYPE);
        ArrayAdapter b = new ArrayAdapter(this,android.R.layout.simple_spinner_item, TRANSACTION_PURPOSE);

        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        b.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Setting the ArrayAdapter data on the Spinner
        spinnerTransactionType.setAdapter(a);
        spinnerTransactionPurpose.setAdapter(b);


        if(getIntent().getBooleanExtra("EDIT", false))
        {
            transactionObject = Transaction.list.get(getIntent().getIntExtra("POSITION", 0));
            setData();
        }
    }


    private void setData()
    {

        DecimalFormat df = new DecimalFormat("0");

        if(transactionObject.getCreditAmount() != 0)
        {
            editAmount.setText(df.format(transactionObject.getCreditAmount()));
        }

        if(transactionObject.getDebitAmount() != 0)
        {
            editAmount.setText(df.format(transactionObject.getDebitAmount()));
        }

        editMessage.setText(transactionObject.getMessage());
        editDate.setText(transactionObject.getTimestamp());
    }


    private boolean validate()
    {

        if(spinnerTransactionType.getSelectedItemPosition() == 0)
        {

            makeSnackbar("Select Transaction Type");
            return false;
        }

        if(spinnerTransactionPurpose.getSelectedItemPosition() == 0)
        {

            makeSnackbar("Select Transaction Purpose");
            return false;
        }

        if(editAmount.getText().toString().trim().length() == 0)
        {

            makeSnackbar("Enter Amount");
            return false;
        }

        if(editDate.getText().toString().trim().length() == 0)
        {

            makeSnackbar("Enter Date");
            return false;
        }

        return true;
    }


    private void makeSnackbar(String msg)
    {

        LinearLayout linear_main = (LinearLayout) findViewById(R.id.linearMain);

        Snackbar snackbar = Snackbar.make(linear_main, msg, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.myPrimaryColor));
        snackbar.show();
    }


    private Transaction initTransactionObj()
    {

        if(!getIntent().getBooleanExtra("EDIT", false))
        {
            transactionObject = new Transaction();
            transactionObject.setTransactionId(String.valueOf(System.currentTimeMillis()));
        }

        transactionObject.setTransactionPurpose(transaction_purpose);

        if(spinnerTransactionType.getSelectedItemPosition() == 1)
        {
            transactionObject.setDebitAmount(Double.parseDouble(editAmount.getText().toString()));
            transactionObject.setCreditAmount(0);
        }

        else if(spinnerTransactionType.getSelectedItemPosition() == 2)
        {
            transactionObject.setCreditAmount(Double.parseDouble(editAmount.getText().toString()));
            transactionObject.setDebitAmount(0);
        }

        transactionObject.setMessage(editMessage.getText().toString());
        transactionObject.setTimestamp(editDate.getText().toString());

        return transactionObject;
    }


    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {

            case R.id.btnSave:

                    if(validate())
                    {

                        LinearLayout linear_main = (LinearLayout) findViewById(R.id.linearMain);

                        SQLiteDatabaseHelper helper = new SQLiteDatabaseHelper(this);

                        Transaction transactionObject = initTransactionObj();

                        if(!getIntent().getBooleanExtra("EDIT", false))
                        {

                            if(helper.insertTransaction(transactionObject))
                            {

                                Snackbar snackbar = Snackbar.make(linear_main, "Record added Successfully", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.success));
                                snackbar.show();

                                Transaction.list.add(0, transactionObject);

                                if(new InternetConnectionDetector(this).isConnected())
                                {
                                    new SyncTransaction(this).execute();
                                }

                                clear();
                            }
                        }

                        else if(getIntent().getBooleanExtra("EDIT", false))
                        {

                            Transaction.list.set(getIntent().getIntExtra("POSITION", 0), transactionObject);

                            helper.updateTransaction(transactionObject);

                            Snackbar snackbar = Snackbar.make(linear_main, "Record updated Successfully", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.modify));
                            snackbar.show();

                            if(new InternetConnectionDetector(this).isConnected())
                            {
                                new SyncTransaction(this).execute();
                            }
                        }

                        else
                        {
                            Snackbar snackbar = Snackbar.make(linear_main, "Failed to add record", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.error));
                            snackbar.show();
                        }
                    }

                    break;

            case R.id.ib_pick_date:

                Intent intent = new Intent(EntryActivity.this, DatePickerActivity.class);
                startActivityForResult(intent, 100);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try
        {

            if(requestCode == 100 && resultCode == 100)
            {
                editDate.setText(data.getStringExtra("DATE"));
            }
        }

        catch (Exception e)
        {

        }
    }


    private void clear()
    {

        editAmount.getText().clear();
        editMessage.getText().clear();
        editDate.getText().clear();

        spinnerTransactionPurpose.setSelection(0);
        transaction_purpose = "";

        spinnerTransactionType.setSelection(0);
    }


    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView parent, View arg1, int position, long id)
    {

        AppCompatSpinner spinner = (AppCompatSpinner) parent;

        switch (spinner.getId())
        {

            case R.id.spinnerTransactionPurpose:

                if(position > 0)
                {

                    transaction_purpose = TRANSACTION_PURPOSE.get(position);
                }

                else
                {
                    transaction_purpose = "";
                }

                break;

            case R.id.spinnerTransactionType:

                break;
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
        // TODO Auto-generated method stub
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case android.R.id.home:
            {
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}