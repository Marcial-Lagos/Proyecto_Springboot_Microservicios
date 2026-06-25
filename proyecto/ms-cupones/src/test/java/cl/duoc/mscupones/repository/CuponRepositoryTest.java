package cl.duoc.mscupones.repository;

import cl.duoc.mscupones.model.Cupon;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CuponRepositoryTest {
    @Autowired CuponRepository repository;

    @Test
    void encuentraCuponPorCodigo() {
        repository.save(Cupon.builder()
                .codigo("PROMO10").tipo(Cupon.TipoDescuento.PORCENTAJE)
                .valor(new BigDecimal("10")).usosMaximos(10).build());
        assertThat(repository.findByCodigo("PROMO10")).isPresent();
        assertThat(repository.existsByCodigo("PROMO10")).isTrue();
    }
}