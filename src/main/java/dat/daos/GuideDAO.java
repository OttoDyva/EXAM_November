package dat.daos;

import dat.dtos.GuideDTO;
import dat.entities.Guide;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.stream.Collectors;

public class GuideDAO implements IDAO<GuideDTO> {

    private static EntityManagerFactory emf;

    private static GuideDAO instance;

    public GuideDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static GuideDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new GuideDAO(emf);
        }
        return instance;
    }
    @Override
    public GuideDTO create(GuideDTO guideDTO) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Guide guide = new Guide();
            guide.setFirstName(guideDTO.getFirstName());
            guide.setLastName(guideDTO.getLastName());
            guide.setEmail(guideDTO.getEmail());
            guide.setPhone(guideDTO.getPhone());

            em.persist(guide);
            em.getTransaction().commit();

            return new GuideDTO(guide);
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
    public GuideDTO getById(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, integer);
            if (guide == null) {
                throw new EntityNotFoundException("No guide found with that ID" + integer);
            }
            return new GuideDTO(guide);
        }
    }

    @Override
    public List<GuideDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Guide> query = em.createQuery("FROM Guide", Guide.class);
            List<Guide> guides = query.getResultList();

            return guides.stream()
                    .map(GuideDTO::new)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public GuideDTO update(Integer integer, GuideDTO guideDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            Guide found = em.find(Guide.class, integer);
            if (found == null) {
                throw new EntityNotFoundException("No guide found with that ID" + integer);
            }

            em.getTransaction().begin();
            if (guideDTO.getFirstName() != null) {
                found.setFirstName(guideDTO.getFirstName());
            }
            if (guideDTO.getLastName() != null) {
                found.setLastName(guideDTO.getLastName());
            }
            if (guideDTO.getEmail() != null) {
                found.setEmail(guideDTO.getEmail());
            }
            if (guideDTO.getPhone() > 0) {
                found.setPhone(guideDTO.getPhone());
            }

            em.getTransaction().commit();

            return new GuideDTO(found);
        }
    }

    @Override
    public void delete(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Guide guide = em.find(Guide.class, integer);
            if (guide == null) {
                throw new EntityNotFoundException("No guide found with ID: " + integer);
            }
            em.remove(guide);
            em.getTransaction().commit();
        }
    }
}
