package rocks.inspectit.oce.eum.server.configuration.model;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import rocks.inspectit.oce.eum.server.configuration.model.exporters.ExportersSettings;

/**
 * The configuration of the EUM server.
 */
@ConfigurationProperties("inspectit-eum-server")
@Component
@Data
@Validated
public class EumServerConfiguration {

    /**
     * The exporters settings
     */
    @Valid
    private ExportersSettings exporters;
}
