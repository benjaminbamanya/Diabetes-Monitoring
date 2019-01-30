package faithworks.diabetesmonitoring.android.presenter;

import faithworks.diabetesmonitoring.android.Constants;
import faithworks.diabetesmonitoring.android.activity.A1cCalculatorActivity;
import faithworks.diabetesmonitoring.android.db.DatabaseHandler;
import faithworks.diabetesmonitoring.android.db.User;
import faithworks.diabetesmonitoring.android.db.UserBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class A1CCalculatorPresenterTest {
    private final User user = new UserBuilder()
            .setId(1).setName("test")
            .setPreferredLanguage("en")
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
    @Mock
    private DatabaseHandler dbHandlerMock;
    @Mock
    private A1cCalculatorActivity activity;
    @InjectMocks
    private A1CCalculatorPresenter presenter;

    @Before
    public void setUp() {
        when(dbHandlerMock.getUser(anyLong())).thenReturn(user);
    }

    @Test
    public void ShouldReturnZero_WhenNullStringPassed() {
        assertThat(presenter.calculateA1C(null)).isZero();
    }

    @Test
    public void ShouldReturnZero_WhenEmptyStringPassed() {
        assertThat(presenter.calculateA1C(null)).isZero();
    }

    @Test
    public void ShouldReturnZero_WhenDecimalSeparatorStringPassed() {
        assertThat(presenter.calculateA1C(".")).isZero();
    }

    @Test
    public void ShouldReturnZero_WhenAnotherDecimalSeparatorStringPassed() {
        assertThat(presenter.calculateA1C(",")).isZero();
    }

    @Test
    public void ShouldCalculatable_WhenUserPreferredMmol() {
        user.setPreferred_unit(Constants.Units.MMOL_L);

        assertThat(presenter.calculateA1C("1")).isEqualTo(2.25);
    }

    @Test
    public void ShouldCalculatable_WhenUserPreferredPercentage() {
        user.setPreferred_unit_a1c("mmol/mol");

        assertThat(presenter.calculateA1C("20")).isEqualTo(1.84);
    }

    @Test
    public void ShouldCallSetMmol_WhenUserPreferredUnitIsNotMgDl() {
        user.setPreferred_unit(Constants.Units.MMOL_L);

        presenter.checkGlucoseUnit();

        verify(activity).setMmol();
    }

    @Test
    public void ShouldReturnCorrectA1CUnit_WhenGetterCalled() {
        user.setPreferred_unit_a1c("mmol/mol");

        assertThat(presenter.getA1cUnit()).isEqualTo("mmol/mol");
    }

    @Test
    public void ShouldFinishActivityAfterSaving_WhensaveA1CCalled() {
        user.setPreferred_unit_a1c("mmol/mol");

        presenter.saveA1C(presenter.calculateA1C("20"));

        verify(activity).finish();
    }
}
