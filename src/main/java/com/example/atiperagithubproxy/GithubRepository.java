package com.example.atiperagithubproxy;

record GithubRepository(
        String name,
        boolean fork,
        GithubOwner owner
) {}
