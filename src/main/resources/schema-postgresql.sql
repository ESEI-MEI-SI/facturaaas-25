-- ===========================================
-- SCHEMA DE BASE DE DATOS - FACTURAaaS
-- PostgreSQL
-- ===========================================

-- Tabla: usuario
CREATE TABLE IF NOT EXISTS usuario (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_ultimo_acceso TIMESTAMP
);

-- Tabla: tipo_iva
CREATE TABLE IF NOT EXISTS tipo_iva (
    id BIGSERIAL PRIMARY KEY,
    descripcion VARCHAR(50) NOT NULL,
    porcentaje DECIMAL(5,2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla: forma_pago
CREATE TABLE IF NOT EXISTS forma_pago (
    id BIGSERIAL PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL,
    numero_pagos INTEGER NOT NULL DEFAULT 1,
    periodicidad_dias INTEGER NOT NULL DEFAULT 30,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_forma_pago_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Tabla: datos_facturacion
CREATE TABLE IF NOT EXISTS datos_facturacion (
    id BIGSERIAL PRIMARY KEY,
    nombre_comercial VARCHAR(150),
    nif VARCHAR(20) NOT NULL,
    domicilio VARCHAR(200),
    localidad VARCHAR(100),
    codigo_postal VARCHAR(10),
    provincia VARCHAR(100),
    telefono VARCHAR(20),
    email_contacto VARCHAR(100),
    cuenta_bancaria VARCHAR(34),
    tipo VARCHAR(30),
    usuario_id BIGINT NOT NULL UNIQUE,
    tipo_iva_defecto_id BIGINT,
    forma_pago_defecto_id BIGINT,
    CONSTRAINT fk_datos_facturacion_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_datos_facturacion_tipo_iva FOREIGN KEY (tipo_iva_defecto_id) REFERENCES tipo_iva(id),
    CONSTRAINT fk_datos_facturacion_forma_pago FOREIGN KEY (forma_pago_defecto_id) REFERENCES forma_pago(id)
);

-- Tabla: cliente
CREATE TABLE IF NOT EXISTS cliente (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    nif VARCHAR(20),
    domicilio VARCHAR(200),
    localidad VARCHAR(100),
    codigo_postal VARCHAR(10),
    provincia VARCHAR(100),
    email VARCHAR(100),
    telefono VARCHAR(20),
    cuenta_bancaria VARCHAR(34),
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_cliente_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Tabla: factura
CREATE TABLE IF NOT EXISTS factura (
    id BIGSERIAL PRIMARY KEY,
    numero_factura VARCHAR(20) NOT NULL,
    ejercicio INTEGER NOT NULL,
    fecha_emision DATE NOT NULL,
    estado VARCHAR(20) NOT NULL,
    comentarios TEXT,
    importe_total DECIMAL(12,2) NOT NULL DEFAULT 0,
    iva_total DECIMAL(12,2) NOT NULL DEFAULT 0,
    suma_total DECIMAL(12,2) NOT NULL DEFAULT 0,
    usuario_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    forma_pago_id BIGINT,
    CONSTRAINT fk_factura_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_factura_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_factura_forma_pago FOREIGN KEY (forma_pago_id) REFERENCES forma_pago(id),
    CONSTRAINT uk_factura_numero UNIQUE (numero_factura, ejercicio)
);

-- Tabla: linea_factura
CREATE TABLE IF NOT EXISTS linea_factura (
    id BIGSERIAL PRIMARY KEY,
    numero_linea INTEGER NOT NULL,
    concepto VARCHAR(255) NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(12,2) NOT NULL,
    porcentaje_descuento DECIMAL(5,2) NOT NULL DEFAULT 0,
    importe_total DECIMAL(12,2) NOT NULL,
    factura_id BIGINT NOT NULL,
    tipo_iva_id BIGINT NOT NULL,
    CONSTRAINT fk_linea_factura_factura FOREIGN KEY (factura_id) REFERENCES factura(id) ON DELETE CASCADE,
    CONSTRAINT fk_linea_factura_tipo_iva FOREIGN KEY (tipo_iva_id) REFERENCES tipo_iva(id)
);

-- Tabla: pago
CREATE TABLE IF NOT EXISTS pago (
    id BIGSERIAL PRIMARY KEY,
    numero_pago INTEGER NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    importe DECIMAL(12,2) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_pago DATE,
    factura_id BIGINT NOT NULL,
    CONSTRAINT fk_pago_factura FOREIGN KEY (factura_id) REFERENCES factura(id) ON DELETE CASCADE
);

-- Índices para mejorar rendimiento
CREATE INDEX IF NOT EXISTS idx_usuario_login ON usuario(login);
CREATE INDEX IF NOT EXISTS idx_cliente_usuario ON cliente(usuario_id);
CREATE INDEX IF NOT EXISTS idx_factura_usuario ON factura(usuario_id);
CREATE INDEX IF NOT EXISTS idx_factura_cliente ON factura(cliente_id);
CREATE INDEX IF NOT EXISTS idx_factura_ejercicio ON factura(ejercicio);
CREATE INDEX IF NOT EXISTS idx_linea_factura_factura ON linea_factura(factura_id);
CREATE INDEX IF NOT EXISTS idx_pago_factura ON pago(factura_id);
CREATE INDEX IF NOT EXISTS idx_pago_estado ON pago(estado);
CREATE INDEX IF NOT EXISTS idx_forma_pago_usuario ON forma_pago(usuario_id);
