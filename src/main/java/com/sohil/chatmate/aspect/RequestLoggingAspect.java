package com.sohil.chatmate.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class RequestLoggingAspect {

    @Pointcut("execution(* com.sohil.chatmate.controller..*.*(..))")  // ✔️ Correct
    public void allMethods(){}

    @Before("allMethods()")
    public void requestLogging(JoinPoint joinPoint){
        log.info("Invoked controller method: {}", joinPoint.getSignature().getName());
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            log.info("Argument: {}", arg);
        }        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String uri = request.getRequestURI();
        log.info("Request mapping invoked: " + uri);
    }
}
