package reservas.peluqueria.reservas.service;

import reservas.peluqueria.reservas.entity.Servicio;

import java.time.LocalDate;
import java.util.List;

public interface ServicioService {

    // CRUD servicios
    Servicio crearServicio(Servicio servicio);
    Servicio actualizarServicio(Integer id, Servicio servicio);
    void eliminarServicio(Integer id);
    List<Servicio> listarServicios();
    Servicio obtenerServicioPorId(Integer id);

    // Reportes básicos de ganancias
    Double calcularGananciasTotales();
    Double calcularGananciasPorFecha(LocalDate fecha);
    Double calcularGananciasPorRango(LocalDate inicio, LocalDate fin);
}
