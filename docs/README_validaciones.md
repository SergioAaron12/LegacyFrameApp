# Guía rápida para editar validaciones y textos de error

Esta guía te muestra exactamente dónde cambiar reglas de validación (largo máximo, solo números/letras, etc.) y los textos de error en el proyecto. Incluye rutas de archivo, funciones clave y ejemplos de edición.

## 1) Validaciones centrales (dominio)

Archivo: `app/src/main/java/com/example/legacyframeapp/domain/validation/validators.kt`

Aquí están las funciones reutilizables para formularios (login/registro):

- `validateEmail(email: String)`: valida el correo con `Patterns.EMAIL_ADDRESS`.
  - Cambiar texto de error: edita los strings dentro de la función.
  - Cambiar reglas (ej. mínimo de caracteres): agrega o edita las condiciones `if (...) return "mensaje"`.
  - Nota: Si ves una línea como `if (email.length < 8) return "deb"`, cámbiala por un mensaje real o elimínala si no quieres esa regla.

- `validateNameLettersOnly(name: String)`: solo letras y espacios.
  - Cambiar regex permitida: edita `Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$")`.
  - Cambiar texto de error: edita "Solo letras y espacios".

- `validateApellido(apellido: String)`: similar a nombre, pero opcional.

- `validatePhoneDigitsOnly(phone: String)`: solo dígitos y largo exacto.
  - Cambiar obligatoriedad: edita `if (phone.isBlank()) return "El teléfono es obligatorio"`.
  - Cambiar a largo 8/10 dígitos: cambia `if (phone.length != 9)` a tu valor.
  - Cambiar texto de error: edita los strings.

- `validateRut(rut: String)` y `validateDv(dv: String, rut: String)`: reglas básicas y cálculo de DV.
  - Cambiar rango de largo: ajusta `if (rut.length < 7 || rut.length > 8)`.
  - Cambiar textos: edita strings dentro de cada `if`.

- `validateStrongPassword(pass: String)`: complejidad de contraseña.
  - Cambiar largo mínimo: `if (pass.length < 8) ...`.
  - Reglas de mayúscula/número/símbolo: edita o comenta las líneas respectivas.

- `validateConfirm(pass, confirm)`: confirma que coincidan.

Ejemplo rápido (teléfono a 8 dígitos):
```kotlin
fun validatePhoneDigitsOnly(phone: String): String? {
    if (phone.isBlank()) return "El teléfono es obligatorio"
    if (!phone.all { it.isDigit() }) return "Solo números"
    if (phone.length != 8) return "Debe tener 8 dígitos" // <- cambiado
    return null
}
```

## 2) Validaciones y mensajes en el ViewModel

Archivo: `app/src/main/java/com/example/legacyframeapp/ui/viewmodel/AuthViewModel.kt`

- Handlers de registro/login usan las funciones del dominio y además definen textos por campo vacío al enviar:
  - `onRegister*Change(...)`: llaman a `validate...` y setean errores en `RegisterUiState`.
  - `submitRegister()`: si falta un campo, rellena mensajes tipo "El nombre es obligatorio". Puedes cambiar esos textos aquí.

- Validación de precios (molduras/cuadros):
  - Función privada `validatePrice(price: String): String?` (cerca del final del archivo).
    - Máximo de dígitos: actualmente `if (price.length > 9) return "Máximo 9 dígitos"`.
    - Cambia 9 por el tope que quieras y ajusta el texto.

Ejemplo (tope 6 dígitos):
```kotlin
private fun validatePrice(price: String): String? {
    if (price.isBlank()) return "El precio es obligatorio"
    if (!price.all { it.isDigit() }) return "Solo números"
    if (price.length > 6) return "Máximo 6 dígitos" // <- cambiado
    return null
}
```

Importante: En las pantallas, también se limita lo que el usuario puede tipear (ver sección 3). Mantén consistentes ambos lugares (UI y ViewModel).

## 3) Restricciones directamente en la UI (inputs)

Además de validar, las pantallas limitan la entrada del usuario (solo números, largo máximo) y muestran mensajes.

- Molduras: `app/src/main/java/com/example/legacyframeapp/ui/screen/AddProductScreen.kt`
  - Campo Precio:
    ```kotlin
    onValueChange = { onPriceChange(it.filter(Char::isDigit).take(6)) }
    ```
    - Cambia `6` por el máximo deseado.
    - Muestra formato con miles: usa `ThousandSeparatorTransformation()`.

- Cuadros: `app/src/main/java/com/example/legacyframeapp/ui/screen/AddCuadroScreen.kt`
  - Campo Precio:
    ```kotlin
    onValueChange = { newValue ->
        if (newValue.all { it.isDigit() } && newValue.length <= 6) {
            onPriceChange(newValue)
        }
    }
    ```
    - Cambia `6` por tu máximo.

- Transformación visual (separadores de miles): `app/src/main/java/com/example/legacyframeapp/ui/components/PriceVisuals.kt`
  - `formatWithThousands(...)` y `ThousandSeparatorTransformation`.
  - Si no quieres puntos de miles, retira `visualTransformation` del `OutlinedTextField`.

## 4) Dónde cambiar textos visibles al usuario

- Mensajes de error vienen del estado (`state.*Error`) y se pintan cerca de los inputs.
  - Registro: `ui/screen/RegisterScreen.kt` (labels, placeholders, y muestra de errores).
  - Login: `ui/screen/LoginScreen.kt`.
  - AddProduct/AddCuadro: muestran `priceError`, `imageError`, etc.

