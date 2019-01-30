package faithworks.diabetesmonitoring.android.presenter;

import faithworks.diabetesmonitoring.android.RobolectricTest;
import faithworks.diabetesmonitoring.android.tools.network.GlucosioExternalLinks;
import faithworks.diabetesmonitoring.android.tools.network.NetworkConnectivity;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExternalViewPresenterTest extends RobolectricTest {

    private ExternalViewPresenter.View view;
    private ExternalViewPresenter presenter;
    private NetworkConnectivity network;

    @Before
    public void setUp() {
        view = mock(ExternalViewPresenter.View.class);
        network = mock(NetworkConnectivity.class);
        presenter = new ExternalViewPresenter(view, network);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowException_WhenNoParameters() {
        when(network.isConnected()).thenReturn(true);
        when(view.extractTitle()).thenReturn(null);
        when(view.extractUrl()).thenReturn(null);
        presenter.onViewCreated();
    }

    @Test
    public void shouldLoadOpenSourceLicenses_WhenLicenseParameters() {
        String LICENSES = "licenses";
        when(view.extractUrl()).thenReturn(GlucosioExternalLinks.LICENSES);
        when(view.extractTitle()).thenReturn(LICENSES);
        when(network.isConnected()).thenReturn(true);

        presenter.onViewCreated();

        verify(view).loadExternalUrl(GlucosioExternalLinks.LICENSES);
        verify(view).setupToolbarTitle(LICENSES);
    }

    @Test
    public void shouldInvokeShowNoConnectionWarning_WhenNetworkIsNotConnected() {
        when(network.isConnected()).thenReturn(false);
        presenter.onViewCreated();
        verify(view).showNoConnectionWarning();
    }
}