package com.example.atiperagithubproxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
class GithubClient {

    private final RestClient restClient;

    GithubClient(
            RestClient.Builder builder,
            @Value("${github.base-url:https://api.github.com}") String baseUrl
    ) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    List<GithubRepository> getRepositories(String username) {
        return restClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    List<GithubBranch> getBranches(String username, String repo) {
        return restClient.get()
                .uri("/repos/{username}/{repo}/branches", username, repo)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
