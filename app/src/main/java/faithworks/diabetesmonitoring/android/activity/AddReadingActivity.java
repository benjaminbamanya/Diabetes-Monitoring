package faithworks.diabetesmonitoring.android.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import org.glucosio.android.R;
import faithworks.diabetesmonitoring.android.presenter.AddReadingPresenter;
import faithworks.diabetesmonitoring.android.tools.AnimationTools;
import faithworks.diabetesmonitoring.android.tools.FormatDateTime;
import faithworks.diabetesmonitoring.android.tools.NumberFormatUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class AddReadingActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    protected final NumberFormat numberFormat = NumberFormatUtils.createDefaultNumberFormat();
    private final java.lang.String INTENT_EXTRA_EDIT = "editing";
    private final java.lang.String INTENT_EXTRA_EDIT_ID = "edit_id";
    private final String INTENT_EXTRA_PAGER = "pager";
    private final String INTENT_EXTRA_DROPDOWN = "history_dropdown";
    private AddReadingPresenter presenter;

    private TextView addTimeTextView;
    private TextView addDateTextView;
    private FloatingActionButton doneFAB;
    private Runnable fabAnimationRunnable;

    private int pagerPosition;
    private int dropdownPosition;
    private long editId = 0;
    private boolean editing = false;

    protected void retrieveExtra() {
        Bundle b = getIntent().getExtras();
        if (b != null) {
            pagerPosition = b.getInt(INTENT_EXTRA_PAGER);
            editId = b.getLong(INTENT_EXTRA_EDIT_ID);
            editing = b.getBoolean(INTENT_EXTRA_EDIT);
            dropdownPosition = b.getInt(INTENT_EXTRA_DROPDOWN);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView addTime = findViewById(R.id.dialog_add_time);
        DecimalFormat df = new DecimalFormat("00");

        presenter.setReadingHour(df.format(hourOfDay));
        presenter.setReadingMinute(df.format(minute));

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        FormatDateTime formatDateTime = new FormatDateTime(getApplicationContext());
        addTime.setText(formatDateTime.getTime(cal));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        TextView addDate = findViewById(R.id.dialog_add_date);
        DecimalFormat df = new DecimalFormat("00");

        presenter.setReadingYear(year + "");
        presenter.setReadingMonth(df.format(monthOfYear + 1));
        presenter.setReadingDay(df.format(dayOfMonth));

        String date = +dayOfMonth + "/" + presenter.getReadingMonth() + "/" + presenter.getReadingYear();
        addDate.setText(date);
    }

    public void createDateTimeViewAndListener() {
        addTimeTextView = findViewById(R.id.dialog_add_time);
        addDateTextView = findViewById(R.id.dialog_add_date);

        addDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(
                        AddReadingActivity.this,
                        AddReadingActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
                dpd.show();
            }
        });

        addTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(getApplicationContext());
                AddReadingActivity addReadingActivity = AddReadingActivity.this;
                AddReadingPresenter addReadingPresenter = addReadingActivity.getPresenter();
                Calendar cal = addReadingPresenter.getReadingCal();
                if (addReadingActivity.isEditing()) {
                    cal.setTime(addReadingPresenter.getReadingTime());
                }
                TimePickerDialog tpd = new TimePickerDialog(
                        AddReadingActivity.this,
                        AddReadingActivity.this,
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        is24HourFormat);
                tpd.show();
            }
        });
    }

    public void createFANViewAndListener() {

        doneFAB = findViewById(R.id.done_fab);
        doneFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOnAddButtonPressed();
            }
        });
        fabAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                AnimationTools.startCircularReveal(doneFAB);
            }
        };
        doneFAB.postDelayed(fabAnimationRunnable, 600);
    }

    protected abstract void dialogOnAddButtonPressed();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void finishActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        // Pass pager position to open it again later
        Bundle b = new Bundle();
        b.putInt(INTENT_EXTRA_PAGER, this.getPagerPosition());
        b.putInt(INTENT_EXTRA_DROPDOWN, this.getDropdownPosition());
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doneFAB.removeCallbacks(fabAnimationRunnable);
    }

    public AddReadingPresenter getPresenter() {
        return this.presenter;
    }

    // Getter and Setter
    public void setPresenter(AddReadingPresenter newPresenter) {
        this.presenter = newPresenter;
    }

    public int getPagerPosition() {
        return pagerPosition;
    }

    public int getDropdownPosition() {
        return dropdownPosition;
    }

    public long getEditId() {
        return editId;
    }

    public boolean isEditing() {
        return editing;
    }

    public TextView getAddTimeTextView() {
        return addTimeTextView;
    }

    public void setAddTimeTextView(TextView addTimeTextView) {
        this.addTimeTextView = addTimeTextView;
    }

    public TextView getAddDateTextView() {
        return addDateTextView;
    }

    public void setAddDateTextView(TextView addDateTextView) {
        this.addDateTextView = addDateTextView;
    }

}
