package reservas.peluqueria.reservas.dto;


public record DatosServicio(
        String nombreServicio,
        String descripcion,
        Double precio,
        int duracionEstimadaMinutos,
        boolean activo
) {}
