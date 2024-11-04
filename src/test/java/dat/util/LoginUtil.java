package dat.util;

import dat.security.entities.Role;
import dat.security.entities.User;
import jakarta.persistence.EntityManagerFactory;

import static io.restassured.RestAssured.given;

public class LoginUtil {

    private static void persistTestUsers(EntityManagerFactory emf){

        User user = new User("user", "user123");
        User admin = new User("admin", "admin123");
        Role userRole = new Role("USER");
        Role adminRole = new Role("ADMIN");
        user.addRole(userRole);
        admin.addRole(adminRole);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.getTransaction().commit();
        }

    }

    private static String login(String username, String password) {
        String json = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);

        var token =  given()
                .contentType("application/json")
                .body(json)
                .when()
                .post("http://localhost:9090/api/auth/login")
                .then()
                .extract()
                .response()
                .body()
                .path("token");

        return "Bearer " + token;

    }

    public static void createTestUsers(EntityManagerFactory emf){
        persistTestUsers(emf);
    }
    public static String getAdminToken() {
        return login("admin", "admin123");
    }
    public static String getUserToken() {
        return login("user", "user123");
    }
}