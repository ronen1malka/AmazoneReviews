package com.ronenm.foodreview.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class ClassPathXmlApplicationContextFactory {

    private static final String COMMON_DI_CONFIGURATION = "/di.common.xml";


    private static ClassPathXmlApplicationContext applicationContext = null;

    private ClassPathXmlApplicationContextFactory() {
    }

    public static synchronized ClassPathXmlApplicationContext getOrCreate() {

        if (applicationContext == null) {
            applicationContext = create();
        }

        return applicationContext;
    }

    private static ClassPathXmlApplicationContext create() {

        return new ClassPathXmlApplicationContext(
                COMMON_DI_CONFIGURATION
        );
    }
}
