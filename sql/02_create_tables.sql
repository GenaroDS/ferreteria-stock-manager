CREATE TABLE Producto (
    idProducto INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100),
    unidadMinima VARCHAR(20),
    factorConversion FLOAT
);

CREATE TABLE Inventario (
    idInventario INT PRIMARY KEY AUTO_INCREMENT,
    idProducto INT,
    cantidad FLOAT,
    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)
);

CREATE TABLE Venta (
    idVenta INT PRIMARY KEY AUTO_INCREMENT,
    idProducto INT,
    cantidad FLOAT,
    unidad VARCHAR(20),
    fecha DATE,
    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)
);

CREATE TABLE IngresoProducto (
    idIngreso INT PRIMARY KEY AUTO_INCREMENT,
    idProducto INT,
    cantidad FLOAT,
    unidad VARCHAR(20),
    fecha DATE,
    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)
);

CREATE TABLE Alerta (
    idAlerta INT PRIMARY KEY AUTO_INCREMENT,
    idProducto INT,
    cantidadMinima FLOAT,
    fechaAlerta DATE,
    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)
);