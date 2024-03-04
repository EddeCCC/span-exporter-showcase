package rocks.inspectit.oce.eum.server.exporters;

import com.google.common.io.CharStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStreamReader;
import java.io.Reader;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ExporterIntMockMvcTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Value("classpath:ot-trace-array-v0.48.0.json")
    private Resource resourceSpans;

    public static final String SERVICE_NAME = "E2E-test";

    // Trace-Id used in the resource spans
    protected static String RESOURCE_TRACE_ID = "a4a68b53c52438381b6cb304410ff0be";

    /**
     * Posts a {@code Span} to {@link rocks.inspectit.oce.eum.server.rest.TraceController#spans(String)}
     *
     * @param traceId the trace id to be used
     */
    protected void postSpan(String traceId) throws Exception {
        mockMvc.perform(post("/spans").contentType(MediaType.APPLICATION_JSON).content(getSpanString(traceId)))
                .andExpect(status().isAccepted());

    }

    /**
     * Posts a {@code Span} to {@link rocks.inspectit.oce.eum.server.rest.TraceController#spans(String)}.
     * The span data will be read from a file.
     * <br>
     * Currently, OTel is not able to process arrayValue objects in Attributes.
     * Instead, all values will be merged to one string.
     * See <a href="https://github.com/open-telemetry/opentelemetry-java/issues/6243">issue</a>
     *
     */
    protected void postResourceSpans() throws Exception {
        try (Reader reader = new InputStreamReader(resourceSpans.getInputStream())) {
            String json = CharStreams.toString(reader);

            mockMvc.perform(post("/spans").contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isAccepted());
        }
    }

    /**
     * Returns a request string for a {@code Span}
     *
     * @param traceId the trace id
     *
     * @return String of a dummy span with provided trace id
     */
    private String getSpanString(String traceId) {
        String now = System.currentTimeMillis() + "000001";
        return "{\"resourceSpans\":[{\"resource\":{\"attributes\":[{\"key\":\"service.name\",\"value\":{\"stringValue\":\"" + SERVICE_NAME + "\"}},{\"key\":\"telemetry.sdk.language\",\"value\":{\"stringValue\":\"webjs\"}},{\"key\":\"telemetry.sdk.name\",\"value\":{\"stringValue\":\"opentelemetry\"}},{\"key\":\"telemetry.sdk.version\",\"value\":{\"stringValue\":\"0.18.2\"}}],\"droppedAttributesCount\":0},\"scopeSpans\":[{\"spans\":[{\"traceId\":\"" + traceId + "\",\"spanId\":\"fc3d735ad8dd7399\",\"name\":\"HTTP GET\",\"kind\":3,\"startTimeUnixNano\":" + now + ",\"endTimeUnixNano\":" + now + ",\"attributes\":[{\"key\":\"http.method\",\"value\":{\"stringValue\":\"GET\"}},{\"key\":\"http.url\",\"value\":{\"stringValue\":\"http://localhost:1337?command=undefined\"}},{\"key\":\"http.response_content_length\",\"value\":{\"intValue\":665}},{\"key\":\"http.status_code\",\"value\":{\"intValue\":200}},{\"key\":\"http.status_text\",\"value\":{\"stringValue\":\"OK\"}},{\"key\":\"http.host\",\"value\":{\"stringValue\":\"localhost:1337\"}},{\"key\":\"http.scheme\",\"value\":{\"stringValue\":\"http\"}},{\"key\":\"http.user_agent\",\"value\":{\"stringValue\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.128 Safari/537.36\"}}],\"droppedAttributesCount\":0,\"events\":[{\"timeUnixNano\":1619187815416888600,\"name\":\"open\",\"attributes\":[],\"droppedAttributesCount\":0},{\"timeUnixNano\":1619187815417378600,\"name\":\"send\",\"attributes\":[],\"droppedAttributesCount\":0},{\"timeUnixNano\":1619187815418218800,\"name\":\"fetchStart\",\"attributes\":[],\"droppedAttributesCount\":0},{\"timeUnixNano\":1619187815420648700,\"name\":\"domainLookupStart\",\"attributes\":[],\"droppedAttributesCount\":0},{\"timeUnixNano\":1619187815420648700,\"name\":\"domainLookupEnd\",\"attributes\":[],\"droppedAttributesCount\":0},{\"timeUnixNano\":1619187815420648700,\"name\":\"connectStart\",\"attributes\":[],\"droppedAttributesCount\":0},{\"timeUnixNano\":1619170468572063700,\"name\":\"secureConnectionStart\",\"attributes\":[],\"droppedAttributesCount\":0},{\"timeUnixNano\":1619187815723468800,\"name\":\"connectEnd\",\"attributes\":[],\"droppedAttributesCount\":0},{\"timeUnixNano\":1619187815723523600,\"name\":\"requestStart\",\"attributes\":[],\"droppedAttributesCount\":0},{\"timeUnixNano\":1619187815732868600,\"name\":\"responseStart\",\"attributes\":[],\"droppedAttributesCount\":0},{\"timeUnixNano\":1619187815734768600,\"name\":\"responseEnd\",\"attributes\":[],\"droppedAttributesCount\":0},{\"timeUnixNano\":1619187815735928600,\"name\":\"loaded\",\"attributes\":[],\"droppedAttributesCount\":0}],\"droppedEventsCount\":0,\"status\":{\"code\":0},\"links\":[],\"droppedLinksCount\":0}],\"scope\":{\"name\":\"@opentelemetry/instrumentation-xml-http-request\",\"version\":\"0.18.2\"}}]}]}";
    }
}
