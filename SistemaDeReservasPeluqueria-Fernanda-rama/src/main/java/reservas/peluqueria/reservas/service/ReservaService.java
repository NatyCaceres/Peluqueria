package reservas.peluqueria.reservas.service;

import reservas.peluqueria.reservas.dto.DatosHistorialReserva;
import reservas.peluqueria.reservas.dto.DatosReserva;
import reservas.peluqueria.reservas.entity.Reserva;

import java.time.LocalDate;
import java.util.List;

public interface ReservaService {
    Reserva crearReserva(DatosReserva datos, Integer idCliente);
    List<Reserva> listarReservasCliente(Integer idCliente);
    List<Reserva> listarReservasTrabajador(Integer idTrabajador);
    Reserva modificarReserva(Integer idReserva, Integer idCliente, DatosReserva datos);
    void cancelarReserva(Integer idReserva, Integer idCliente);
    // ADMIN
    List<Reserva> listarTodas();
    Reserva cambiarEstadoReserva(Integer idReserva, String nuevoEstado);
    void cancelarReservaAdmin(Integer idReserva);

    // Completado (admin/manual)
    Reserva marcarComoCompletada(Integer idReserva);
    // Completado por trabajador (si necesitas control adicional)
    Reserva completarReserva(Integer idReserva, Integer idTrabajador);

    void cancelarReservaPorTrabajador(Integer idReserva, Integer idTrabajador);
    List<Reserva> listarReservasPorTrabajadorYFecha(Integer idTrabajador, LocalDate fecha);
    List<Reserva> listarPorFecha(LocalDate fecha);
    List<Reserva> listarPorTrabajadorYFecha(Integer idTrabajador, LocalDate fecha);
    List<DatosHistorialReserva> listarHistorialCliente(Integer idCliente);
}
