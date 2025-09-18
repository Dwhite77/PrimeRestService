package org.example.primeapi.exception;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Slf4j
@SpringBootTest(
        classes = org.example.primeapi.PrimeApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GlobalExceptionHandlerIntegrationTest {


    @LocalServerPort
    private int port;

    @BeforeAll
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port; // or whatever port your app runs on
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

    }


    @Test
    void missingLimitParameterReturns400() {
        Response response = given()
                .accept("application/json")
                .queryParam("algorithm", "sieve")
                .queryParam("threads", 2)
                .get("/api/primes");

        response.then()
                .statusCode(400)
                .body("error.status", equalTo(400))
                .body("error.message", containsString("Missing required parameter"))
                .body("error.path", equalTo("/api/primes"))
                .body("timestamp", matchesRegex("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    void invalidLimitTypeReturns400() {
        Response response = given()
                .accept("application/json")
                .queryParam("limit", "abc")
                .queryParam("algorithm", "sieve")
                .queryParam("threads", 2)
                .get("/api/primes");

        response.then()
                .statusCode(400)
                .body("error.status", equalTo(400))
                .body("error.message", containsString("Invalid value for parameter"))
                .body("error.path", equalTo("/api/primes"))
                .body("timestamp", matchesRegex("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    void unsupportedAlgorithmReturns400() {
        Response response = given()
                .accept("application/json")
                .queryParam("limit", 100)
                .queryParam("algorithm", "unknown")
                .queryParam("threads", 2)
                .get("/api/primes");

        response.then()
                .statusCode(400)
                .body("error.status", equalTo(400))
                .body("error.message", containsString("Unsupported algorithm"))
                .body("error.path", equalTo("/api/primes"))
                .body("timestamp", matchesRegex("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    void unknownPathReturns404() {
        Response response = given()
                .accept("application/json")
                .get("/api/does-not-exist");

        response.then()
                .statusCode(404)
                .body("error.status", equalTo(404))
                .body("error.message", containsString("Unknown path"))
                .body("error.path", equalTo("/api/does-not-exist"))
                .body("timestamp", matchesRegex("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    void postToGetEndpointReturns405() {
        Response response = given()
                .accept("application/json")
                .post("/api/primes");

        response.then()
                .statusCode(405)
                .body("error.status", equalTo(405)) // now matches outer status
                .body("error.message", containsString("Method"))
                .body("error.path", equalTo("/api/primes"))
                .body("timestamp", matchesRegex("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}"));
    }


    @Test
    void genericExceptionReturns500() {
        Response response = given()
                .accept("application/json")
                .get("/api/trigger-runtime-exception"); // you’ll need to create this endpoint

        response.then()
                .statusCode(500)
                .body("error.status", equalTo(500))
                .body("error.message", containsString("Unexpected error occurred"))
                .body("error.path", equalTo("/api/trigger-runtime-exception"))
                .body("timestamp", matchesRegex("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    void responseStatusExceptionReturnsExpectedStatus() {
        Response response = given()
                .accept("application/json")
                .get("/api/trigger-response-status"); // You’ll need to create this endpoint

        response.then()
                .statusCode(418)
                .body("error.status", equalTo(400))
                .body("error.message", equalTo("I'm a teapot"))
                .body("error.path", equalTo("/api/trigger-response-status"))
                .body("timestamp", matchesRegex("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    void illegalArgumentExceptionReturns400() {
        Response response = given()
                .accept("application/json")
                .get("/api/trigger-illegal-argument");

        response.then()
                .statusCode(400)
                .body("error.status", equalTo(400))
                .body("error.message", containsString("Illegal argument"))
                .body("error.path", equalTo("/api/trigger-illegal-argument"))
                .body("timestamp", matchesRegex("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}"));
    }

}
