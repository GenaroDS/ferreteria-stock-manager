DELIMITER //
CREATE PROCEDURE registrar_venta_producto(
    IN p_idProducto INT,
    IN p_cantidad FLOAT,
    IN p_unidad VARCHAR(20),
    IN p_fecha DATE
)
BEGIN
    INSERT INTO Venta (idProducto, cantidad, unidad, fecha)
    VALUES (p_idProducto, p_cantidad, p_unidad, p_fecha);

    DECLARE v_factor FLOAT;
    SELECT factorConversion INTO v_factor FROM Producto WHERE idProducto = p_idProducto;

    UPDATE Inventario
    SET cantidad = cantidad - (p_cantidad * v_factor)
    WHERE idProducto = p_idProducto;

    DECLARE v_cantidad_actual FLOAT;
    DECLARE v_cantidad_minima FLOAT;

    SELECT cantidad INTO v_cantidad_actual FROM Inventario WHERE idProducto = p_idProducto;
    SELECT cantidadMinima INTO v_cantidad_minima FROM Alerta WHERE idProducto = p_idProducto;

    IF v_cantidad_actual <= v_cantidad_minima THEN
        INSERT INTO Alerta (idProducto, cantidadMinima, fechaAlerta)
        VALUES (p_idProducto, v_cantidad_minima, CURDATE());
    END IF;
END;
//
DELIMITER ;
