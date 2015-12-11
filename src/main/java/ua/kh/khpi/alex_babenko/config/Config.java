package ua.kh.khpi.alex_babenko.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@PropertySource("classpath:config.properties")
@EnableScheduling
public class Config {

}