- Textos hardcodeados: están en español dentro del código. Si quieres internacionalizar:
  - Mueve los strings a `app/src/main/res/values/strings.xml` y usa `stringResource(R.string.tu_clave)`.

## 5) Cambios comunes (copiar/pegar)

- Cambiar teléfono a 8 dígitos:
  1) `validators.kt` → `if (phone.length != 8) return "Debe tener 8 dígitos"`.
  2) (Opcional) Limitar input en la UI del registro a 8 dígitos si agregas un filtro al `OutlinedTextField`.

- Cambiar precio máximo de 6 a 9 dígitos:
  1) `AddProductScreen.kt` → `take(9)`.
  2) `AddCuadroScreen.kt` → `newValue.length <= 9`.
  3) `AuthViewModel.validatePrice` → `if (price.length > 9) ...` y su texto.

- Suavizar reglas de contraseña (sin símbolo obligatorio):
  1) `validators.kt` → comenta/elimina `if (!pass.any { !it.isLetterOrDigit() }) return "Debe incluir un símbolo"`.

- Permitir números y letras en nombre:
  1) `validators.kt` → cambia el `Regex` a algo como `^[A-Za-z0-9ÁÉÍÓÚÑáéíóúñ ]+$`.

## 6) Checklist de consistencia

- Si subes el tope de dígitos en UI, súbelo también en el ViewModel (y viceversa).
- Ajusta los textos de error para que coincidan con la nueva regla.
- Compila y prueba el flujo (registro, añadir moldura/cuadro) para ver errores y límites en acción.

## 7) Ubicaciones rápidas

- Validadores centrales: `domain/validation/validators.kt`
- ViewModel (textos y validación de precio): `ui/viewmodel/AuthViewModel.kt`
- UI Añadir Moldura: `ui/screen/AddProductScreen.kt`
- UI Añadir Cuadro: `ui/screen/AddCuadroScreen.kt`
- Formato de precio (miles): `ui/components/PriceVisuals.kt`
- Pantallas de formularios (labels/placeholders): `ui/screen/RegisterScreen.kt`, `ui/screen/LoginScreen.kt`

---

Sugerencia: mantén valores como "máximo de dígitos" en constantes compartidas para no olvidarte de cambiarlos en dos lugares. Si quieres, puedo centralizar eso en una siguiente iteración.

## 8) Toggle de tema (claro / oscuro)

La app ahora usa un toggle con ícono (sol / luna) en `SettingsScreen.kt` para cambiar entre tema claro y oscuro.

- Archivo: `app/src/main/java/com/example/legacyframeapp/ui/screen/SettingsScreen.kt`
- Recurso íconos: `res/drawable/ic_sun.xml` y `res/drawable/ic_moon.xml`
- Persistencia: `UserPreferences.isDarkMode` vía DataStore (`UserPreferences.kt`).

Si quieres revertir al switch anterior, reemplaza el `IconToggleButton` por `Switch` dentro del `ListItem`.

## 9) Restricción de compra para invitados

Los usuarios no autenticados (invitados) ya no pueden finalizar una compra desde el carrito.

- Archivo modificado: `ui/screen/CartScreen.kt`
- Comportamiento: El botón "Comprar" aparece deshabilitado y muestra el texto "Inicia sesión" si `session.isLoggedIn` es `false`.
- Al tocar el botón estando deslogueado se lanza un `Snackbar` con el mensaje: "Debes iniciar sesión para comprar.".
- Lógica de guardia añadida en `CartScreenVm`: antes de llamar a `recordOrder(...)` verifica `session.isLoggedIn`.

Para cambiar el mensaje, edita la cadena dentro de `showSnackbar("Debes iniciar sesión para comprar.")`.

### Redirección automática
Al intentar comprar sin sesión ahora se navega a la pantalla de Login (ver `NavGraph.kt` donde se pasa `onRequireLogin = goLogin` a `CartScreenVm`).

## 10) Ajustes de paleta café

Se refinaron `LightColorScheme` y `DarkColorScheme` en `Theme.kt` para evitar tonos rosados / rojos y blancos puros:
- `surface` claro ahora usa el mismo crema (`LightBackground`).
- `surfaceVariant` claro: `#F2E7DB` (crema ligeramente más tostado).
- Fondo oscuro: `#1A140E` y superficie `#241A12` (marrones profundos en lugar de negro/gris).
- Variante oscura: `#2E2219` para listas/elementos elevados.

Si deseas volver a los valores previos, puedes restaurar los códigos hex en `Theme.kt`.

## 11) Estados de botones y contraste

Se actualizó `AppButton.kt` para eliminar el verde fuera de la paleta y usar la paleta café:

- Variantes disponibles: `Filled`, `Tonal`, `Outlined` vía parámetro `variant`.
- Colores:
  - Filled: `primary` / `onPrimary`.
  - Tonal: `primaryContainer` / `onPrimaryContainer`.
  - Outlined: borde y texto con `primary`.
- Disabled: se aplica alpha (0.35–0.55) asegurando distinción visual y legibilidad.

Botón "Comprar" (carrito) ahora usa estos colores y se atenúa cuando está deshabilitado (no logueado).

Para cambiar opacidades, edita las llamadas `copy(alpha = ...)`.
