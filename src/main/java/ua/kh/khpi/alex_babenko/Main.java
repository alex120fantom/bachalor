package ua.kh.khpi.alex_babenko;

import org.apache.log4j.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.kh.khpi.alex_babenko.config.Config;

public class Main {
	
	private static final Logger LOG = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		LOG.info("Main started");
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        Application application = context.getBean(Application.class);
        application.start();
     }

}
