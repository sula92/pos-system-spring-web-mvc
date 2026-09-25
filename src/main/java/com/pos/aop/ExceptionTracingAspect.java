package com.pos.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Logs exception context from service/repository calls.
 *
 * Why this is useful:
 * - Keeps failure diagnostics centralized and consistent.
 * - Complements GlobalExceptionHandler with deeper method-level context.
 */
@Aspect
@Component
public class ExceptionTracingAspect {

    private static final Logger logger = Logger.getLogger(ExceptionTracingAspect.class.getName());

    /**
     * AfterThrowing advice runs only when an exception escapes the target method.
     * It captures method signature + arguments + exception type/message.
     */
    @AfterThrowing(
            pointcut = "execution(* com.pos.service..*(..)) || execution(* com.pos.repository..*(..))",
            throwing = "exception"
    )
    public void logExceptionContext(JoinPoint joinPoint, Throwable exception) {
        String method = joinPoint.getSignature().toShortString();
        String args = Arrays.toString(joinPoint.getArgs());

        logger.severe("[AOP-EX] Method failed: " + method +
                " | args=" + args +
                " | type=" + exception.getClass().getSimpleName() +
                " | message=" + exception.getMessage());
    }
}

