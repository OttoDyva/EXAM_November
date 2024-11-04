package dat;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.populators.Populate;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("trips");
        Populate.populate(emf);
        ApplicationConfig.startServer(9090);
    }
}