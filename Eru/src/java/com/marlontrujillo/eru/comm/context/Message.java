package com.marlontrujillo.eru.comm.context;

/**
 * Created by mtrujillo on 3/3/2016.
 */
public interface Message {
    void create();
    void send() throws Exception;
    void collectResponse();
}
