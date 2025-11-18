# Legacy Frames App

Aplicación móvil Android para "Legacy Frames", una tienda de enmarcación y arte. Permite a los usuarios explorar productos (molduras y cuadros), gestionar un carrito de compras y (para administradores) gestionar el catálogo.

---

## Estudiantes 

* Ignacio Gomez
* Sergio Peña

---

## Funcionalidades Implementadas 

* **Autenticación:**
    * Registro de nuevos usuarios.
    * Inicio de sesión (incluyendo un usuario administrador predefinido).
    * Persistencia de sesión.
* **Catálogo:**
    * Visualización de Molduras.
    * Visualización de Cuadros.
    * Datos de ejemplo precargados en la base de datos local (Room).
* **Carrito de Compras:**
    * Añadir molduras y cuadros al carrito.
    * Ajustar cantidades.
    * Eliminar ítems.
    * Visualización del total.
    * Simulación de "Compra" (guarda orden, limpia carrito, muestra notificación).
* **Gestión de Administrador:**
    * Añadir nuevas molduras (con carga de imagen desde galería/cámara).
    * Añadir nuevos cuadros (con carga de imagen desde galería/cámara).
    * Eliminar molduras existentes.
    * Eliminar cuadros existentes.
    * Cambiar imagen de molduras existentes.
* **Perfil y Configuraciones:**
    * Pantalla de perfil (muestra datos básicos, permite cambiar avatar).
    * Pantalla de configuraciones:
        * Activar/desactivar Modo Oscuro.
        * Acceso al historial de compras.
        * Acceso a Términos y Condiciones.
        * Acceso a pantalla de Contacto.
* **Historial de Compras:**
    * Pantalla que muestra un registro de las compras realizadas por el usuario.
* **Interfaz de Usuario:**
    * Navegación con Barra Superior, Menú Lateral y Barra Inferior.
    * Splash Screen al inicio.
    * Tema claro/oscuro adaptativo.
    * Componentes reutilizables (botones, logo, etc.).
* **Otros:**
    * Notificaciones push para confirmación de compra.
    * Almacenamiento local de imágenes seleccionadas por el admin.
    * Validaciones en formularios.

---

## Pasos para Ejecutar 

1.  **Clonar el Repositorio:**
    ```bash
    git clone [URL_DEL_REPOSITORIO]
    cd [NOMBRE_DE_LA_CARPETA_DEL_PROYECTO]
    ```
2.  **Abrir en Android Studio:**
    * Abre Android Studio (se recomienda la versión más reciente, ej. Giraffe, Iguana o superior).
    * Selecciona `File > Open` (Archivo > Abrir).
    * Navega hasta la carpeta del proyecto que clonaste y selecciónala.
3.  **Sincronizar Gradle:**
    * Espera a que Android Studio indexe los archivos y descargue las dependencias (puede tardar unos minutos).
    * Si aparece una barra amarilla pidiendo sincronizar, haz clic en `Sync Now`. O ve a `File > Sync Project with Gradle Files`.
4.  **Ejecutar la App:**
    * Selecciona un emulador o conecta un dispositivo físico Android.
    * Haz clic en el botón `Run 'app'` (el triángulo verde ) en la barra superior.

> **Nota:** La primera vez que se ejecute, la base de datos se creará con productos y un cuadro de ejemplo. El usuario administrador es `admin@legacyframes.cl` con contraseña `Admin123!`.
