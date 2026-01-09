# GitHub Repositories Proxy API

---

## Functional Requirements

For a given GitHub username, the API:

* returns **only repositories that are not forks**
* for each repository returns:

  * repository name
  * owner login
  * list of branches, each containing:

    * branch name
    * last commit SHA

If the given GitHub user does not exist, the API responds with **HTTP 404** and a custom error body.

---

## API Endpoints

### List user repositories (non-fork only)

```
GET /users/{username}/repositories
```

#### Successful response – `200 OK`

```json
[
  {
    "name": "repository-name",
    "owner": "owner-login",
    "branches": [
      {
        "name": "main",
        "sha": "abc123"
      }
    ]
  }
]
```

#### User not found – `404 Not Found`

```json
{
  "status": 404,
  "message": "GitHub user not found"
}
```

---

## Technology Stack

* **Java 25**
* **Spring Boot 4.0.1**
* **Spring Web MVC**
* **Spring RestClient**
* **Gradle**
* **WireMock**

---

## Running the Application

### Prerequisites

* Java **25** installed and available in the system
* Gradle

### Clone the repository

```bash
git clone <repository-url>
cd <repository-directory>
```

### Build the project

```bash
./gradlew clean build
```

### Run the application

```bash
./gradlew bootRun
```

The application will start on:

```
http://localhost:8080
```

---

## Running Tests

Only **integration tests** are provided, as required. External GitHub API calls are fully emulated using **WireMock**.

To run tests:

```bash
./gradlew test
```
