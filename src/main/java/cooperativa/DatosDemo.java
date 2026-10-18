package cooperativa;

import cooperativa.modelo.cliente.Cliente;
import cooperativa.modelo.cuenta.Cuenta;
import cooperativa.modelo.excepcion.CuentaBloqueadaException;
import cooperativa.modelo.excepcion.SaldoInsuficienteException;
import cooperativa.servicio.Banco;

import java.math.BigDecimal;

/**
 * Carga datos de ejemplo para que la interfaz arranque con algo que mostrar. (Integración - Ewer)
 * Los montos quedan por debajo del umbral de fraude y cada cuenta tiene pocos movimientos,
 * así que no se disparan alertas: las alertas se provocan en vivo durante la demo.
 */
public final class DatosDemo {
    private DatosDemo() {
    }

    public static void cargar(Banco banco) {
        Cliente ana = banco.crearCliente("Ana Pérez", "3001112233");
        Cliente luis = banco.crearCliente("Luis Gómez", "3104445566");
        Cliente marta = banco.crearCliente("Marta Rojas", "3207778899");

        Cuenta ahorroAna = banco.abrirCuenta(ana.getId(), "AHORRO");
        Cuenta corrienteLuis = banco.abrirCuenta(luis.getId(), "CORRIENTE");
        Cuenta ahorroMarta = banco.abrirCuenta(marta.getId(), "AHORRO");

        try {
            banco.depositar(ahorroAna.getNumero(), new BigDecimal("1500000"));
            banco.retirar(ahorroAna.getNumero(), new BigDecimal("200000"));
            banco.depositar(corrienteLuis.getNumero(), new BigDecimal("800000"));
            banco.depositar(ahorroMarta.getNumero(), new BigDecimal("300000"));
        } catch (CuentaBloqueadaException | SaldoInsuficienteException e) {
            // Las cuentas recién creadas no están bloqueadas y los saldos alcanzan: no debería pasar.
            throw new IllegalStateException("Fallaron los datos de demostración", e);
        }
    }
}
