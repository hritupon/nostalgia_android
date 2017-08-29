package com.hritupon.nostalgia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import me.nlmartian.silkcal.DatePickerController;
import me.nlmartian.silkcal.DayPickerView;
import me.nlmartian.silkcal.SimpleMonthAdapter;

public class CalendarActivity extends AppCompatActivity implements DatePickerController {

    private DayPickerView calendarView;
    private TextView selectedDateTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        selectedDateTextView = (TextView)findViewById(R.id.selected_calendar_date_id);
        calendarView = (DayPickerView) findViewById(R.id.calendar_view);
        calendarView.setController(this);

    }

    @Override
    public int getMaxYear() {
        return 0;
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {
        String dateString = day+"/"+month+"/"+year;
        selectedDateTextView.setText(dateString);
    }

    @Override
    public void onDateRangeSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays) {

    }
}