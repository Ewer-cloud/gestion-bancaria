# Gestión bancaria para cooperativas pequeñas

Proyecto final de Programación Orientada a Objetos. Es un sistema para que el cajero de una cooperativa pequeña (la "Cooperativa X", que es ficticia) cree clientes, abra cuentas, deposite, retire, transfiera, busque transacciones y reciba alertas cuando algo se ve sospechoso.

## Equipo

- Ewer: integración, guardián de `main` y búsqueda
- Daniel: clientes, transacciones y Banco
- Daniel L.: cuentas
- Víctor: alertas de fraude
- Valeria: interfaz gráfica

## Tecnologías

Java (JDK 25), Maven, Swing, SQLite con JDBC, JUnit 5, Git y GitHub.

## Cómo compilar y ejecutar

```
git clone https://github.com/Ewer-cloud/gestion-bancaria.git
cd gestion-bancaria
mvn clean test      # compila y corre las pruebas JUnit (hoy: 91, todas deben pasar)
```

Para ejecutar, abre `cooperativa.Main` en IntelliJ y dale Run (con JDK 25).

- Por defecto guarda en SQLite: el archivo `cooperativa.db` se crea solo, en la carpeta desde la que se ejecuta el programa (en IntelliJ, la raíz del proyecto), y no se sube a Git. La primera vez carga 3 clientes de ejemplo.
- Para empezar de cero, cierra el programa y borra `cooperativa.db`.
- Para probar sin base de datos: Run > Edit Configurations > Program arguments: `--memoria` (los datos se pierden al cerrar).
- Si al arrancar aparece una advertencia sobre "acceso nativo" de sqlite-jdbc, no es un error: se silencia con la opción de VM `--enable-native-access=ALL-UNNAMED`.

## Estado del proyecto

Hito actual: **integración 1** (tag `integracion-1`). El núcleo funciona de punta a punta.

**Funciona hoy**

- Clientes: crear, listar y consultar.
- Cuentas: abrir de ahorro o corriente, bloquear y desbloquear.
- Operaciones: depositar, retirar y transferir, con validaciones y una transacción registrada por cada movimiento.
- Búsqueda de transacciones por monto, tipo, cliente y fecha (los filtros se combinan con AND).
- Alertas de fraude: monto alto y ráfaga de movimientos, con nivel de riesgo, que llegan a la interfaz por el patrón Observer.
- Persistencia en SQLite con las 3 tablas (`cliente`, `cuenta`, `transaccion`): los datos siguen ahí al cerrar y abrir.
- 91 pruebas JUnit.

**Pendiente de la fase 3** (el código se congela el sábado 24 de octubre)

| Pendiente | Quién |
|---|---|
| Redondear el interés a 2 decimales en `CuentaAhorro` | Daniel L. |
| Decidir si el Decorator (seguro y tarjeta) se hace o se corta | Daniel L. |
| `ReglaHoraRara` y `DetectorFraude.isActiva` con sus pruebas | Víctor |
| Botón de cierre de mes en `PanelCuentas` | Valeria |
| Casillas para activar y desactivar reglas en `PanelAlertas` | Valeria y Víctor |
| `CriterioAnd`, `CriterioOr` y `ExportadorCSV` con sus pruebas | Ewer |
| Pruebas de `Cliente` y `Transaccion` | Daniel |
| Documento final (mínimo 3000 palabras, APA) y UML al día | todos |

## Demo de punta a punta

Los 7 pasos que se muestran en la sustentación. Con la configuración actual (`Main.java`), un depósito de más de 5.000.000 o 3 movimientos de una misma cuenta en 1 minuto disparan una alerta.

| # | Paso | Dónde |
|---|---|---|
| 1 | Crear un cliente | Clientes |
| 2 | Abrir una cuenta de ahorro para ese cliente | Cuentas |
| 3 | Depositar dinero (por ejemplo 1.000.000) | Operaciones |
| 4 | Intentar un retiro imposible (por ejemplo 9.999.999) y ver el error de saldo insuficiente | Operaciones |
| 5 | Provocar una alerta (depositar 6.000.000) y verla en la pestaña Alertas | Operaciones y Alertas |
| 6 | Buscar la transacción con un filtro (monto desde 5000000) | Búsqueda |
| 7 | Cerrar el programa, abrirlo de nuevo y comprobar que los datos siguen | Todo |

La lista de alertas no se guarda en la base de datos: al reabrir el programa aparece vacía.

## Base de datos

SQLite en un solo archivo, accedido por JDBC. Todo el SQL vive únicamente en `repositorio.sqlite`.

| Tabla | Columnas | Notas |
|---|---|---|
| `cliente` | `id` (PK), `nombre`, `telefono` | |
| `cuenta` | `numero` (PK), `tipo`, `saldo`, `id_titular` (FK a `cliente.id`), `bloqueada` | `tipo` es AHORRO o CORRIENTE |
| `transaccion` | `id` (PK), `numero_cuenta` (FK a `cuenta.numero`), `tipo`, `monto`, `fecha` | `fecha` en texto ISO-8601 |

El dinero se guarda como texto y se reconstruye como `BigDecimal` al leer. Las tablas se crean en el orden cliente, cuenta, transacción por las llaves foráneas, que se activan en cada conexión.

## Estructura

```
src/main/java/cooperativa/
├─ modelo/        cuenta, cliente, transaccion, excepcion, decorador
├─ repositorio/   memoria, sqlite
├─ servicio/      Banco, busqueda, alerta
├─ vista/         panel, tabla
├─ Main.java
└─ DatosDemo.java
src/test/java/cooperativa/   (misma estructura de packages)
```

Las capas solo dependen de la de abajo: `vista` → `servicio` → `repositorio` → `modelo`. Un botón de la vista solo llama a un método de `Banco` y muestra el resultado; no lleva lógica de negocio.

## Cómo trabajamos

- `main` está protegida: nadie sube directo, todo entra por Pull Request con una aprobación. Nunca `push --force`.
- Una rama por tarea, creada siempre desde un `main` actualizado y con el nombre `feature/f3-<modulo>`:
  ```
  git switch main
  git pull origin main
  git switch -c feature/f3-<modulo>
  ```
- Cada quien edita solo sus packages. No se cambia una firma sin avisar al grupo.
- Se une a `main` cada 2 o 3 días, sin dejar la rama más tiempo aislada.
- **Antes de abrir un Pull Request**, en tu rama:
    1. `git pull origin main`
    2. `mvn clean test` (tiene que pasar todo)
    3. Ejecutar `Main` y probar lo propio
    4. `git status`: que no se suban `.db`, `.idea` ni `target/`
    5. Que no quede ningún método con `TODO` o `UnsupportedOperationException`
- Mensajes de commit con el formato `tipo(módulo): descripción`, por ejemplo `feat(cuentas): agrega validación de monto`.
- Los hitos se marcan con un tag: `integracion-1` (hecho) e `integracion-2` (el congelamiento del sábado 24).
- Una vez fusionado el Pull Request, se borra la rama.
