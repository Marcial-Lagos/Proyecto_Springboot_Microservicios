package cl.duoc.msauth.repository;

import cl.duoc.msauth.model.AuthUser;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AuthUserRepositoryTest {

    @Autowired
    private AuthUserRepository repository;

    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Test
    void findByEmailYExistsByEmailDebenEncontrarUsuarioPersistido() {
        String email = "auth" + faker.number().digits(6) + "@test.cl";
        AuthUser usuario = AuthUser.builder()
                .email(email)
                .password("hash-de-prueba")
                .rol(AuthUser.Rol.CLIENTE)
                .build();
        repository.save(usuario);

        assertThat(repository.findByEmail(email)).isPresent();
        assertThat(repository.existsByEmail(email)).isTrue();
        assertThat(repository.existsByEmail("no-existe@test.cl")).isFalse();
    }
}