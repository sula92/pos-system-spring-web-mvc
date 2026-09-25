package com.pos.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Measures execution time for service and controller methods.
 *
 * Why this is useful:
 * - Identifies slow endpoints and business operations.
 * - Helps track regressions after query or mapping changes.
 */
@Aspect
@Component
public class PerformanceAspect {

    private static final Logger logger = Logger.getLogger(PerformanceAspect.class.getName());
    private static final long SLOW_THRESHOLD_MS = 200L;

    /**
     * Around advice wraps method execution to compute duration.
     * The pointcut intentionally targets controller + service packages where
     * business latency is most relevant.
     */
    @Around("execution(* com.pos.controller..*(..)) || execution(* com.pos.service..*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startedAt = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long elapsedMs = System.currentTimeMillis() - startedAt;
            String signature = joinPoint.getSignature().toShortString();

            if (elapsedMs >= SLOW_THRESHOLD_MS) {
                logger.warning("[AOP-PERF] Slow call: " + signature + " took " + elapsedMs + " ms");
            } else {
                logger.info("[AOP-PERF] " + signature + " took " + elapsedMs + " ms");
            }
        }
    }
}

