package dat.dtos;

import dat.entities.Guide;
import dat.entities.Trip;
import dat.enums.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripDTO {
    private Integer id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String startPosition;
    private int price;
    private Category category;
    private Set<GuideDTO> guides;

    public TripDTO(Trip trip) {
        this.id = trip.getId();
        this.name = trip.getName();
        this.startTime = trip.getStartTime();
        this.endTime = trip.getEndTime();
        this.startPosition = trip.getStartPosition();
        this.price = trip.getPrice();
        this.category = trip.getCategory();
        this.guides = trip.getGuides().stream()
                .map(GuideDTO::new)
                .collect(Collectors.toSet());    }
}
