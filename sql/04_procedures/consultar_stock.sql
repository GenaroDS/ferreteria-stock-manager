DELIMITER //
CREATE PROCEDURE consultar_stock(
    IN p_idProducto INT
)
BEGIN
    SELECT P.nombre, I.cantidad, P.unidadMinima
    FROM Producto P
    JOIN Inventario I ON P.idProducto = I.idProducto
    WHERE P.idProducto = p_idProducto;
END;
//
DELIMITER ;
