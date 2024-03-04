package rocks.inspectit.oce.eum.server.configuration.model.exporters.trace;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class TraceExportersSettings {

    @Valid
    private OtlpTraceExporterSettings otlp;

    /**
     * The service name. Used in {@link rocks.inspectit.oce.eum.server.exporters.configuration.TraceExportersConfiguration}
     */
    private String serviceName;
}
