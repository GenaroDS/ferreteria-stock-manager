DELIMITER //
CREATE PROCEDURE registrar_ingreso_producto(
    IN p_idProducto INT,
    IN p_cantidad FLOAT,
    IN p_unidad VARCHAR(20),
    IN p_fecha DATE
)
BEGIN
    INSERT INTO IngresoProducto (idProducto, cantidad, unidad, fecha)
    VALUES (p_idProducto, p_cantidad, p_unidad, p_fecha);

    DECLARE v_factor FLOAT;
    SELECT factorConversion INTO v_factor FROM Producto WHERE idProducto = p_idProducto;

    UPDATE Inventario
    SET cantidad = cantidad + (p_cantidad * v_factor)
    WHERE idProducto = p_idProducto;
END;
//
DELIMITER ;
