package reservas.peluqueria.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatosHistorialReserva {
    private Integer idReserva;
    private String nombreServicio;
    private String nombreTrabajador;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
