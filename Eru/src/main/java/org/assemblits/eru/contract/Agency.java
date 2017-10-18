package org.assemblits.eru.contract;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marlontrujillo1080 on 10/14/17.
 */
@Data
public class Agency {

    private List<Agent> agents = new ArrayList<>();

    public Agent findAgentByClient(Object newClient) {
        for (Agent agent : getAgents()){
            if (agent.getClient() == newClient) return agent;
        }
        return null;
    }

}
