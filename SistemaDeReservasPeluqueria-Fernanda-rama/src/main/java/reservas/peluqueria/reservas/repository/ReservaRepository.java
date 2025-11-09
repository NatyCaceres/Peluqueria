package reservas.peluqueria.reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import reservas.peluqueria.reservas.entity.Reserva;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    List<Reserva> findByIdCliente(Integer idCliente);
    List<Reserva> findByIdTrabajador(Integer idTrabajador);
}
