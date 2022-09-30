package io.fishmaster.ms.be.pub.building.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MDCUtility {

    private static final String TRACE_ID_MDC_KEY = "TRACE.ID";

    public static final String X_TRACE_ID_HEADER = "X-Trace-Id";

    public static void putTraceId(String traceId) {
        String currentTraceId = MDC.get(TRACE_ID_MDC_KEY);
        if (StringUtils.isBlank(traceId) && StringUtils.isBlank(currentTraceId)) {
            MDC.put(TRACE_ID_MDC_KEY, generateTraceId());
        } else if (StringUtils.isBlank(traceId) && StringUtils.isNotBlank(currentTraceId)) {
            MDC.put(TRACE_ID_MDC_KEY, currentTraceId);
        } else if (StringUtils.isNotBlank(traceId)) {
            MDC.put(TRACE_ID_MDC_KEY, traceId);
        }
    }

    public static byte[] getTraceIdBytes() {
        return getTraceId().getBytes(StandardCharsets.UTF_8);
    }

    public static String getTraceId() {
        var traceId = MDC.get(TRACE_ID_MDC_KEY);
        if (StringUtils.isBlank(traceId)) {
            traceId = generateTraceId();
        }
        return traceId;
    }

    public static void clear() {
        MDC.clear();
    }

    private static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

}
