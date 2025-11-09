package reservas.peluqueria.reservas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trabajadores_servicios",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_trabajador", "id_servicio"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrabajadorServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTrabajadorServicio;

    @ManyToOne
    @JoinColumn(name = "id_trabajador", nullable = false)
    private Usuario trabajador;

    @ManyToOne
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;
}
