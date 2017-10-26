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

import eu.hansolo.medusa.Gauge;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MedusaGauge extends Gauge implements ValuableDynamo {

    private StringProperty currentValueTagName;

    public MedusaGauge() {
        super();
        this.currentValueTagName = new SimpleStringProperty(this, "currentValueTagID", "");
    }

    @Override
    public void setCurrentTagValue(String value) {
        this.setValue(Double.parseDouble(value));
    }

    @Override
    public String getCurrentTagValue() {
        return String.valueOf(getCurrentValue());
    }

    @Override
    public String getCurrentValueTagName() {
        return String.valueOf(currentValueTagName.get());
    }

    @Override
    public StringProperty currentValueTagNameProperty() {
        return currentValueTagName;
    }

    @Override
    public void setCurrentValueTagName(String currentValueTagName) {
        this.currentValueTagName.set(currentValueTagName);
    }

    @Override public String getUserAgentStylesheet() { return Gauge.class.getResource("gauge.css").toExternalForm(); }
}
