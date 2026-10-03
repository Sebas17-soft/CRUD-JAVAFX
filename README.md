# 📦 Sistema CRUD en JavaFX (En Memoria - ArrayList)

Aplicación de escritorio moderna para la **Gestión de Inventario de Productos**, desarrollada en **Java** y **JavaFX** aplicando el patrón arquitectónico **MVC (Modelo - Vista - Controlador)** y principios de diseño **UX/UI**.

El almacenamiento de los datos reside **100% en memoria mediante `ArrayList`**, sin uso de bases de datos, Hibernate, Spring ni mecanismos de persistencia externa.

---

## 🚀 Tecnologías Utilizadas

- **Lenguaje:** Java 21 (compatible con JDK 17, 21 y versiones superiores como JDK 26).
- **Framework Gráfico:** JavaFX 21.0.2 (`javafx-controls`, `javafx-fxml`).
- **Herramienta de Construcción:** Apache Maven 3.9+ (incluye Maven Wrapper `mvnw`).
- **Diseño de Interfaz:** FXML (declarativo, compatible con Scene Builder).
- **Estilos Visuales:** CSS3 de JavaFX (`-fx-*`) con jerarquía visual moderna.
- **Estructura de Datos:** `java.util.ArrayList` para manipulación de registros en memoria RAM.
- **Testing:** JUnit 5 (Jupiter) para validación automatizada de la lógica CRUD.

---

## 🏛️ Arquitectura del Proyecto

El proyecto está estructurado siguiendo una separación clara de responsabilidades:

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── crud/
│   │           └── javafx/
│   │               ├── MainApp.java            # Punto de entrada JavaFX (Application)
│   │               ├── Launcher.java           # Bootstrap para ejecución standalone y fat-jar
│   │               ├── controller/
│   │               │   └── ProductoController.java # Controlador FXML y mediador de eventos
│   │               ├── model/
│   │               │   └── Producto.java           # Entidad con propiedades observables
│   │               └── service/
│   │                   ├── IProductoService.java   # Interfaz de lógica de negocio CRUD
│   │                   └── ProductoService.java    # Implementación con almacenamiento en ArrayList
│   │
│   └── resources/
│       └── com/
│           └── crud/
│               └── javafx/
│                   ├── styles/
│                   │   └── styles.css          # Hoja de estilos moderna y estados visuales
│                   └── view/
│                       └── producto-view.fxml  # Definición visual de la interfaz
│
└── test/
    └── java/
        └── com/
            └── crud/
                └── javafx/
                    └── service/
                        └── ProductoServiceTest.java # Pruebas unitarias completas
```

### Roles de cada Capa:
1. **Modelo (`model/Producto.java`):**
   - Modela la entidad de negocio con atributos: `id`, `nombre`, `precio` y `cantidad`.
   - Implementa propiedades observables de JavaFX (`IntegerProperty`, `StringProperty`, `DoubleProperty`) para permitir **Data Binding** bidireccional y actualización reactiva en la `TableView`.
2. **Servicio (`service/IProductoService.java` y `ProductoService.java`):**
   - Encapsula las operaciones del negocio y el ciclo de vida de los datos.
   - Aloja la colección privada `List<Producto> productos = new ArrayList<>()`.
   - Genera identificadores autoincrementales de forma atómica y provee cálculos de inventario (total stock, valoración global).
3. **Vista (`view/producto-view.fxml` y `styles/styles.css`):**
   - Maquetación estructurada con `BorderPane`, `VBox`, `HBox`, campos de entrada, botones con estados interactivos y tabla de datos `TableView`.
4. **Controlador (`controller/ProductoController.java`):**
   - Vincula la vista FXML con el servicio.
   - Ejecuta validaciones de entrada, maneja eventos de teclado y ratón, controla el banner dinámico de notificaciones y actualiza métricas del dashboard.

---

## ⚡ Funcionalidades CRUD y UX/UI

### 1. Crear (Create)
- Formulario con campos: Nombre, Precio y Cantidad.
- El ID se autogenera automáticamente.
- Validación en tiempo real con resaltado en rojo y mensajes explicativos.
- Al guardar, el producto se añade al `ArrayList`, se refleja de inmediato en la tabla y se actualizan las tarjetas de métricas.

### 2. Leer / Listar (Read)
- Al iniciar la aplicación, la `TableView` carga automáticamente registros preexistentes desde el `ArrayList`.
- Buscador reactivo en tiempo real: filtra instantáneamente por nombre o número de ID.
- Columnas con formato numérico y monetario (`$#,##0.00`).

