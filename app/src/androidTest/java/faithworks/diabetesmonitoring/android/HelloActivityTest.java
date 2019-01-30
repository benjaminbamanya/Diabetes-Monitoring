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

package faithworks.diabetesmonitoring.android;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.glucosio.android.R;

import faithworks.diabetesmonitoring.android.activity.HelloActivity;
import faithworks.diabetesmonitoring.android.util.CustomClickAction;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * @author amouly on 11/11/15.
 */

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HelloActivityTest {

    private static final String STRING_TYPED_AGE = "23";
    private static final String VALID_COUNTRY = "Argentina";
    private static final String VALID_GENDER = "Other";
    private static final String VALID_TYPE = "Type 2";

    @Rule
    public ActivityTestRule<HelloActivity> mRule = new ActivityTestRule<>(HelloActivity.class);
    private int[] helloActivityViews = {
            R.id.activity_hello_title,
            R.id.activity_hello_subtitle,
            R.id.activity_hello_spinner_country,
            R.id.activity_hello_age,
            R.id.activity_hello_spinner_gender,
            R.id.activity_hello_spinner_diabetes_type,
            R.id.activity_hello_spinner_preferred_unit,
            R.id.activity_hello_check_share};

    @BeforeClass
    public static void clearAppData() {
        try {
            // clearing app data
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear org.diabetesmonitoring.android");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void check_001_IfHelloActivityIsCompletelyDisplayed() {
        for (int id : helloActivityViews) {
            if (id == R.id.activity_hello_check_share) {
                ViewInteraction checkButtonInteraction = onView(withId(id)).perform(scrollTo());
                checkButtonInteraction.check(matches(isDisplayed()));
                continue;
            }
            onView(withId(id)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void check_002_IfStartButtonIsDisplayed() {
        onView(withId(R.id.activity_hello_button_start))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
    }

    @Test
    public void check_003_IfICanUseCountrySpinnerToSelectMyActualCountry() {
        // Click on Country spinner
        onView(withId(R.id.activity_hello_spinner_country)).perform(click());

        // Select random Country
        onData(allOf(is(instanceOf(String.class)), is(VALID_COUNTRY))).perform(click());

        // Locate Spinner view and check its text is equal with COUNTRY
        onView(allOf(withId(R.id.custom_spinner),
                withParent(withId(R.id.activity_hello_spinner_country))))
                .check(matches(withSpinnerText(VALID_COUNTRY)));
    }

    @Test
    public void check_004_IfICanEnterMyAgeUsingHelloAgeEditText() {
        // Enter a valid Age
        onView(withId(R.id.activity_hello_age))
                .perform(typeText(STRING_TYPED_AGE), closeSoftKeyboard());
    }

    @Test
    public void check_005_IfCanUseGenderSpinnerToSelectMyGender() {
        // Click on Gender spinner
        onView(withId(R.id.activity_hello_spinner_gender))
                .perform(CustomClickAction.click());

        // Select random Gender
        onData(allOf(is(instanceOf(String.class)), is(VALID_GENDER)))
                .perform(CustomClickAction.click());

        // Locate Spinner view and check its text is equal with VALID_GENDER
        onView(allOf(withId(R.id.custom_spinner),
                withParent(withId(R.id.activity_hello_spinner_gender))))
                .check(matches(withSpinnerText(VALID_GENDER)));
    }

    @Test
    public void check_006_IfICanUseTypeSpinnerToChangeDiabetesType() {
        // Click on Type spinner
        onView(withId(R.id.activity_hello_spinner_diabetes_type))
                .perform(CustomClickAction.click());

        // Select random Type
        onData(allOf(is(instanceOf(String.class)), is(VALID_TYPE)))
                .perform(CustomClickAction.click());

        // Locate Spinner view and check its text is equal with VALID_TYPE
        onView(allOf(withId(R.id.custom_spinner),
                withParent(withId(R.id.activity_hello_spinner_diabetes_type))))
                .check(matches(withSpinnerText(VALID_TYPE)));
    }

    @Test
    public void check_007_IfICanUseUnitSpinnerToSelectPreferredUnit() {
        // Click on Unit spinner
        onView(withId(R.id.activity_hello_spinner_preferred_unit))
                .perform(CustomClickAction.click());

        // Select random Unit
        onData(allOf(is(instanceOf(String.class)), is(Constants.Units.MG_DL)))
                .perform(CustomClickAction.click());

        // Locate Spinner view and check its text is equal with VALID_UNIT
        onView(allOf(withId(R.id.custom_spinner),
                withParent(withId(R.id.activity_hello_spinner_preferred_unit))))
                .check(matches(withSpinnerText(Constants.Units.MG_DL)));
    }

    @Test
    public void check_008_IfICanUncheckShareDataCheckBox() {
        // Click on Share Data CheckBox multiple times
        onView(withId(R.id.activity_hello_check_share))
                .check(matches(isChecked()))
                .perform(scrollTo(), click()) // this checkButton needs to be scrolled to
                .check(matches(not(isChecked())));
    }

    @Test
    public void check_009_IfICanSubmitAnyData() {
        // Perform submit
        onView(withId(R.id.activity_hello_button_start))
                .perform(scrollTo(), click());
    }

    @Test
    public void check_010_IfIEnterWrongAge() {
        // Entering invalid age
        onView(withId(R.id.activity_hello_age))
                .perform(typeText("0"), closeSoftKeyboard());

        // Click on GET STARTED button
        onView(withId(R.id.activity_hello_button_start))
                .perform(scrollTo(), click());

        // Checking the toast is displayed with invalid message
        onView(withText(R.string.helloactivity_age_invalid))
                .inRoot(withDecorView(not(mRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }
}
