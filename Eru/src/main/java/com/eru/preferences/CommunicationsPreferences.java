package com.eru.preferences;

import org.springframework.stereotype.Component;

/**
 * Created by mtrujillo on 9/13/17.
 */
@Component
public class CommunicationsPreferences {

    static final int DEFAULT_MODBUS_BLOCK_MAX_LIMIT = 120;
    static final String MODBUS_BLOCK_MAX_LIMIT = "MODBUS_BLOCK_MAX_LIMIT";
    private int modbusBlockMaxLimit = DEFAULT_MODBUS_BLOCK_MAX_LIMIT;

    public CommunicationsPreferences() {
    }

    public int getModbusBlockMaxLimit() {
        return modbusBlockMaxLimit;
    }

    public void setModbusBlockMaxLimit(int modbusBlockMaxLimit) {
        this.modbusBlockMaxLimit = modbusBlockMaxLimit;
    }
}
