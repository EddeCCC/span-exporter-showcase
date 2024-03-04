package rocks.inspectit.oce.eum.server.configuration.model.exporters;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import rocks.inspectit.oce.eum.server.configuration.model.exporters.trace.TraceExportersSettings;

/**
 * Extended exporter settings.
 */
@Data
@Validated
public class ExportersSettings {

    /**
     * Exporter settings for trace exporters.
     */
    @Valid
    private TraceExportersSettings tracing;
}
