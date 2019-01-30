package faithworks.diabetesmonitoring.android.activity;

import faithworks.diabetesmonitoring.android.Constants;
import faithworks.diabetesmonitoring.android.RobolectricTest;
import faithworks.diabetesmonitoring.android.db.User;
import faithworks.diabetesmonitoring.android.db.UserBuilder;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreferencesActivityTest extends RobolectricTest {
    private final User user = new UserBuilder()
            .setId(1)
            .setName("test")
            .setPreferredLanguage("en")
            .setCountry("en")
            .setAge(23)
            .setGender("M")
            .setDiabetesType(1)
            .setPreferredUnit(Constants.Units.MG_DL)
            .setPreferredA1CUnit("")
            .setPreferredWeightUnit("")
            .setPreferredRange("Test")
            .setMinRange(0)
            .setMaxRange(100)
            .createUser();
    private PreferencesActivity activity;

    @Before
    public void setUp() {
        when(getDBHandler().getUser(1)).thenReturn(user);

        activity = Robolectric.buildActivity(PreferencesActivity.class).create().get();
    }

    @Test
    public void ShouldReportAnalytics_WhenCreated() {
        verify(getAnalytics()).reportScreen("Preferences");
    }
}
