package reservas.peluqueria.reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import reservas.peluqueria.reservas.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreoElectronico(String correoElectronico);
    @Query("SELECT u FROM Usuario u WHERE u.rol.idRol = :idRol")
    List<Usuario> findByIdRol(@Param("idRol") Integer idRol);

}
