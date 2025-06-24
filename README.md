# Ferretería Stock Manager

Este proyecto es una aplicación de escritorio desarrollada en Java utilizando JavaFX, destinada a la gestión de productos e inventario en una ferretería.

## Funcionalidades principales

- Alta de productos (unidad mínima, conversión y empaquetado).
- Ingreso de stock por unidad o paquete.
- Armado y desaramdo de paquetes
- Registro de ventas con descuento automático de stock en unidad mínima.
- Visualización de stock de productos.

## Estructura del proyecto

- **Modelo**: contiene clases como `Producto`, `UnidadDeConversion` e `Inventario`.
- **Controladores**: lógica de interacción para cada pantalla de JavaFX.
- **Servicios**: lógica de negocio (ej. `InventarioService`, `ProductoService`).
- **AppData**: estructura temporal en memoria que simula persistencia.

## Requisitos

- JDK 17 o superior.
- JavaFX SDK.
- IDE recomendado: IntelliJ IDEA o NetBeans.

## Cómo ejecutar

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/GenaroDS/ferreteria-stock-manager/
Abrir el proyecto en el IDE.

Configurar JavaFX en el módulo (si el IDE no lo hace automáticamente).

Ejecutar la clase Main.java.

---