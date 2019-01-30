package faithworks.diabetesmonitoring.android;

import android.support.annotation.NonNull;

import faithworks.diabetesmonitoring.android.db.DatabaseHandler;
import faithworks.diabetesmonitoring.android.db.User;
import faithworks.diabetesmonitoring.android.db.UserBuilder;
import faithworks.diabetesmonitoring.android.tools.Preferences;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GlucosioApplicationTest {
    private final User user = new UserBuilder()
            .setId(1)
            .setName("test")
            .setPreferredLanguage(null)
            .setCountry("en")
            .setAge(23)
            .setGender("M")
            .setDiabetesType(1)
            .setPreferredUnit(Constants.Units.MG_DL)
            .setPreferredA1CUnit("percentage")
            .setPreferredWeightUnit("")
            .setPreferredRange("Test")
            .setMinRange(0)
            .setMaxRange(100)
            .createUser();
    private GlucosioApplicationForTest application;
    @Mock
    private Preferences preferencesMock;
    @Mock
    private DatabaseHandler databaseHandlerMock;
    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Before
    public void setUp() {
        application = new GlucosioApplicationForTest(databaseHandlerMock, preferencesMock);
    }

    @Test
    public void ShouldClearLanguage_WhenSetFromHelloActivityAndNotFixedYet() {
        when(databaseHandlerMock.getUser(1)).thenReturn(user);

        application.onCreate();

        verify(databaseHandlerMock).updateUser(userCaptor.capture());
        assertThat(userCaptor.getValue().getPreferred_language()).isNull();
    }

    @Test
    public void ShouldSaveLanguageClearedToPreferences_WhenItIsDone() {
        when(databaseHandlerMock.getUser(1)).thenReturn(user);

        application.onCreate();

        verify(preferencesMock).saveLocaleCleaned();
    }

    @Test
    public void ShouldNotClearLanguage_WhenAlreadyDone() {
        user.setPreferred_language("en");

        application.onCreate();

        assertThat(user.getPreferred_language()).isNotNull();
        verify(databaseHandlerMock, never()).updateUser(user);
    }

    static class GlucosioApplicationForTest
            extends GlucosioApplication {

        private final DatabaseHandler dbHandler;
        private final Preferences preferences;

        private GlucosioApplicationForTest(DatabaseHandler dbHandler, Preferences preferences) {
            this.dbHandler = dbHandler;
            this.preferences = preferences;
        }

        @NonNull
        @Override
        public DatabaseHandler getDBHandler() {
            return dbHandler;
        }

        @NonNull
        @Override
        public Preferences getPreferences() {
            return preferences;
        }

        @Override
        protected void initFont() {
            //don't do anything for now
        }
    }
}
