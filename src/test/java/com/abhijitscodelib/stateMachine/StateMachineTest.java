package com.abhijitscodelib.stateMachine;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class StateMachineTest {
    @Test
    void testHelloEndpoint() {
        given()
          .when().get("/state-machine")
          .then()
             .statusCode(200)
             .body(is("Hello from RESTEasy Reactive"));
    }

}