package com.ronenm.foodreview;

import com.ronenm.foodreview.counterstasks.Reviews;
import com.ronenm.foodreview.spring.ClassPathXmlApplicationContextFactory;
import com.ronenm.foodreview.translatetask.Translator;
import org.springframework.context.ApplicationContext;

public class App {
    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = ClassPathXmlApplicationContextFactory.getOrCreate();

        // Counters tasks
        Reviews counter = applicationContext.getBean(Reviews.class);
        counter.doCounts();

        // Translation tasks
        Translator translate = applicationContext.getBean(Translator.class);
        translate.doTranslate();
    }
}