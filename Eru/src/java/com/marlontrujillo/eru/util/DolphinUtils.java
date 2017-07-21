package com.marlontrujillo.eru.util;

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
