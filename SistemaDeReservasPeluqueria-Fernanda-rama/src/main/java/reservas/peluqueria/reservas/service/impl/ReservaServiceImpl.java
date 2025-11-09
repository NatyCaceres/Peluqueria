package reservas.peluqueria.reservas.service.impl;

import org.springframework.stereotype.Service;
import reservas.peluqueria.reservas.dto.DatosReserva;
import reservas.peluqueria.reservas.entity.Reserva;
import reservas.peluqueria.reservas.entity.Servicio;
import reservas.peluqueria.reservas.entity.Usuario;
import reservas.peluqueria.reservas.repository.ReservaRepository;
import reservas.peluqueria.reservas.repository.ServicioRepository;
import reservas.peluqueria.reservas.repository.UsuarioRepository;
import reservas.peluqueria.reservas.service.ReservaService;

import java.time.LocalTime;
import java.util.List;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository,
                              UsuarioRepository usuarioRepository,
                              ServicioRepository servicioRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicioRepository = servicioRepository;
    }

    @Override
    public Reserva crearReserva(DatosReserva datos, Integer idCliente) {
        Usuario cliente = usuarioRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Usuario trabajador = usuarioRepository.findById(datos.idTrabajador())
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));

        Servicio servicio = servicioRepository.findById(datos.idServicio())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        // Calcular hora fin según duración del servicio
        LocalTime horaFin = datos.horaInicio().plusMinutes(servicio.getDuracionEstimadaMinutos().longValue());

        Reserva reserva = new Reserva();
        reserva.setIdCliente(cliente.getIdUsuario());
        reserva.setIdTrabajador(trabajador.getIdUsuario());
        reserva.setIdServicio(servicio.getIdServicio());
        reserva.setFecha(datos.fecha());
        reserva.setHoraInicio(datos.horaInicio());
        reserva.setHoraFin(horaFin);
        reserva.setEstadoReserva("confirmada");
        reserva.setTipoReserva("web");

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
}