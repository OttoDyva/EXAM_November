package dat.entities;

import dat.enums.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="trip")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id", nullable = false)
    private Integer id;

    @Column(name = "trip_name", nullable = false)
    private String name;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "start_position", nullable = false)
    private String startPosition;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "category", nullable = false)
    private Category category;

    /*
    @ManyToOne
    @JoinColumn(name = "guide_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Guide guide;
     */

    @OneToMany(mappedBy = "trip", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Guide> guides = new HashSet<>();
}
