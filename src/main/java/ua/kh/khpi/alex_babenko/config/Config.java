package ua.kh.khpi.alex_babenko.config;


import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import ua.kh.khpi.alex_babenko.aspect.TimerAspect;

@Configuration
@ComponentScan("ua.kh.khpi.alex_babenko")
@PropertySource("classpath:config.properties")
@EnableScheduling
@EnableAspectJAutoProxy
public class Config {

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }


}
