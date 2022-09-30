package io.fishmaster.ms.be.pub.building.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import io.fishmaster.ms.be.pub.building.utility.MDCThreadPoolExecutor;

@EnableScheduling
@Configuration
public class SchedulerConfiguration implements SchedulingConfigurer {

    @Value("${scheduler.thread-pool.size}")
    private Integer poolSize;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(MDCThreadPoolExecutor.getScheduledInstance(poolSize));
    }
}
