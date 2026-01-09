package com.example.atiperagithubproxy;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
class GithubService {

    private final GithubClient client;

    GithubService(GithubClient client) {
        this.client = client;
    }

    List<RepositoryResponse> getNonForkRepositories(String username) {
        var repos = client.getRepositories(username);

        return repos.stream()
                .filter(repo -> !repo.fork())
                .map(repo -> new RepositoryResponse(
                        repo.name(),
                        repo.owner().login(),
                        client.getBranches(username, repo.name()).stream()
                                .map(branch -> new BranchResponse(
                                        branch.name(),
                                        branch.commit().sha()
                                ))
                                .toList()
                ))
                .toList();
    }
}
