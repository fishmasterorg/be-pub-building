package io.fishmaster.ms.be.pub.building.utility;

import java.util.Objects;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MDCThreadPoolExecutor {

    public static ThreadPoolTaskExecutor getCachedInstance() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(0);
        threadPoolTaskExecutor.setMaxPoolSize(Integer.MAX_VALUE);
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        threadPoolTaskExecutor.setTaskDecorator(new MDCTaskDecorator());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    public static ThreadPoolTaskScheduler getScheduledInstance(Integer poolSize) {
        var threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(poolSize);
        threadPoolTaskScheduler.setThreadNamePrefix("scheduled-task-pool-");
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }

    private static class MDCTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            var webThreadContext = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    if (Objects.nonNull(webThreadContext)) {
                        MDC.setContextMap(webThreadContext);
                    } else {
                        MDCUtility.putTraceId(null);
                    }

                    runnable.run();
                } finally {
                    MDCUtility.clear();
                }
            };
        }
    }

}
