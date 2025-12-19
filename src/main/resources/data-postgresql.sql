-- ===========================================
-- DATOS INICIALES - FACTURAaaS
-- PostgreSQL
-- ===========================================
-- Nota: El usuario admin se crea mediante AdminInitializer.java

-- Tipos de IVA (solo insertar si no existen)
INSERT INTO tipo_iva (descripcion, porcentaje, activo)
SELECT 'IVA General', 21.00, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tipo_iva WHERE descripcion = 'IVA General');

INSERT INTO tipo_iva (descripcion, porcentaje, activo)
SELECT 'IVA Reducido', 10.00, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tipo_iva WHERE descripcion = 'IVA Reducido');

INSERT INTO tipo_iva (descripcion, porcentaje, activo)
SELECT 'IVA Superreducido', 4.00, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tipo_iva WHERE descripcion = 'IVA Superreducido');

INSERT INTO tipo_iva (descripcion, porcentaje, activo)
SELECT 'Sin IVA', 0.00, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tipo_iva WHERE descripcion = 'Sin IVA');
