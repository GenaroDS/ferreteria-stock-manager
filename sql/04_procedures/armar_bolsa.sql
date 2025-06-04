DELIMITER //
CREATE PROCEDURE armar_bolsa(
    IN p_idProducto INT,
    IN p_cantidad_unidades FLOAT,
    IN p_unidadBolsa VARCHAR(20)
)
BEGIN
    UPDATE Inventario
    SET cantidad = cantidad - p_cantidad_unidades
    WHERE idProducto = p_idProducto;

    INSERT INTO Inventario (idProducto, cantidad, unidadMedida)
    VALUES (p_idProducto, 1, p_unidadBolsa);
END;
//
DELIMITER ;
