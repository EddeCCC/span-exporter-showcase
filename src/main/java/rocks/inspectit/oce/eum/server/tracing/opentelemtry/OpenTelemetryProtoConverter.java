package rocks.inspectit.oce.eum.server.tracing.opentelemtry;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.OcelotSpanUtils;
import io.opentelemetry.sdk.trace.data.SpanData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rocks.inspectit.oce.eum.server.utils.RequestUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Component
@Slf4j
public class OpenTelemetryProtoConverter {

    @VisibleForTesting
    Supplier<HttpServletRequest> requestSupplier = RequestUtils::getCurrentRequest;

    /**
     * Converts open-telemetry proto data to the open-telemetry SDK span data.
     *
     * @param data data to convert
     *
     * @return Non-null collection of {@link SpanData}
     */
    public Collection<SpanData> convert(ExportTraceServiceRequest data) {
        List<SpanData> result = new ArrayList<>();

        for (ResourceSpans resourceSpans : data.getResourceSpansList()) {
            // create general span resources, e.g. sdk-version, service-name, ...
            Attributes attributes = OcelotSpanUtils.toAttributes(resourceSpans.getResource().getAttributesList());
            final Resource resource = Resource.create(attributes);

            Map<String, String> customSpanAttributes = getCustomSpanAttributes();

            resourceSpans.getScopeSpansList()
                    .stream()
                    .flatMap(scopeSpans -> toSpanData(scopeSpans, resource, customSpanAttributes))
                    .forEach(result::add);
        }

        return result;
    }

    /**
     * @return Converts an {@link ScopeSpans} instance to a stream of individual {@link SpanData} instances.
     */
    private Stream<SpanData> toSpanData(ScopeSpans scopeSpans, Resource resource, Map<String, String> customSpanAttributes) {
        InstrumentationScope scope = scopeSpans.getScope();
        InstrumentationScopeInfo instrumentationScopeInfo = InstrumentationScopeInfo
                .builder(scope.getName())
                .setVersion(scope.getVersion())
                .build();

        return scopeSpans.getSpansList()
                .stream()
                .map(protoSpan -> OcelotSpanUtils.createSpanData(protoSpan, resource, instrumentationScopeInfo, customSpanAttributes))
                .filter(Objects::nonNull);
    }

    /**
     * @return Returns the current {@link HttpServletRequest} or <code>null</code> in case no request exists.
     */
    @VisibleForTesting
    Map<String, String> getCustomSpanAttributes() {
        HttpServletRequest request = requestSupplier.get();
        if (request == null) {
            return Collections.emptyMap();
        }

        String clientIp = request.getRemoteAddr();

        return ImmutableMap.of("client.ip", clientIp);
    }
}
