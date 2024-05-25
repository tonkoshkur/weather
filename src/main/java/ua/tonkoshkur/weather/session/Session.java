package ua.tonkoshkur.weather.session;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ua.tonkoshkur.weather.user.User;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "Sessions")
public class Session {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(id, session.id) && Objects.equals(user, session.user) && Objects.equals(expiresAt, session.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, expiresAt);
    }
}
