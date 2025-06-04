-- Productos
INSERT INTO Producto (nombre, unidadMinima, factorConversion)
VALUES 
('Clavos', 'unidad', 10000),        -- 1 kg = 10000 unidades
('Pintura', 'litro', 1),            -- Sin conversión
('Cables', 'metro', 1),             -- Sin conversión
('Tornillos', 'unidad', 5000);      -- 1 kg = 5000 unidades

-- Inventario
INSERT INTO Inventario (idProducto, cantidad)
VALUES 
(1, 20000),   -- Clavos: 20000 unidades
(2, 50),      -- Pintura: 50 litros
(3, 300),     -- Cables: 300 metros
(4, 10000);   -- Tornillos: 10000 unidades
