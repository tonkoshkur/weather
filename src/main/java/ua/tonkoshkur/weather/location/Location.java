package ua.tonkoshkur.weather.location;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ua.tonkoshkur.weather.user.User;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "Locations", uniqueConstraints = @UniqueConstraint(columnNames={"user_id", "latitude", "longitude"}))
public class Location {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(id, location.id) && Objects.equals(name, location.name) && Objects.equals(user, location.user) && Objects.equals(latitude, location.latitude) && Objects.equals(longitude, location.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, user, latitude, longitude);
    }
}
