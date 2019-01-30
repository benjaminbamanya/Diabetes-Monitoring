package faithworks.diabetesmonitoring.android;

import faithworks.diabetesmonitoring.android.analytics.Analytics;
import faithworks.diabetesmonitoring.android.backup.Backup;
import faithworks.diabetesmonitoring.android.db.DatabaseHandler;
import faithworks.diabetesmonitoring.android.presenter.HelloPresenter;
import faithworks.diabetesmonitoring.android.tools.LocaleHelper;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@Ignore
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 25)
public abstract class RobolectricTest {
    protected Analytics getAnalytics() {
        return getTestApplication().getAnalytics();
    }

    protected Backup getBackup() {
        return getTestApplication().getBackup();
    }

    private TestGlucosioApplication getTestApplication() {
        return (TestGlucosioApplication) RuntimeEnvironment.application;
    }

    protected DatabaseHandler getDBHandler() {
        return getTestApplication().getDBHandler();
    }

    protected HelloPresenter getHelloPresenter() {
        //noinspection ConstantConditions
        return getTestApplication().createHelloPresenter(null);
    }

    protected LocaleHelper getLocaleHelper() {
        //noinspection ConstantConditions
        return getTestApplication().getLocaleHelper();
    }
}
