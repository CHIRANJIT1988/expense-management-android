package cab.project;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;


public class DatePickerActivity extends AppCompatActivity
{

    private DatePicker datePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        datePicker = (DatePicker) findViewById(R.id.datePicker);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {

                int mYear = datePicker.getYear();

                int mMonth = datePicker.getMonth() + 1;
                int mDay = datePicker.getDayOfMonth();

                Intent intent = new Intent();
                intent.putExtra("DATE", mYear + "-" + date_format(mMonth) + "-" + date_format(mDay));
                setResult(100, intent);

                finish();
            }
        });
    }


    private String date_format(int value)
    {

        if(value > 9)
        {
            return String.valueOf(value);
        }

        return String.valueOf("0" + value);
    }
}
