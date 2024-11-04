package dat.controllers;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.daos.TripDAO;
import dat.entities.Guide;
import dat.entities.Trip;
import dat.populators.Populate;
import dat.util.LoginUtil;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TripControllerTest {
    private Javalin app;
    private static EntityManagerFactory emfTest = HibernateConfig.getEntityManagerFactoryForTest();
    private int port = 4040;
    private static String adminToken;
    private static String userToken;

    @BeforeAll
    void setUp() {
        app = ApplicationConfig.startServer(port);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        LoginUtil.createTestUsers(emfTest);
        adminToken = LoginUtil.getAdminToken();
        userToken = LoginUtil.getUserToken();
    }

    @BeforeEach
    void setUpEach() {
        Populate.populate(emfTest);
    }

    @AfterAll
    void tearDown() {
        ApplicationConfig.stopServer(app);
    }

    @Test
    void create() {
        given()
                .header("Authorization", adminToken)
                .contentType("application/json")
                .body("{\"name\":\"Trip name\"," +
                        "\"startTime\":\"2023-11-04T10:00:00\"," +
                        "\"endTime\":\"2023-11-04T15:00:00\"," +
                        "\"startPosition\":\"Starting Point\"," +
                        "\"price\":1000," +
                        "\"category\":\"BEACH\"}")
                .when()
                .post("/api/trips/")
                .then()
                .statusCode(201)
                .body("name", equalTo("Trip name"))
                .body("price", equalTo(150))
                .body("startPosition", equalTo("Starting Point"))
                .body("category", equalTo("BEACH"));
    }

    @Test
    void getById() {
        given()
                .header("Authorization", userToken)
                .contentType("application/json")
                .when()
                .get("/api/trips/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    void getAll() {
        given()
                .header("Authorization", userToken)
                .contentType("application/json")
                .when()
                .get("api/trips/")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].id", notNullValue())
                .body("[0].name", notNullValue());
    }

    @Test
    void update() {
        given()
                .header("Authorization", userToken)
                .contentType("application/json")
                .body("{\"name\":\"New trip name\"," +
                        "\"startTime\":\"2023-11-04T10:00:00\"," +
                        "\"endTime\":\"2023-11-04T15:00:00\"," +
                        "\"startPosition\":\" New Starting Point\"," +
                        "\"price\":2000," +
                        "\"category\":\"SNOW\"}")
                .when()
                .put("/api/trips/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("New trip name"))
                .body("price", equalTo(2000))
                .body("startPosition", equalTo("New Starting Point"))
                .body("category", equalTo("SNOW"));
    }

    @Test
    void delete() {
        given()
                .header("Authorization", adminToken)
                .contentType("application/json")
                .when()
                .delete("/api/trips/1")
                .then()
                .statusCode(204);
    }

    @Test
    void addGuideToTrip() {
        given()
                .header("Authorization", adminToken)
                .contentType("application/json")
                .when()
                .put("/api/trips/1/guides/1")
                .then()
                .statusCode(200)
                .body("guides.size()", equalTo(1))
                .body("guides[0].id", equalTo(1));
    }

    @Test
    void getTripsByGuide() {
        given()
                .header("Authorization", userToken)
                .contentType("application/json")
                .when()
                .get("/api/trips/getTripsByGuide/1")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].id", notNullValue())
                .body("[0].name", notNullValue());
    }

    @Test
    void getTripsByCategory() {
        given()
                .header("Authorization", userToken)
                .contentType("application/json")
                .when()
                .get("/api/trips/category/BEACH")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].id", notNullValue())
                .body("[0].name", notNullValue());
    }

    @Test
    void getGuideWithTotalSumOfTheirTrips() {
        given()
                .header("Authorization", userToken)
                .contentType("application/json")
                .when()
                .get("/api/trips/guides/totalprice")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].id", notNullValue())
                .body("[0].name", notNullValue());
    }
}