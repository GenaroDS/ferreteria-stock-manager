-- Vista de stock actual
CREATE VIEW vista_stock AS
SELECT 
    P.nombre, 
    I.cantidad AS stock_en_unidad_minima, 
    P.unidadMinima
FROM 
    Producto P
JOIN 
    Inventario I ON P.idProducto = I.idProducto;

-- Consulta de alertas activas
SELECT 
    P.nombre, 
    A.cantidadMinima, 
    A.fechaAlerta
FROM 
    Producto P 
JOIN 
    Alerta A ON P.idProducto = A.idProducto;
