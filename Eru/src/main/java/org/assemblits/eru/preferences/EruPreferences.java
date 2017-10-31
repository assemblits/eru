/******************************************************************************
 * Copyright (c) 2017 Assemblits contributors                                 *
 *                                                                            *
 * This file is part of Eru The open JavaFX SCADA by Assemblits Organization. *
 *                                                                            *
 * Eru The open JavaFX SCADA is free software: you can redistribute it        *
 * and/or modify it under the terms of the GNU General Public License         *
 *  as published by the Free Software Foundation, either version 3            *
 *  of the License, or (at your option) any later version.                    *
 *                                                                            *
 * Eru is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.            *
 ******************************************************************************/
package org.assemblits.eru.preferences;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.gui.Application;
import org.springframework.stereotype.Component;

import java.util.prefs.Preferences;

@Slf4j
@Data
@Component
public class EruPreferences {

    private final Preferences appPreferences;
    private final EruPreference<Application.Theme> theme;
    private final EruPreference<String> eruDatabaseURL;
    private final EruPreference<String> eruDatabaseUsername;
    private final EruPreference<String> eruDatabasePassword;
    private final EruPreference<Boolean> animationEnabled;
    private final EruPreference<Integer> animationDuration;
    private final EruPreference<Integer> modbusBlockMaxLimit;
    private final EruPreference<Boolean> historianEnabled;
    private final EruPreference<Integer> historianLimit;
    private final EruPreference<Integer> historianSamplingTime;
    private final EruPreference<Boolean> alarmingEnabled;
    private final EruPreference<Integer> alarmingLimit;
    private final EruPreference<Integer> alarmingSamplingTime;
    private final EruPreference<Boolean> alarmingHornEnabled;
    private final EruPreference<String> applicationDirectory;
    private final EruPreference<Boolean> applicationConfigured;

