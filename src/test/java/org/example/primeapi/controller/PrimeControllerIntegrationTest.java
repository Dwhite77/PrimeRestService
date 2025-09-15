package org.example.primeapi.controller;

import io.restassured.RestAssured;
import org.example.primeapi.PrimeApiApplication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PrimeControllerIntegrationTest {

    @BeforeAll
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080; // or whatever port your app runs on
    }

    @Test
    void testValidPrimeRequest() {
        given()
                .queryParam("limit", 30)
                .queryParam("algorithm", "sieve")
                .queryParam("threads", 2)
                .when()
                .get("/api/primes")
                .then()
                .statusCode(200)
                .body("data.algorithm", equalTo("sieve"))
                .body("data.limit", equalTo(30))
                .body("data.total", greaterThan(0))
                .body("data.primes", hasItem(29));
    }

    @Test
    void testInvalidAlgorithm() {
        given()
                .queryParam("limit", 30)
                .queryParam("algorithm", "invalid")
                .when()
                .get("/api/primes")
                .then()
                .statusCode(400)
                .body("error.message", containsString("Unsupported algorithm"));
    }

    @Test
    void testNegativeLimit() {
        given()
                .queryParam("limit", -10)
                .when()
                .get("/api/primes")
                .then()
                .statusCode(400)
                .body("error.message", containsString("Limit must be non-negative"));
    }

    @Test
    void testXmlResponse() {
        given()
                .queryParam("limit", 20)
                .queryParam("algorithm", "trial")
                .accept("application/xml")
                .when()
                .get("/api/primes")
                .then()
                .statusCode(200)
                .contentType("application/xml");
    }
}
