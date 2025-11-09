package reservas.peluqueria.reservas.service;

import reservas.peluqueria.reservas.dto.DatosReserva;
import reservas.peluqueria.reservas.entity.Reserva;

import java.util.List;

public interface ReservaService {
    Reserva crearReserva(DatosReserva datos, Integer idCliente);
    List<Reserva> listarReservasCliente(Integer idCliente);
    List<Reserva> listarReservasTrabajador(Integer idTrabajador);
}