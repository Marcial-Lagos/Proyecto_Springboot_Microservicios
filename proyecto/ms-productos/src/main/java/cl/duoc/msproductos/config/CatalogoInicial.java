package cl.duoc.msproductos.config;

import cl.duoc.msproductos.model.Producto;
import cl.duoc.msproductos.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
import java.util.List;

@Configuration
@ConditionalOnProperty(name = "nexus.catalogo.inicial", havingValue = "true")
public class CatalogoInicial {
    @Bean
    CommandLineRunner cargarCatalogo(ProductoRepository productos) {
        return args -> {
            if (productos.count() != 0) return;
            productos.saveAll(List.of(
                    producto("Teclado Gamer", "Teclado mecánico Nexus KX-400 con iluminación RGB.", 49990, "/img/teclado.jpg"),
                    producto("Mouse Gamer", "Mouse óptico para juegos de precisión.", 59990, null),
                    producto("Audífonos Gamer", "Audífonos con micrófono para jugar en equipo.", 69990, null)));
        };
    }

    private Producto producto(String nombre, String descripcion, int precio, String imagen) {
        return Producto.builder().nombre(nombre).descripcion(descripcion).precio(BigDecimal.valueOf(precio))
                .imagenUrl(imagen).categoria("Gaming").stock(10).build();
    }
}
