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
package org.assemblits.eru.util;

import org.opendolphin.binding.Converter;

/**
 * Created by mtrujillo on 10/11/2015.
 */
public class DolphinUtils {
    public static Converter  STRING_TO_DOUBLE_CONVERTER = new Converter<String,Double>() {
        public Double convert(String value) {
            if(value != null && !value.isEmpty()){
                return Double.parseDouble(value);
            } else {
                return 0.0;
            }
        }
    };
    public static Converter STRING_TO_BOOLEAN_CONVERTER = new Converter<String,Boolean>() {
        public Boolean convert(String value) {
            return value != null && !value.isEmpty() && Boolean.parseBoolean(value);
        }
    };
}
