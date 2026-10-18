package cooperativa.servicio.alerta;

import cooperativa.modelo.transaccion.TipoTransaccion;
import cooperativa.modelo.transaccion.Transaccion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DetectorFraudeTest {
    private final DetectorFraude detector = new DetectorFraude();
    private final List<Alerta> recibidas = new ArrayList<>();

    private Transaccion trx(String monto) {
        return new Transaccion("TRX-1", "AH-001", TipoTransaccion.DEPOSITO,
                new BigDecimal(monto), LocalDateTime.of(2026, 10, 20, 10, 0));
    }

    @Test
    void elListenerRecibeLaAlertaCuandoUnaReglaSeCumple() {
        detector.registrarListener(recibidas::add);
        detector.agregarRegla(new ReglaMontoAlto(new BigDecimal("1000")));
        detector.analizar(trx("5000"), List.of());
        assertEquals(1, recibidas.size());
        assertEquals(NivelRiesgo.ALTO, recibidas.get(0).getNivel());
    }

    @Test
    void sinReglasNoHayAlertas() {
        detector.registrarListener(recibidas::add);
        detector.analizar(trx("5000"), List.of());
        assertTrue(recibidas.isEmpty());
    }

    @Test
    void unaReglaDesactivadaNoAvisaYAlReactivarlaVuelveAAvisar() {
        detector.registrarListener(recibidas::add);
        detector.agregarRegla(new ReglaMontoAlto(new BigDecimal("1000")));
        detector.desactivarRegla("Monto alto");
        detector.analizar(trx("5000"), List.of());
        assertTrue(recibidas.isEmpty());
        detector.activarRegla("Monto alto");
        detector.analizar(trx("5000"), List.of());
        assertEquals(1, recibidas.size());
    }

    @Test
    void unListenerQuitadoDejaDeRecibirAvisos() {
        AlertaListener listener = recibidas::add;
        detector.registrarListener(listener);
        detector.agregarRegla(new ReglaMontoAlto(new BigDecimal("1000")));
        detector.quitarListener(listener);
        detector.analizar(trx("5000"), List.of());
        assertTrue(recibidas.isEmpty());
    }

    @Test
    void laListaDeReglasNoSePuedeModificarDesdeAfuera() {
        detector.agregarRegla(new ReglaMontoAlto(new BigDecimal("1000")));
        assertEquals(1, detector.getReglas().size());
        assertThrows(UnsupportedOperationException.class,
                () -> detector.getReglas().add(new ReglaMontoAlto(BigDecimal.ONE)));
    }
}
