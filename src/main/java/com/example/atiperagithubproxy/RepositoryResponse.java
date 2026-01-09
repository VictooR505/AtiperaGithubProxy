package com.example.atiperagithubproxy;

import java.util.List;

record RepositoryResponse(
        String repositoryName,
        String ownerLogin,
        List<BranchResponse> branches
) {}