### 3. Actualizar (Update)
- Al seleccionar una fila en la tabla o hacer doble clic sobre ella, los datos se cargan en el formulario.
- La interfaz conmuta a **"Modo Edición"**: el botón principal cambia a *"Actualizar Cambios"* (color ámbar) y se activa el botón *"Cancelar"*.
- Al guardar, los cambios se aplican sobre la instancia en el `ArrayList` y se refresca la vista.

### 4. Eliminar (Delete)
- Los botones *Editar* y *Eliminar* se habilitan únicamente cuando una fila está seleccionada.
- Al presionar *Eliminar*, se muestra un **diálogo modal de confirmación** para prevenir eliminaciones accidentales.
- Si el usuario confirma, el elemento se remueve del `ArrayList` y de la tabla.

### 5. Experiencia de Usuario (UX/UI)
- **Tarjetas de Estadísticas:** Muestra en tiempo real: *Total de Productos*, *Stock Global* y *Valor Total del Inventario*.
- **Banner de Estado:** Notificaciones visuales diferenciadas por color (Verde para éxito, Rojo para error, Azul para información).
- **Limpieza y Cancelación:** Botón para reiniciar el formulario a modo creación en cualquier instante.

---

## 📋 Requisitos del Sistema

- **Java JDK:** Versión 17, 21 (LTS recomendada) o superior (verificado hasta OpenJDK 26).
- **Maven:** Versión 3.8+ (o utilizar el comando `./mvnw` / `.\mvnw.cmd` provisto en el proyecto).

---

## ⚙️ Instalación y Ejecución

### Opción 1: Ejecutar directamente con Maven (Recomendada)

Abre una terminal en la raíz del proyecto y ejecuta:

```bash
# En Windows (usando Maven instalado):
mvn clean javafx:run

# O usando el Maven Wrapper integrado (sin necesidad de tener Maven en el PATH):
.\mvnw.cmd clean javafx:run
```

En Linux / macOS:
```bash
./mvnw clean javafx:run
```

### Opción 2: Empaquetar y Ejecutar como JAR autónomo

El archivo `pom.xml` incluye el plugin `maven-shade-plugin` y la clase intermediaria `Launcher.java`:

```bash
# Compilar y empaquetar
mvn clean package -DskipTests

# Ejecutar el archivo JAR generado
java -jar target/CRUD_JavaFX-1.0.0.jar
```

### Opción 3: Ejecutar Pruebas Unitarias

Para comprobar el correcto funcionamiento de las operaciones CRUD en el `ArrayList`:

```bash
mvn test
```

---

## 📖 Instrucciones de Uso

1. **Registrar un nuevo producto:**
   - Llena los campos *Nombre*, *Precio* y *Cantidad*.
   - Haz clic en **"💾 Guardar Producto"**.
2. **Editar un producto:**
   - Selecciona un producto en la tabla o haz doble clic sobre su fila.
   - Modifica los valores en el formulario.
   - Haz clic en **"💾 Actualizar Cambios"** (o en **"✖ Cancelar"** para descartar).
3. **Eliminar un producto:**
   - Selecciona un producto en la tabla.
   - Haz clic en **"🗑️ Eliminar Selección"**.
   - Confirma la eliminación en el cuadro de diálogo.
4. **Buscar productos:**
   - Escribe en la caja de texto *"🔍 Buscar por nombre o ID..."* para filtrar la tabla al instante.

---

## ⚠️ Limitaciones Importantes

> [!WARNING]
> **Almacenamiento Volátil en Memoria RAM:**
> Este proyecto **NO** cuenta con base de datos ni persistencia en disco. Todos los datos se almacenan exclusivamente en un `ArrayList` durante la ejecución. Al cerrar la ventana o detener el programa, **las modificaciones, adiciones y eliminaciones se reinician**.
