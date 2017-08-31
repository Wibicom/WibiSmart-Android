package wibicom.wibeacon3.Historical;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

import wibicom.wibeacon3.R;

/**
 * Created by Michael Vaquier on 2017-08-18.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), R.style.DatePickerDialogTheme, this, year, month, day);

        return datePicker;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        month++;
        String monthString = Integer.toString(month);
        String dayString = Integer.toString(day);
        if(dayString.length() == 1)
            dayString = "0" + dayString;
        if(monthString.length() == 1)
            monthString = "0" + monthString;
        HistoricalDashboardActivity.getInstance().getFragmentHistoricalDashboard().dateSelected(year + "/" + monthString + "/" + dayString);
    }


}