package reservas.peluqueria.reservas.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "servicios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idServicio;

    @Column(unique = true, nullable = false)
    private String nombreServicio;

    private String descripcion;

    private Double precio;

    private Integer duracionEstimadaMinutos;

    private Boolean activo = true;
}
