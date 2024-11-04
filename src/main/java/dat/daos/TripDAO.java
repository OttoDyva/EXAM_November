package dat.daos;

import dat.dtos.GuideDTO;
import dat.dtos.TripDTO;
import dat.entities.Guide;
import dat.entities.Trip;
import dat.enums.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TripDAO implements IDAO<TripDTO>, ITripGuideDAO{

    private static EntityManagerFactory emf;

    private static TripDAO instance;

    public TripDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static TripDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new TripDAO(emf);
        }
        return instance;
    }



    @Override
    public TripDTO create(TripDTO tripDTO) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Trip trip = new Trip();
            trip.setName(tripDTO.getName());
            trip.setStartTime(tripDTO.getStartTime());
            trip.setEndTime(tripDTO.getEndTime());
            trip.setPrice(tripDTO.getPrice());
            trip.setCategory(tripDTO.getCategory());
            trip.setStartPosition(tripDTO.getStartPosition());

            em.persist(trip);
            em.getTransaction().commit();

            return new TripDTO(trip);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public TripDTO getById(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            Trip trip = em.find(Trip.class, integer);
            if (trip == null) {
                throw new EntityNotFoundException("No trip found with that ID" + integer);
            }
            return new TripDTO(trip);
        }
    }

    @Override
    public List<TripDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Trip> query = em.createQuery("FROM Trip", Trip.class);
            List<Trip> trips = query.getResultList();

            return trips.stream()
                    .map(TripDTO::new)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public TripDTO update(Integer integer, TripDTO tripDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            Trip found = em.find(Trip.class, integer);
            if (found == null) {
                throw new EntityNotFoundException("No trip found with that ID" + integer);
            }

            em.getTransaction().begin();
            if (tripDTO.getName() != null) {
                found.setName(tripDTO.getName());
            }
            if (tripDTO.getStartTime() != null) {
                found.setStartTime(tripDTO.getStartTime());
            }
            if (tripDTO.getEndTime() != null) {
                found.setEndTime(tripDTO.getEndTime());
            }
            if (tripDTO.getPrice() > 0) {
                found.setPrice(tripDTO.getPrice());
            }
            if (tripDTO.getCategory() != null) {
                found.setCategory(tripDTO.getCategory());
            }
            if (tripDTO.getStartPosition() != null) {
                found.setStartPosition(tripDTO.getStartPosition());
            }

            em.getTransaction().commit();

            return new TripDTO(found);
        }
    }

    @Override
    public void delete(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = em.find(Trip.class, integer);
            if (trip == null) {
                throw new EntityNotFoundException("No trip found with ID: " + integer);
            }
            em.remove(trip);
            em.getTransaction().commit();
        }
    }

    @Override
    public void addGuideToTrip(int tripId, int guideId) {
        EntityManager em = emf.createEntityManager();
        Trip trip = em.find(Trip.class, tripId);
        Guide guide = em.find(Guide.class, guideId);

        if (trip == null) {
            throw new EntityNotFoundException("Trip with ID " + tripId + " not found.");
        }
        if (guide == null) {
            throw new EntityNotFoundException("Guide with ID " + guideId + " not found.");
        }

        if(!trip.getGuides().contains(guide)){
            trip.getGuides().add(guide);
        }

        em.getTransaction().begin();
        em.merge(trip);
        em.merge(guide);
        em.getTransaction().commit();

        em.close();
    }

    @Override
    public Set getTripsByGuide(int guideId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Guide> query = em.createQuery("SELECT g FROM Guide g WHERE g.trip = :trip", Guide.class);
            query.setParameter("trip", guideId);

            return query.getResultList().stream()
                    .map(GuideDTO::new)
                    .collect(Collectors.toSet());
        }
    }

    public Set getTripsByCategory(Category category) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Trip> query = em.createQuery("SELECT t FROM Trip t WHERE t.category = :category", Trip.class);
            query.setParameter("category", category);

            return query.getResultList().stream()
                    .map(TripDTO::new)
                    .collect(Collectors.toSet());
        }
    }

    public Set getGuideWithTotalSumOfTheirTrips() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Guide> query = em.createQuery("SELECT g FROM Guide g WHERE g.totalPrice = :totalPrice", Guide.class);
            query.setParameter("totalPrice", 0);

            return query.getResultList().stream()
                    .map(GuideDTO::new)
                    .collect(Collectors.toSet());
        }
    }
}
