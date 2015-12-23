package ua.kh.khpi.alex_babenko.aspect;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


import static java.text.MessageFormat.format;

@Aspect
@Component
public class TimerAspect {

    private static final Logger LOG = Logger.getLogger(TimerAspect.class);

    @Pointcut("execution(* ua.kh.khpi.alex_babenko.art.service.ImageDetectionService.*(..))")
    public void imageDetectionServicePointCat() {}

    @Around("imageDetectionServicePointCat()")
    public Object timerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();
        LOG.info(format("Method [{0}] was executed for {1} nanoseconds with result [{2}]", joinPoint.getSignature().getName(), stopWatch.getNanoTime(), result));
        return result;
    }

}
