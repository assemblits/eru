package org.assemblits.eru.contract;

import lombok.Data;

import java.util.List;

/**
 * Created by marlontrujillo1080 on 10/17/17.
 */
@Data
public class Agent {
    private Object client;
    private List<Contract> contracts;
}
