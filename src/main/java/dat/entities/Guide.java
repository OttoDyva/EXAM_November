package dat.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="guide")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Guide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id", nullable = false)
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private int phone;

    @Column(name = "years_of_experience", nullable = false)
    private int yearsOfExperience;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    /*
    @OneToMany(mappedBy = "guide", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Trip> trips = new HashSet<>();

     */

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Trip trip;

}
