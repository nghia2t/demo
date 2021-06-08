package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.acme.model.User;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestSecurity(authorizationEnabled = false)
public class UserResourceTest {

    public User createTestUser() {
        return given().contentType(ContentType.JSON)
                .body("{\n" +
                        "    \"firstName\": \"bb\",\n" +
                        "    \"lastName\": \"aa\",\n" +
                        "    \"grade\": \"1\",\n" +
                        "    \"address\": {\n" +
                        "        \"houseNumber\": \"23123\",\n" +
                        "        \"street\": \"bhn\",\n" +
                        "        \"country\": \"vn\"\n" +
                        "    }\n" +
                        "}")
                .when().put("/api/users").then().extract().body().as(User.class);
    }

    @Test
    public void createUser() {
        given().contentType(ContentType.JSON)
                .body("{\n" +
                        "    \"firstName\": \"bb\",\n" +
                        "    \"lastName\": \"aa\",\n" +
                        "    \"grade\": \"1\",\n" +
                        "    \"address\": {\n" +
                        "        \"houseNumber\": \"23123\",\n" +
                        "        \"street\": \"bhn\",\n" +
                        "        \"country\": \"vn\"\n" +
                        "    }\n" +
                        "}")
                .when().put("/api/users")
                .then()
                .statusCode(201);
    }

    @Test
    public void createUserConflict() {
        User u = createTestUser();
        given().contentType(ContentType.JSON)
                .body("{\n" +
                        "    \"id\": " + u.getId() + ",\n" +
                        "    \"firstName\": \"bb\",\n" +
                        "    \"lastName\": \"aa\",\n" +
                        "    \"grade\": \"1\",\n" +
                        "    \"address\": {\n" +
                        "        \"houseNumber\": \"23123\",\n" +
                        "        \"street\": \"bhn\",\n" +
                        "        \"country\": \"vn\"\n" +
                        "    }\n" +
                        "}")
                .when().put("/api/users")
                .then()
                .statusCode(409);
    }

    @Test
    public void getUser() {
        User u = createTestUser();
        given().pathParam("id", u.getId())
                .when().get("/api/users/{id}")
                .then()
                .statusCode(200);
    }

    @Test
    public void getUserNotFound() {
        given().pathParam("id", 0)
                .when().get("/api/users/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    public void updateUser() {
        User u = createTestUser();
        given().contentType(ContentType.JSON)
                .pathParam("id", u.getId())
                .body("{\n" +
                        "    \"id\": " + u.getId() + ",\n" +
                        "    \"firstName\": \"bb\",\n" +
                        "    \"lastName\": \"aa\",\n" +
                        "    \"grade\": \"2\",\n" +
                        "    \"address\": {\n" +
                        "        \"houseNumber\": \"23123\",\n" +
                        "        \"street\": \"bhn\",\n" +
                        "        \"country\": \"vn\"\n" +
                        "    }\n" +
                        "}")
                .when().post("/api/users/{id}")
                .then()
                .statusCode(200)
                .body("grade", is("2"))
                .extract().body().as(User.class);
    }

    @Test
    public void updateUserNotFound() {
        given().contentType(ContentType.JSON)
                .pathParam("id", 0)
                .body("{\n" +
                        "    \"id\": " + 0 + ",\n" +
                        "    \"firstName\": \"bb\",\n" +
                        "    \"lastName\": \"aa\",\n" +
                        "    \"grade\": \"2\",\n" +
                        "    \"address\": {\n" +
                        "        \"houseNumber\": \"23123\",\n" +
                        "        \"street\": \"bhn\",\n" +
                        "        \"country\": \"vn\"\n" +
                        "    }\n" +
                        "}")
                .when().post("/api/users/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    public void updateUserBadRequest() {
        User u = createTestUser();
        given().contentType(ContentType.JSON)
                .pathParam("id", u.getId() + 1)
                .body("{\n" +
                        "    \"id\": " + u.getId() + ",\n" +
                        "    \"firstName\": \"bb\",\n" +
                        "    \"lastName\": \"aa\",\n" +
                        "    \"grade\": \"2\",\n" +
                        "    \"address\": {\n" +
                        "        \"houseNumber\": \"23123\",\n" +
                        "        \"street\": \"bhn\",\n" +
                        "        \"country\": \"vn\"\n" +
                        "    }\n" +
                        "}")
                .when().post("/api/users/{id}")
                .then()
                .statusCode(400);
    }

    @Test
    public void deleteUser() {
        User u = createTestUser();
        given().contentType(ContentType.JSON)
                .pathParam("id", u.getId())
                .when().delete("/api/users/{id}")
                .then()
                .statusCode(200);
    }

    @Test
    public void deleteUserNotFound() {
        given().contentType(ContentType.JSON)
                .pathParam("id", 0)
                .when().delete("/api/users/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    public void searchUser() {
        Map<String, String> queryParams = Map.of("firstName", "test",
                "lastName", "test",
                "street", "test",
                "houseNumber", "test",
                "grade", "test");
        given().queryParams(queryParams)
                .when().get("/api/users")
                .then()
                .statusCode(200);
    }


}