package org.assemblits.eru.gui;

import org.springframework.context.ApplicationContext;

public class ApplicationContextHolder {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("Application context should be assigned before");
        }
        return applicationContext;
    }

    static void setApplicationContext(ApplicationContext appContext) {
        applicationContext = appContext;
    }
}
