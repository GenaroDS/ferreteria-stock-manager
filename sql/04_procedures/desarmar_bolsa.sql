DELIMITER //
CREATE PROCEDURE desarmar_bolsa(
    IN p_idProducto INT,
    IN p_cantidad_unidades FLOAT,
    IN p_unidadBolsa VARCHAR(20)
)
BEGIN
    DELETE FROM Inventario
    WHERE idProducto = p_idProducto AND unidadMedida = p_unidadBolsa
    LIMIT 1;

    UPDATE Inventario
    SET cantidad = cantidad + p_cantidad_unidades
    WHERE idProducto = p_idProducto;
END;
//
DELIMITER ;
