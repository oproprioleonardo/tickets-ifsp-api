package br.com.ifsp.tickets.infra.user.recovery;

import br.com.ifsp.tickets.domain.user.recovery.PasswordRecovery;
import br.com.ifsp.tickets.domain.user.recovery.PasswordRecoveryID;
import br.com.ifsp.tickets.infra.user.persistence.UserJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_recovery")
@NoArgsConstructor
@Getter
public class PassRecoveryJpaEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;
    @Column(name = "token", nullable = false, unique = true, updatable = false)
    private String token;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "expires_at", nullable = false, updatable = false)
    private LocalDateTime expiresAt;
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    @Column(name = "used", nullable = false)
    private boolean used;

    public PassRecoveryJpaEntity(UUID id, UserJpaEntity user, String token, LocalDateTime createdAt, LocalDateTime expiresAt, LocalDateTime usedAt, boolean used) {
        this.id = id;
        this.user = user;
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.usedAt = usedAt;
        this.used = used;
    }

    public static PassRecoveryJpaEntity from(PasswordRecovery passRecovery) {
        return new PassRecoveryJpaEntity(
                passRecovery.getId().getValue(),
                UserJpaEntity.from(passRecovery.getUser()),
                passRecovery.getToken(),
                passRecovery.getCreatedAt(),
                passRecovery.getExpiresAt(),
                passRecovery.getUsedAt(),
                passRecovery.isUsed()
        );
    }

    public PasswordRecovery toAggregate() {
        return new PasswordRecovery(
                new PasswordRecoveryID(this.getId()),
                this.getUser().toAggregate(),
                this.getToken(),
                this.getCreatedAt(),
                this.getUsedAt(),
                this.getExpiresAt(),
                this.isUsed()
        );
    }

}