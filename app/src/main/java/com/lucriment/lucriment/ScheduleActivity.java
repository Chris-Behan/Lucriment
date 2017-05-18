package com.lucriment.lucriment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

public class ScheduleActivity extends AppCompatActivity implements  View.OnClickListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    Button selectTimeButton;
    TextView timeResult;
    TextView toView;
    int day, month, year, hour, minute;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal, hourFinal2,minuteFinal2;
    boolean fromSet = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        selectTimeButton = (Button) findViewById(R.id.selectTime);
        timeResult = (TextView) findViewById(R.id.TimeView);
        toView = (TextView) findViewById(R.id.toView);


        selectTimeButton.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.selectTime:
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ScheduleActivity.this, ScheduleActivity.this,year,month,day);

                datePickerDialog.show();

            break;



        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yearFinal = year;
        monthFinal = month +1;
        dayFinal = dayOfMonth;

        Calendar cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog1 = new TimePickerDialog(ScheduleActivity.this, ScheduleActivity.this, hour,minute, true);
        timePickerDialog1.setMessage("To");
        timePickerDialog1.show();

        TimePickerDialog timePickerDialog2 = new TimePickerDialog(ScheduleActivity.this, ScheduleActivity.this, hour,minute, true);
        timePickerDialog2.setMessage("From");
        timePickerDialog2.show();




    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(!fromSet) {

            hourFinal = hourOfDay;
            minuteFinal = minute;
            timeResult.setText("Year: " + yearFinal + "\n" +
                    "Month: " + monthFinal + "\n" +
                    "Day: " + dayFinal + "\n" +
                    "Hour: " + hourFinal + "\n" +
                    "Minute: " + minuteFinal + "\n");

            fromSet = true;
        }else{
            hourFinal2 = hourOfDay;
            minuteFinal2 = minute;
            toView.setText("Year: " + yearFinal + "\n" +
                    "Month: " + monthFinal + "\n" +
                    "Day: " + dayFinal + "\n" +
                    "Hour: " + hourFinal2 + "\n" +
                    "Minute: " + minuteFinal2 + "\n");

        }


    }
}
