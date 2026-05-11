# Source Code — *Sealed and Delivered*
### REST APIs in Kotlin, from JVM to Android

Source code for every chapter of *Sealed and Delivered* by George Jeffrey Francis
(Ordo Artificum Press, 2026).

**Available on Amazon:**
https://www.amazon.com/gp/product/B0GZDJXYZG/

---

## Organization

Each directory corresponds to a chapter and is **self-contained**.

### Part I — Building REST API Clients in Kotlin (ch04–ch23)

| Directory | Chapter |
|-----------|---------|
| `ch04_first_call/` | Chapter 4: Your First API Call |
| `ch05_java_vs_kotlin/` | Chapter 5: Java vs Kotlin: A Direct Comparison |
| `ch06_data_classes_json/` | Chapter 6: Data Classes and JSON |
| `ch07_credentials/` | Chapter 7: Keeping Secrets |
| `ch08_parameters/` | Chapter 8: Query and Path Parameters |
| `ch09_sending_data/` | Chapter 9: Sending Data |
| `ch10_error_handling/` | Chapter 10: Error Handling with Sealed Classes |
| `ch11_coroutines/` | Chapter 11: Coroutines |
| `ch12_rate_limits/` | Chapter 12: Rate Limits |
| `ch13_retrying/` | Chapter 13: Retrying with Coroutines |
| `ch14_pagination/` | Chapter 14: Pagination |
| `ch15_http_caching/` | Chapter 15: HTTP Caching |
| `ch16_connection_management/` | Chapter 16: Connection Management |
| `ch17_parallel_requests/` | Chapter 17: Parallel Requests |
| `ch18_testing/` | Chapter 18: Testing HTTP Clients |
| `ch19_xml/` | Chapter 19: XML |
| `ch20_auth/` | Chapter 20: Authentication Deep Dive |
| `ch21_oauth/` | Chapter 21: OAuth 2.0 |
| `ch22_webhooks/` | Chapter 22: Webhooks |
| `ch23_complete_client/` | Chapter 23: The Complete Client |

### Android Interlude (ch24–ch31)

| Directory | Chapter |
|-----------|---------|
| `ch25_first_android_call/` | Chapter 25: Your First Android API Call |
| `ch28_viewmodel_repository/` | Chapter 28: ViewModel and Repository |
| `ch31_complete_android_client/` | Chapter 31: The Complete Android Client |

### Part II — Building REST APIs with Spring Boot (ch32–ch49)

| Directory | Chapter |
|-----------|---------|
| `ch32_spring_boot_intro/` | Chapter 32: Introduction to Spring Boot |
| `ch33_routing/` | Chapter 33: Routing with Spring Web MVC |
| `ch34_path_parameters/` | Chapter 34: Path Parameters and Responses |
| `ch35_validation/` | Chapter 35: Validation |
| `ch36_database/` | Chapter 36: Databases with Spring Data JPA |
| `ch37_organization/` | Chapter 37: Organizing a Spring Boot Application |
| `ch38_post/` | Chapter 38: POST and Creating Resources |
| `ch39_put_patch_delete/` | Chapter 39: PUT, PATCH, and DELETE |
| `ch40_error_responses/` | Chapter 40: Error Responses |
| `ch41_kotlin_dsl/` | Chapter 41: Kotlin DSL for REST |
| `ch42_pagination_and_versioning/` | Chapter 42: Pagination and API Versioning |
| `ch43_auth_server/` | Chapter 43: Authentication |
| `ch44_rate_limiting/` | Chapter 44: Rate Limiting |
| `ch45_search/` | Chapter 45: Search |
| `ch46_files_and_webhooks/` | Chapter 46: File Upload and Webhooks |
| `ch47_testing_server/` | Chapter 47: Testing Spring Boot |
| `ch48_performance_and_observability/` | Chapter 48: Performance and Observability |
| `ch49_complete_server/` | Chapter 49: The Complete Server |

---

## Getting Started

**Requirements:** JDK 17+, Gradle

Chapters 4–23 (Part I) call the GitHub API and require a personal access token.
Create a `.env` file in each chapter directory you want to run:

```
GITHUB_TOKEN=your_token_here
```

Build and run any JVM chapter:

```bash
cd ch04_first_call/
./gradlew run
```

Android chapters (24–31) require Android Studio. Open the chapter directory
as an Android Studio project and run on a device or emulator.

Part II (ch32–49) runs a local Spring Boot server:

```bash
cd ch32_spring_boot_intro/
./gradlew bootRun
```
