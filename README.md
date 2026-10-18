# Gestión bancaria para cooperativas pequeñas

Proyecto final de Programación Orientada a Objetos. Es un sistema para que el cajero de una cooperativa pequeña (la "Cooperativa X", que es ficticia) cree clientes, abra cuentas, deposite, retire, busque transacciones y reciba alertas cuando algo se ve sospechoso.

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
mvn compile      # compila
mvn test         # corre las pruebas JUnit
```

Para ejecutar, abre `cooperativa.Main` en IntelliJ y dale Run (con JDK 25).
Por defecto guarda en SQLite (archivo `cooperativa.db`, que se crea solo y no se sube a Git).
Para probar sin base de datos: Run > Edit Configurations > Program arguments: `--memoria`.

Si al arrancar aparece una advertencia sobre "acceso nativo" de sqlite-jdbc, no es un error:
se silencia con la opción de VM `--enable-native-access=ALL-UNNAMED`.

Estado: etapa 2 completa (núcleo / capa 1 funcionando de punta a punta, más los repositorios SQLite de las 3 tablas).

## Estructura

```
src/main/java/cooperativa/
├─ modelo/        cuenta, cliente, transaccion, excepcion, decorador
├─ repositorio/   memoria, sqlite
├─ servicio/      Banco, busqueda, alerta
├─ vista/         panel, tabla
├─ Main.java
└─ DatosDemo.java
```

Las capas solo dependen de la de abajo: `vista` → `servicio` → `repositorio` → `modelo`. Un botón de la vista solo llama a un método de `Banco` y muestra el resultado; no lleva lógica de negocio.

## Cómo trabajamos

- `main` está protegida: nadie sube directo, todo entra por Pull Request con una aprobación.
- Cada módulo tiene su rama (`feature/cuentas`, `feature/clientes-transacciones`, `feature/alertas`, `feature/gui`, `feature/busqueda`).
- Cada quien edita solo sus packages.
- No se cambia una firma sin avisar al grupo.
- Antes de abrir un Pull Request: `git pull origin main` en tu rama.
- Mensajes de commit con este formato: `feat(cuentas): agrega validación de monto`.
- Nunca se sube el archivo `.db` ni las carpetas de IntelliJ.