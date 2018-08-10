package com.ronenm.foodreview;

import com.ronenm.foodreview.counterstasks.Reviews;
import com.ronenm.foodreview.spring.ClassPathXmlApplicationContextFactory;
import org.springframework.context.ApplicationContext;

public class App {
    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = ClassPathXmlApplicationContextFactory.getOrCreate();

        Reviews counter = applicationContext.getBean(Reviews.class);
        counter.doCounts();


    }
}
