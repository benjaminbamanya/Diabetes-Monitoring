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

package faithworks.diabetesmonitoring.android.presenter;

import android.text.TextUtils;

import faithworks.diabetesmonitoring.android.db.DatabaseHandler;
import faithworks.diabetesmonitoring.android.db.User;
import faithworks.diabetesmonitoring.android.db.UserBuilder;
import faithworks.diabetesmonitoring.android.view.HelloView;


public class HelloPresenter {
    private final DatabaseHandler dB;
    private final HelloView helloView;

    private int id;
    private String name;

    public HelloPresenter(final HelloView helloView, final DatabaseHandler dbHandler) {
        this.helloView = helloView;
        dB = dbHandler;
    }

    public void loadDatabase() {
        id = 1; // Id is always 1. We don't support multi-user (for now :D).
        name = "Test Account"; //TODO: add input for name in Tips;
    }

    public void onNextClicked(String age, String gender, String language, String country, int type, String unit) {
        if (validateAge(age)) {
            saveToDatabase(id, name, language, country, Integer.parseInt(age), gender, type, unit);
            helloView.startMainView();
        } else {
            helloView.displayErrorWrongAge();
        }
    }

    private boolean validateAge(String age) {
        if (TextUtils.isEmpty(age)) {
            return false;
        } else if (!TextUtils.isDigitsOnly(age)) {
            return false;
        } else {
            int finalAge = Integer.parseInt(age);
            return finalAge > 0 && finalAge < 120;
        }
    }

    private void saveToDatabase(final int id, final String name, final String language,
                                final String country, final int age, final String gender,
                                final int diabetesType, final String unitMeasurement) {
        User user = new UserBuilder()
                .setId(id)
                .setName(name)
                .setPreferredLanguage(language)
                .setCountry(country)
                .setAge(age)
                .setGender(gender)
                .setDiabetesType(diabetesType)
                .setPreferredUnit(unitMeasurement)
                .setPreferredA1CUnit("percentage")
                .setPreferredWeightUnit("kilograms")
                .setPreferredRange("ADA")
                .setMinRange(70)
                .setMaxRange(180)
                .createUser();
        dB.addUser(user); // We use ADA range by default
    }
}
