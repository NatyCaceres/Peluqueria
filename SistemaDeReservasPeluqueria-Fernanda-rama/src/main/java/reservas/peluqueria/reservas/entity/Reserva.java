package reservas.peluqueria.reservas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
@Entity
@Table(name = "reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReserva;

    private Integer idCliente;
    private Integer idTrabajador;
    private Integer idServicio;

    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    private String estadoReserva; // confirmada, cancelada, completada
    private String tipoReserva;   // web, recepcionista

    @Column(name = "fecha_creacion", updatable = false)
    private java.time.LocalDateTime fechaCreacion = java.time.LocalDateTime.now();
}
