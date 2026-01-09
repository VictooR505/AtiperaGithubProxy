package com.example.atiperagithubproxy;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GithubIntegrationTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @LocalServerPort
    int port;

    @Autowired
    RestClient restClient;

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("github.base-url", () -> wireMock.baseUrl());
    }

    @Test
    void shouldReturnNonForkRepositories() {
        wireMock.stubFor(get("/users/test/repos")
                .willReturn(okJson("""
                [
                  {"name":"repo1","fork":false,"owner":{"login":"test"}}
                ]
                """)));

        wireMock.stubFor(get("/repos/test/repo1/branches")
                .willReturn(okJson("""
                [
                  {"name":"main","commit":{"sha":"abc123"}}
                ]
                """)));

        var response = restClient.get()
                .uri("http://localhost:" + port + "/users/test/repositories")
                .retrieve()
                .toEntity(RepositoryResponse[].class);

        Assertions.assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldIgnoreForkRepositories() {
        wireMock.stubFor(get("/users/test/repos")
                .willReturn(okJson("""
            [
              {"name":"repo1","fork":true,"owner":{"login":"test"}}
            ]
            """)));

        var response = restClient.get()
                .uri("http://localhost:" + port + "/users/test/repositories")
                .retrieve()
                .toEntity(RepositoryResponse[].class);

        Assertions.assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldReturn404WhenUserDoesNotExist() {
        wireMock.stubFor(get("/users/unknown/repos")
                .willReturn(aResponse().withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                       {"status":404,"message":"Github user not found"}
                                   """)));

        try {
            restClient.get()
                    .uri("http://localhost:" + port + "/users/unknown/repositories")
                    .retrieve()
                    .toEntity(ErrorResponse.class);

            Assertions.fail("Expected HttpClientErrorException.NotFound to be thrown");
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            Assertions.assertThat(e.getStatusCode().value()).isEqualTo(404);
            String body = e.getResponseBodyAsString();
            Assertions.assertThat(body).contains("not found");
        }
    }
}
