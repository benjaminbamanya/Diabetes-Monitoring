/*
 * Copyright (C) 2016 Glucosio Foundation
 *
 * This file is part of Glucosio.
 *
 * Glucosio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Glucosio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Glucosio.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package faithworks.diabetesmonitoring.android.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import faithworks.diabetesmonitoring.android.GlucosioApplication;
import org.glucosio.android.R;
import faithworks.diabetesmonitoring.android.analytics.Analytics;
import faithworks.diabetesmonitoring.android.tools.network.GlucosioExternalLinks;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_about);

        getFragmentManager().beginTransaction()
                .replace(R.id.aboutPreferencesFrame, new MyPreferenceFragment()).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.preferences_about_glucosio));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.about_preference);

            final Preference licencesPref = findPreference("preference_licences");
            final Preference ratePref = findPreference("preference_rate");
            final Preference feedbackPref = findPreference("preference_feedback");
            final Preference privacyPref = findPreference("preference_privacy");
            final Preference termsPref = findPreference("preference_terms");
            final Preference versionPref = findPreference("preference_version");
            final Preference thanksPref = findPreference("preference_thanks");


            termsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ExternalLinkActivity.launch(
                            getActivity(),
                            getString(R.string.preferences_terms),
                            GlucosioExternalLinks.TERMS);
                    addTermsAnalyticsEvent("Glucosio Terms opened");
                    return false;
                }
            });

            licencesPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ExternalLinkActivity.launch(
                            getActivity(),
                            getString(R.string.preferences_licences_open),
                            GlucosioExternalLinks.LICENSES);
                    addTermsAnalyticsEvent("Glucosio Licence opened");
                    return false;
                }
            });

            ratePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=org.diabetesmonitoring.android"));
                    startActivity(intent);

                    return false;
                }
            });

            feedbackPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // Open email intent
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:hello@diabetesmonitoring.org"));
                    boolean activityExists = emailIntent.resolveActivityInfo(getActivity().getPackageManager(), 0) != null;

                    if (activityExists) {
                        startActivity(emailIntent);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.menu_support_error1), Toast.LENGTH_LONG).show();
                    }

                    return false;
                }
            });

            privacyPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ExternalLinkActivity.launch(
                            getActivity(),
                            getString(R.string.preferences_privacy),
                            GlucosioExternalLinks.PRIVACY);
                    addTermsAnalyticsEvent("Glucosio Privacy opened");
                    return false;
                }
            });

            thanksPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ExternalLinkActivity.launch(
                            getActivity(),
                            getString(R.string.preferences_contributors),
                            GlucosioExternalLinks.THANKS);
                    addTermsAnalyticsEvent("Glucosio Contributors opened");
                    return false;
                }
            });

            versionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                int easterEggCount;

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (easterEggCount == 6) {
                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 40.794010, 17.124583);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        getActivity().startActivity(intent);
                        easterEggCount = 0;
                    } else {
                        this.easterEggCount = easterEggCount + 1;
                    }
                    return false;
                }
            });

        }

        private void addTermsAnalyticsEvent(String action) {
            Analytics analytics = ((GlucosioApplication) getActivity().getApplication()).getAnalytics();

            analytics.reportAction("Preferences", action);
        }
    }
}
