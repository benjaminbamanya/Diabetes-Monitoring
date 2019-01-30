package faithworks.diabetesmonitoring.android.presenter;

import android.widget.ListAdapter;

import java.util.Calendar;
import java.util.Date;

import faithworks.diabetesmonitoring.android.R;
import faithworks.diabetesmonitoring.android.activity.RemindersActivity;
import faithworks.diabetesmonitoring.android.adapter.RemindersAdapter;
import faithworks.diabetesmonitoring.android.db.DatabaseHandler;
import faithworks.diabetesmonitoring.android.db.Reminder;
import faithworks.diabetesmonitoring.android.tools.GlucosioAlarmManager;

public class RemindersPresenter {

    private RemindersActivity activity;
    private DatabaseHandler db;

    public RemindersPresenter(RemindersActivity activity) {
        this.activity = activity;
        db = new DatabaseHandler(activity);
    }

    public Calendar getCalendar() {
        return Calendar.getInstance();
    }

    public void updateReminder(Reminder reminder) {
        // Create a new object RealM unattached
        db.updateReminder(reminder);
    }

    public ListAdapter getAdapter() {
        return new RemindersAdapter(activity, R.layout.activity_reminder_item, db.getReminders());
    }

    public void addReminder(long id, Date alarmTime, String label, String metric, boolean oneTime, boolean active) {
        Reminder reminder = new Reminder(id, alarmTime, label, metric, oneTime, active);
        boolean added = db.addReminder(reminder);
        if (added) {
            activity.updateRemindersList();
            saveReminders();
        } else {
            activity.showDuplicateError();
        }
    }

    public void deleteReminder(long id) {
        db.deleteReminder(id);
        saveReminders();
    }

    public void saveReminders() {
        GlucosioAlarmManager alarmManager = new GlucosioAlarmManager(activity.getApplicationContext());
        alarmManager.setAlarms();
    }
}
