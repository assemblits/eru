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
package org.assemblits.eru.gui.dynamo;

import com.eru.dynamo.control.Display;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EruDisplay extends Display implements ValuableDynamo {

    private StringProperty currentValueTagName;

    public EruDisplay() {
        super();
        this.currentValueTagName = new SimpleStringProperty(this, "currentValueTagName", "");
    }

    @Override
    public String getCurrentTagValue() {
        return getCurrentText();
    }

    @Override
    public void setCurrentTagValue(String value) {
        setCurrentText(value);
    }

    @Override
    public String getCurrentValueTagName() {
        return currentValueTagName.get();
    }

    @Override
    public void setCurrentValueTagName(String currentValueTagID) {
        this.currentValueTagName.set(currentValueTagID);
    }

    @Override
    public StringProperty currentValueTagNameProperty() {
        return currentValueTagName;
    }

    @Override
    public String getUserAgentStylesheet() {
        return Display.class.getResource("display.css").toExternalForm();
    }
}
