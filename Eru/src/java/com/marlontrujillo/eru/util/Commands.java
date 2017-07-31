package com.marlontrujillo.eru.util;

/**
 * Created by mtrujillo on 9/7/2015.
 */
public class Commands {
    public static final String INITIALIZE_SYSTEM             = "engine.initialize.system";
    public static final String LOAD_DATABASE                 = "database.load";
    public static final String PAUSE_SYNCHRONIZATION         = "engine.synchronize.pause";
    public static final String PAUSE_ALL_SYNCHRONIZATIONS    = "engine.synchronize.all.pause";
    public static final String CONTINUE_SYNCHRONIZATION      = "engine.synchronize.continue";
    public static final String CONTINUE_ALL_SYNCHRONIZATIONS = "engine.synchronize.all.continue";
    public static final String INITIALIZE_PM                 = "engine.initialize.pm";
    public static final String SYNCHRONIZE_PM                = "engine.synchronize.pm";
    public static final String SYNCHRONIZE_SYSTEM_PM         = "engine.synchronize.system.pm";
    public static final String ACKNOWLEDGE_ALARMS            = "alarms.acknowledge";
    public static final String CHECK_USER_ENTRY              = "user.check.entry";
    public static final String START_COMMUNICATIONS          = "user.comm.load";
    public static final String STOP_COMMUNICATIONS           = "user.comm.stop";
    public static final String BLOCK_COMMUNICATIONS          = "user.comm.block";
    public static final String RELEASE_COMMUNICATIONS        = "user.comm.release";
    public static final String START_HISTORIAN               = "user.historian.load";
    public static final String STOP_HISTORIAN                = "user.historian.stop";
    public static final String START_ALARMING                = "user.alarming.load";
    public static final String STOP_ALARMING                 = "user.alarming.stop";
    public static final String WRITE_TAG                     = "write.tag";

}
