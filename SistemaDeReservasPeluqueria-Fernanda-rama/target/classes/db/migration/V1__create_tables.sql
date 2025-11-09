-- Tabla de Roles
CREATE TABLE roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE -- cliente, trabajador, recepcionista, admin
);

-- Tabla de Usuarios
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo_electronico VARCHAR(255) NOT NULL UNIQUE,
    contrasena_hash VARCHAR(255) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_rol INT NOT NULL,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES roles(id_rol) ON DELETE RESTRICT
);

-- Tabla de Servicios
CREATE TABLE servicios (
    id_servicio INT AUTO_INCREMENT PRIMARY KEY,
    nombre_servicio VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL,
    duracion_estimada_minutos INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

-- Tabla de Horarios Disponibles
CREATE TABLE horarios_disponibles (
    id_disponibilidad INT AUTO_INCREMENT PRIMARY KEY,
    id_trabajador INT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    CONSTRAINT fk_disponibilidad_trabajador FOREIGN KEY (id_trabajador) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- Relación muchos a muchos trabajadores <-> servicios
CREATE TABLE trabajadores_servicios (
    id_trabajador_servicio INT AUTO_INCREMENT PRIMARY KEY,
    id_trabajador INT NOT NULL,
    id_servicio INT NOT NULL,
    CONSTRAINT fk_ts_trabajador FOREIGN KEY (id_trabajador) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_ts_servicio FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio) ON DELETE CASCADE,
    UNIQUE (id_trabajador, id_servicio)
);

-- Tabla de Reservas
CREATE TABLE reservas (
    id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_trabajador INT NOT NULL,
    id_servicio INT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado_reserva VARCHAR(50) NOT NULL, -- confirmada, cancelada, completada
    tipo_reserva VARCHAR(50) NOT NULL, -- web, recepcionista
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reserva_cliente FOREIGN KEY (id_cliente) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_reserva_trabajador FOREIGN KEY (id_trabajador) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_reserva_servicio FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio) ON DELETE CASCADE
);

-- Historial de Cliente
CREATE TABLE historial_cliente (
    id_historial INT AUTO_INCREMENT PRIMARY KEY,
    id_reserva INT NOT NULL,
    fecha_completado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    observaciones TEXT,
    CONSTRAINT fk_historial_reserva FOREIGN KEY (id_reserva) REFERENCES reservas(id_reserva) ON DELETE CASCADE
);
