package reservas.peluqueria.reservas.service.impl;

import org.springframework.stereotype.Service;
import reservas.peluqueria.reservas.dto.DatosHistorialReserva;
import reservas.peluqueria.reservas.dto.DatosReserva;
import reservas.peluqueria.reservas.entity.Reserva;
import reservas.peluqueria.reservas.entity.Servicio;
import reservas.peluqueria.reservas.entity.Usuario;
import reservas.peluqueria.reservas.repository.HorariosDisponiblesRepository;
import reservas.peluqueria.reservas.repository.ReservaRepository;
import reservas.peluqueria.reservas.repository.ServicioRepository;
import reservas.peluqueria.reservas.repository.UsuarioRepository;
import reservas.peluqueria.reservas.service.ReservaService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;
    private final HorariosDisponiblesRepository horariosDisponiblesRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository,
                              UsuarioRepository usuarioRepository,
                              ServicioRepository servicioRepository,
                              HorariosDisponiblesRepository horariosDisponiblesRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicioRepository = servicioRepository;
        this.horariosDisponiblesRepository = horariosDisponiblesRepository;
    }

    @Override
    public Reserva crearReserva(DatosReserva datos, Integer idCliente) {

        Usuario cliente = usuarioRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Usuario trabajador = usuarioRepository.findById(datos.idTrabajador())
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));

        Servicio servicio = servicioRepository.findById(datos.idServicio())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        LocalDate fecha = datos.fecha();
        LocalTime horaInicio = datos.horaInicio();
        LocalTime horaFin = datos.horaFin();

        // ✅ 1. Validar disponibilidad
        boolean disponible = !horariosDisponiblesRepository
                .verificarDisponibilidad(trabajador.getIdUsuario(), fecha, horaInicio, horaFin)
                .isEmpty();

        if (!disponible) {
            throw new RuntimeException("El trabajador no tiene disponibilidad en ese horario.");
        }

        // ✅ 2. Validar solapamiento
        boolean ocupado = !reservaRepository
                .verificarSolapamiento(trabajador.getIdUsuario(), fecha, horaInicio, horaFin)
                .isEmpty();

        if (ocupado) {
            throw new RuntimeException("El trabajador ya tiene una reserva en ese horario.");
        }

        // ✅ 3. Crear reserva
        Reserva reserva = new Reserva();
        reserva.setIdCliente(cliente.getIdUsuario());
        reserva.setIdTrabajador(trabajador.getIdUsuario());
        reserva.setIdServicio(servicio.getIdServicio());
        reserva.setFecha(fecha);
        reserva.setHoraInicio(horaInicio);
        reserva.setHoraFin(horaFin);
        reserva.setEstadoReserva("PENDIENTE");
        reserva.setTipoReserva(datos.tipoReserva() != null ? datos.tipoReserva() : "web");

        return reservaRepository.save(reserva);
    }

    @Override
    public List<Reserva> listarReservasCliente(Integer idCliente) {
        return reservaRepository.findByIdCliente(idCliente);
    }

    @Override
    public List<Reserva> listarReservasTrabajador(Integer idTrabajador) {
        return reservaRepository.findByIdTrabajador(idTrabajador);
    }

    @Override
    public Reserva modificarReserva(Integer idReserva, Integer idCliente, DatosReserva datos) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // 🔐 Validar que la reserva pertenezca al cliente
        if (!reserva.getIdCliente().equals(idCliente)) {
            throw new RuntimeException("No puedes modificar una reserva que no te pertenece");
        }

        // 🔐 Validar que la reserva no esté cancelada o completada
        if (reserva.getEstadoReserva().equalsIgnoreCase("cancelada") ||
                reserva.getEstadoReserva().equalsIgnoreCase("completada")) {
            throw new RuntimeException("No se puede modificar una reserva cancelada o completada");
        }

        // 🕒 Nuevos valores
        LocalDate nuevaFecha = datos.fecha();
        LocalTime nuevaHoraInicio = datos.horaInicio();
        LocalTime nuevaHoraFin = datos.horaFin();

        // 🧩 Verificar disponibilidad del trabajador en la nueva fecha/hora
        boolean disponible = !horariosDisponiblesRepository
                .verificarDisponibilidad(reserva.getIdTrabajador(), nuevaFecha, nuevaHoraInicio, nuevaHoraFin)
                .isEmpty();

        if (!disponible) {
            throw new RuntimeException("El trabajador no tiene disponibilidad en el nuevo horario");
        }

        // 🚫 Verificar que no haya solapamiento con otras reservas (excluyendo la actual)
        boolean ocupado = !reservaRepository
                .verificarSolapamientoExcluyendoReserva(
                        reserva.getIdTrabajador(),
                        nuevaFecha,
                        nuevaHoraInicio,
                        nuevaHoraFin,
                        reserva.getIdReserva()
                ).isEmpty();

        if (ocupado) {
            throw new RuntimeException("El trabajador ya tiene otra reserva en ese horario");
        }

        // ✅ Actualizar la reserva
        reserva.setFecha(nuevaFecha);
        reserva.setHoraInicio(nuevaHoraInicio);
        reserva.setHoraFin(nuevaHoraFin);
        reserva.setEstadoReserva("MODIFICADA");

        return reservaRepository.save(reserva);
    }


    @Override
    public void cancelarReserva(Integer idReserva, Integer idCliente) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // 🔐 Verificar que la reserva pertenezca al cliente
        if (!reserva.getIdCliente().equals(idCliente)) {
            throw new RuntimeException("No puedes cancelar una reserva que no te pertenece");
        }

        // 🚫 Verificar que no esté ya cancelada o completada
        if (reserva.getEstadoReserva().equalsIgnoreCase("cancelada") ||
                reserva.getEstadoReserva().equalsIgnoreCase("completada")) {
            throw new RuntimeException("No se puede cancelar una reserva completada o ya cancelada");
        }

        // ✅ Actualizar estado
        reserva.setEstadoReserva("CANCELADA");
        reservaRepository.save(reserva);
    }


    @Override
    public Reserva completarReserva(Integer idReserva, Integer idTrabajador) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (!reserva.getIdTrabajador().equals(idTrabajador)) {
            throw new RuntimeException("No puedes completar una reserva que no te pertenece");
        }

        if (reserva.getEstadoReserva().equalsIgnoreCase("cancelada")) {
            throw new RuntimeException("No se puede completar una reserva cancelada");
        }

        if (reserva.getEstadoReserva().equalsIgnoreCase("completada")) {
            throw new RuntimeException("La reserva ya fue completada");
        }

        reserva.setEstadoReserva("COMPLETADA");
        return reservaRepository.save(reserva);
    }
    @Override
    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }

    @Override
    public Reserva cambiarEstadoReserva(Integer idReserva, String nuevoEstado) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        reserva.setEstadoReserva(nuevoEstado);
        return reservaRepository.save(reserva);
    }

    @Override
    public void cancelarReservaAdmin(Integer idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (reserva.getEstadoReserva().equalsIgnoreCase("COMPLETADA")) {
            throw new RuntimeException("No se puede cancelar una reserva ya completada.");
        }

        reserva.setEstadoReserva("CANCELADA");
        reservaRepository.save(reserva);
    }
    @Override
    public Reserva marcarComoCompletada(Integer idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Si ya está cancelada, no permitir completar
        if ("CANCELADA".equalsIgnoreCase(reserva.getEstadoReserva())) {
            throw new RuntimeException("No se puede completar una reserva cancelada.");
        }

        if ("COMPLETADA".equalsIgnoreCase(reserva.getEstadoReserva())) {
            // Ya está completada: devolvemos la misma o lanzamos excepción según tu preferencia
            return reserva;
            // o: throw new RuntimeException("La reserva ya fue completada");
        }

        reserva.setEstadoReserva("COMPLETADA");
        return reservaRepository.save(reserva);
    }
    @Override
    public void cancelarReservaPorTrabajador(Integer idReserva, Integer idTrabajador) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (!reserva.getIdTrabajador().equals(idTrabajador)) {
            throw new RuntimeException("No puedes cancelar una reserva que no te pertenece");
        }

        if (reserva.getEstadoReserva().equalsIgnoreCase("cancelada") ||
                reserva.getEstadoReserva().equalsIgnoreCase("completada")) {
            throw new RuntimeException("No se puede cancelar una reserva ya completada o cancelada");
        }

        reserva.setEstadoReserva("RECHAZADA");
        reservaRepository.save(reserva);
    }
    @Override
    public List<Reserva> listarReservasPorTrabajadorYFecha(Integer idTrabajador, LocalDate fecha) {
        return reservaRepository.findByTrabajadorAndFecha(idTrabajador, fecha);
    }
    @Override
    public List<Reserva> listarPorFecha(LocalDate fecha) {
        return reservaRepository.findByFecha(fecha);
    }

    @Override
    public List<Reserva> listarPorTrabajadorYFecha(Integer idTrabajador, LocalDate fecha) {
        return reservaRepository.findByTrabajadorAndFecha(idTrabajador, fecha);
    }

    @Override
    public List<DatosHistorialReserva> listarHistorialCliente(Integer idCliente) {
        List<Reserva> reservas = reservaRepository.findByIdCliente(idCliente);
        List<DatosHistorialReserva> resultado = new ArrayList<>();

        for (Reserva r : reservas) {
            // Buscar el nombre del servicio
            String nombreServicio = servicioRepository.findById(r.getIdServicio())
                    .map(Servicio::getNombreServicio)
                    .orElse("Desconocido");

            // Buscar el nombre del trabajador
            String nombreTrabajador = usuarioRepository.findById(r.getIdTrabajador())
                    .map(u -> u.getNombre() + " " + u.getApellido())
                    .orElse("Desconocido");

            // Agregar al resultado
            resultado.add(new DatosHistorialReserva(
                    r.getIdReserva(),
                    nombreServicio,
                    nombreTrabajador,
                    r.getFecha(),
                    r.getHoraInicio(),
                    r.getHoraFin()
            ));
        }
        return resultado;
    }

}