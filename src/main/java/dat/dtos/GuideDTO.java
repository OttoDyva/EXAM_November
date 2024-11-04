package dat.dtos;

import dat.entities.Guide;
import dat.entities.Trip;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private int phone;
    private int yearsOfExperience;
    private Integer tripId;
    private int totalPrice;
    public GuideDTO(Guide guide) {
        this.id = guide.getId();
        this.firstName = guide.getFirstName();
        this.lastName = guide.getLastName();
        this.email = guide.getEmail();
        this.phone = guide.getPhone();
        this.yearsOfExperience = guide.getYearsOfExperience();
        this.tripId = guide.getTrip().getId();
        this.totalPrice = guide.getTrip().getPrice();

    }
}
