package cl.duoc.msauth.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auth_users")
@Data
@NoArgsConstructor

@AllArgsConstructor
@Builder
public class AuthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
@Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Rol rol = Rol.CLIENTE;

    public enum Rol {
        CLIENTE, ADMIN, REPARTIDOR
    }
}