    public EruPreferences() {
        appPreferences = Preferences.userNodeForPackage(Application.class);

        eruDatabaseURL = new EruPreference<>(this, "eru.database.url", "jdbc:mysql://localhost:3306/eru?useSSL=false");
        eruDatabaseURL.setValue(appPreferences.get(eruDatabaseURL.getName(), eruDatabaseURL.getDefaultValue()));
        eruDatabaseURL.addListener(observable -> save(eruDatabaseURL.getName(), eruDatabaseURL.getValue()));

        eruDatabaseUsername = new EruPreference<>(this, "eru.database.username", "eru");
        eruDatabaseUsername.setValue(appPreferences.get(eruDatabaseUsername.getName(), eruDatabaseUsername.getDefaultValue()));
        eruDatabaseUsername.addListener(observable -> save(eruDatabaseUsername.getName(), eruDatabaseUsername.getValue()));

        eruDatabasePassword = new EruPreference<>(this, "eru.database.password", "951753");
        eruDatabasePassword.setValue(appPreferences.get(eruDatabasePassword.getName(), eruDatabasePassword.getDefaultValue()));
        eruDatabasePassword.addListener(observable -> save(eruDatabasePassword.getName(), eruDatabasePassword.getValue()));

        theme = new EruPreference<>(this, "theme", Application.Theme.DEFAULT);
        theme.setValue(Application.Theme.valueOf(appPreferences.get(theme.getName(), theme.getDefaultValue().name())));
        theme.addListener(observable -> save(theme.getName(), theme.getValue()));

        animationEnabled = new EruPreference<>(this, "animations.enabled", true);
        animationEnabled.setValue(appPreferences.getBoolean(animationEnabled.getName(), animationEnabled.getDefaultValue()));
        animationEnabled.addListener((observable, oldValue, newValue) -> save(animationEnabled.getName(), newValue));

        animationDuration = new EruPreference<>(this, "animations.duration", 5000);
        animationDuration.setValue(appPreferences.getInt(animationDuration.getName(), animationDuration.getDefaultValue()));
        animationDuration.addListener((observable, oldValue, newValue) -> save(animationDuration.getName(), newValue));

        modbusBlockMaxLimit = new EruPreference<>(this, "modbus.block.maxLimit", 120);
        modbusBlockMaxLimit.setValue(appPreferences.getInt(modbusBlockMaxLimit.getName(), modbusBlockMaxLimit.getDefaultValue()));
        modbusBlockMaxLimit.addListener((observable, oldValue, newValue) -> save(modbusBlockMaxLimit.getName(), newValue));

        historianEnabled = new EruPreference<>(this, "historian.enabled", true);
        historianEnabled.setValue(appPreferences.getBoolean(historianEnabled.getName(), historianEnabled.getDefaultValue()));
        historianEnabled.addListener((observable, oldValue, newValue) -> save(historianEnabled.getName(), newValue));

        historianLimit = new EruPreference<>(this, "historian.limit", 1000);
        historianLimit.setValue(appPreferences.getInt(historianLimit.getName(), historianLimit.getDefaultValue()));
        historianLimit.addListener((observable, oldValue, newValue) -> save(historianLimit.getName(), newValue));

        historianSamplingTime = new EruPreference<>(this, "historian.samplingTime", 600_000);
        historianSamplingTime.setValue(appPreferences.getInt(historianSamplingTime.getName(), historianSamplingTime.getDefaultValue()));
        historianSamplingTime.addListener((observable, oldValue, newValue) -> save(historianSamplingTime.getName(), newValue));

        alarmingEnabled = new EruPreference<>(this, "alarming.enabled", true);
        alarmingEnabled.setValue(appPreferences.getBoolean(alarmingEnabled.getName(), alarmingEnabled.getDefaultValue()));
        alarmingEnabled.addListener((observable, oldValue, newValue) -> save(alarmingEnabled.getName(), newValue));

        alarmingLimit = new EruPreference<>(this, "alarming.limit", 1000);
        alarmingLimit.setValue(appPreferences.getInt(alarmingLimit.getName(), alarmingLimit.getDefaultValue()));
        alarmingLimit.addListener((observable, oldValue, newValue) -> save(alarmingLimit.getName(), newValue));

        alarmingSamplingTime = new EruPreference<>(this, "alarming.samplingTime", 500);
        alarmingSamplingTime.setValue(appPreferences.getInt(alarmingSamplingTime.getName(), alarmingSamplingTime.getDefaultValue()));
        alarmingSamplingTime.addListener((observable, oldValue, newValue) -> save(alarmingSamplingTime.getName(), newValue));

        alarmingHornEnabled = new EruPreference<>(this, "alarming.horn.enabled", true);
        alarmingHornEnabled.setValue(appPreferences.getBoolean(alarmingHornEnabled.getName(), alarmingHornEnabled.getDefaultValue()));
        alarmingHornEnabled.addListener((observable, oldValue, newValue) -> save(alarmingHornEnabled.getName(), newValue));

        applicationDirectory = new EruPreference<>(this, "app.directory", System.getProperty("user.home") + "/.eru/");
        applicationDirectory.setValue(appPreferences.get(applicationDirectory.getName(), applicationDirectory.getDefaultValue()));
        applicationDirectory.addListener((observable, oldValue, newValue) -> save(applicationDirectory.getName(), newValue));

        applicationConfigured = new EruPreference<>(this, "app.configured", false);
        applicationConfigured.setValue(appPreferences.getBoolean(applicationConfigured.getName(), applicationConfigured.getDefaultValue()));
        applicationConfigured.addListener((observable, oldValue, newValue) -> save(applicationConfigured.getName(), newValue));
    }

    private void save(String name, Object value){
        log.info("Saving preferences...");
        if (value instanceof String){
            appPreferences.put(name, (String) value);
        } else if (value instanceof Integer){
            appPreferences.putInt(name, (Integer) value);
        } else if (value instanceof Boolean){
            appPreferences.putBoolean(name, (Boolean) value);
        } else if (value instanceof Application.Theme){
            appPreferences.put(name, ((Application.Theme) value).name());
        }
    }

}
