package reservas.peluqueria.reservas.service.impl;

import org.springframework.stereotype.Service;
import reservas.peluqueria.reservas.entity.Reserva;
import reservas.peluqueria.reservas.entity.Servicio;
import reservas.peluqueria.reservas.repository.ReservaRepository;
import reservas.peluqueria.reservas.repository.ServicioRepository;
import reservas.peluqueria.reservas.service.ServicioService;

import java.time.LocalDate;
import java.util.List;

@Service
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepo;
    private final ReservaRepository reservaRepo;

    public ServicioServiceImpl(ServicioRepository servicioRepo, ReservaRepository reservaRepo) {
        this.servicioRepo = servicioRepo;
        this.reservaRepo = reservaRepo;
    }

    @Override
    public Servicio crearServicio(Servicio servicio) {
        if (servicio.getPrecio() == null) servicio.setPrecio(0.0);
        if (servicio.getActivo() == null) servicio.setActivo(true);
        return servicioRepo.save(servicio);
    }

    @Override
    public Servicio actualizarServicio(Integer id, Servicio servicioNuevo) {
        Servicio servicio = servicioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        servicio.setNombreServicio(servicioNuevo.getNombreServicio());
        servicio.setDescripcion(servicioNuevo.getDescripcion());
        servicio.setPrecio(servicioNuevo.getPrecio());
        servicio.setDuracionEstimadaMinutos(servicioNuevo.getDuracionEstimadaMinutos());
        servicio.setActivo(servicioNuevo.getActivo());
        return servicioRepo.save(servicio);
    }

    @Override
    public void eliminarServicio(Integer id) {
        servicioRepo.deleteById(id);
    }

    @Override
    public List<Servicio> listarServicios() {
        return servicioRepo.findAll();
    }

    @Override
    public Servicio obtenerServicioPorId(Integer id) {
        return servicioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
    }

    // ===========================
    // Reportes básicos de ganancias
    // ===========================
    @Override
    public Double calcularGananciasTotales() {
        List<Reserva> reservas = reservaRepo.findAll();
        return reservas.stream()
                .filter(r -> r.getEstadoReserva().equalsIgnoreCase("COMPLETADA"))
                .mapToDouble(r -> {
                    Servicio s = servicioRepo.findById(r.getIdServicio()).orElse(null);
                    return s != null ? s.getPrecio() : 0.0;
                })
                .sum();
    }

    @Override
    public Double calcularGananciasPorFecha(LocalDate fecha) {
        List<Reserva> reservas = reservaRepo.findByFecha(fecha);
        return reservas.stream()
                .filter(r -> r.getEstadoReserva().equalsIgnoreCase("COMPLETADA"))
                .mapToDouble(r -> {
                    Servicio s = servicioRepo.findById(r.getIdServicio()).orElse(null);
                    return s != null ? s.getPrecio() : 0.0;
                })
                .sum();
    }

    @Override
    public Double calcularGananciasPorRango(LocalDate inicio, LocalDate fin) {
        List<Reserva> reservas = reservaRepo.findByFechaBetween(inicio, fin);
        return reservas.stream()
                .filter(r -> r.getEstadoReserva().equalsIgnoreCase("COMPLETADA"))
                .mapToDouble(r -> {
                    Servicio s = servicioRepo.findById(r.getIdServicio()).orElse(null);
                    return s != null ? s.getPrecio() : 0.0;
                })
                .sum();
    }
}
