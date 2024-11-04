package dat.populators;

import dat.config.HibernateConfig;
import dat.entities.Guide;
import dat.entities.Trip;
import dat.enums.Category;
import jakarta.persistence.EntityManagerFactory;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class Populate {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("trips");
        populate(emf);
    }

    public static void populate(EntityManagerFactory emf) {
        Set<Guide> guidesForTrip1 = getGuidesForTrip1();
        Set<Guide> guidesForTrip2 = getGuidesForTrip2();


        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Trip trip1 = new Trip();
            trip1.setName("Trip to Hareskoven");
            trip1.setStartTime(LocalDateTime.of(2023, 5, 14, 10, 0));
            trip1.setEndTime(LocalDateTime.of(2023, 5, 14, 16, 0));
            trip1.setStartPosition("Hareskoven");
            trip1.setPrice(1000);
            trip1.setCategory(Category.FOREST);

            Trip trip2 = new Trip();
            trip2.setName("Trip to Sverige");
            trip2.setStartTime(LocalDateTime.of(2023, 10, 7, 7, 0));
            trip2.setEndTime(LocalDateTime.of(2023, 10, 8, 13, 0));
            trip2.setStartPosition("Hellerup Station");
            trip2.setPrice(5000);
            trip2.setCategory(Category.SNOW);

            guidesForTrip1.forEach(appointment -> appointment.setTrip(trip1));
            guidesForTrip2.forEach(appointment -> appointment.setTrip(trip2));

            em.persist(trip1);
            em.persist(trip2);

            em.getTransaction().commit();
        }
    }

    @NotNull
    private static Set<Guide> getGuidesForTrip1() {
        Guide guide1 = new Guide();
        guide1.setFirstName("Mikkel");
        guide1.setLastName("Hansen");
        guide1.setEmail("mikkelhansen@gmail.com");
        guide1.setPhone(12345678);
        guide1.setYearsOfExperience(5);

        Guide guide2 = new Guide();
        guide2.setFirstName("Viktor");
        guide2.setLastName("Axelsen");
        guide2.setEmail("viktor@gmail.com");
        guide2.setPhone(87654321);
        guide2.setYearsOfExperience(3);

        return Set.of(guide1, guide2);
    }

    @NotNull
    private static Set<Guide> getGuidesForTrip2() {
        Guide guide1 = new Guide();
        guide1.setFirstName("David");
        guide1.setLastName("Møller");
        guide1.setEmail("møller@gmail.com");
        guide1.setPhone(10101010);
        guide1.setYearsOfExperience(2);

        Guide guide2 = new Guide();
        guide2.setFirstName("Mads");
        guide2.setLastName("Kolding");
        guide2.setEmail("madskolding@gmail.com");
        guide2.setPhone(20202020);
        guide2.setYearsOfExperience(20);

        return Set.of(guide1, guide2);
    }
}
