package cl.duoc.msusuarios.repository;

import cl.duoc.msusuarios.model.Usuario;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba JPA real. @DataJpaTest inicia únicamente entidades y repositorios
 * usando la base H2 configurada para el perfil test.
 */
@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Test
    void findByEmailYExistsByEmailDebenEncontrarUsuarioPersistido() {
        // Given
        String email = "usuario" + faker.number().digits(6) + "@test.cl";
        Usuario usuario = Usuario.builder()
                .nombre(faker.name().fullName())
                .email(email)
                .password("hash-de-prueba")
                .rol(Usuario.Rol.CLIENTE)
                .telefono(faker.phoneNumber().cellPhone())
                .direccion(faker.address().streetAddress())
                .build();
        repository.save(usuario);

        // When + Then
        assertThat(repository.findByEmail(email)).isPresent();
        assertThat(repository.existsByEmail(email)).isTrue();
        assertThat(repository.existsByEmail("no-existe@test.cl")).isFalse();
    }
}