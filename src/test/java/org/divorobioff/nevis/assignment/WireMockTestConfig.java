package org.divorobioff.nevis.assignment;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

@TestConfiguration
@ActiveProfiles("test")
public class WireMockTestConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer wireMockServer() {
        WireMockServer server = new WireMockServer(WireMockConfiguration.options().port(8089));

        server.stubFor(post("/v1/embeddings")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"object\": \"list\", \"data\": [ {\"object\": \"embedding\", \"embedding\": [0.1, 0.2, 0.3], \"index\": 0 } ], \"model\": \"text-embedding-ada-002\", \"usage\": { \"prompt_tokens\": 8, \"total_tokens\": 8 } }")));  // Mock vector

        server.stubFor(post("/v1/chat/completions")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"choices\": [ { \"message\": { \"content\": \"Mocked AI response\" } } ] }")));

        return server;
    }
}
