package cooperativa.vista;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Botón redondeado con tres estilos: primario (relleno), secundario (borde gris) y peligro (borde rojo). */
public class BotonTema extends JButton {
    public enum Estilo { PRIMARIO, SECUNDARIO, PELIGRO }

    private static final int ALTO = 38;
    private final Estilo estilo;
    private boolean encima = false;

    public BotonTema(String texto, Estilo estilo) {
        super(texto);
        this.estilo = estilo;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setFont(Tema.fuente(Font.BOLD, 13));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setAlignmentX(LEFT_ALIGNMENT);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                encima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                encima = false;
                repaint();
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(fm.stringWidth(getText()) + 40, ALTO);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, ALTO);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Color fondo;
        Color texto;
        Color borde = null;
        if (!isEnabled()) {
            fondo = new Color(0xE5E7EB);
            texto = new Color(0x9CA3AF);
        } else if (estilo == Estilo.PRIMARIO) {
            fondo = getModel().isPressed() ? Tema.PRIMARIO_HOVER.darker() : encima ? Tema.PRIMARIO_HOVER : Tema.PRIMARIO;
            texto = Color.WHITE;
        } else if (estilo == Estilo.PELIGRO) {
            fondo = encima ? new Color(0xFDECEC) : Color.WHITE;
            texto = Tema.PELIGRO;
            borde = new Color(0xF0B4B4);
        } else {
            fondo = encima ? new Color(0xEEF2F6) : Color.WHITE;
            texto = Tema.TEXTO;
            borde = new Color(0xCBD3DE);
        }

        int w = getWidth();
        int h = getHeight();
        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, w, h, 12, 12);
        if (borde != null) {
            g2.setColor(borde);
            g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
        }
        g2.setFont(getFont());
        g2.setColor(texto);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(getText(), (w - fm.stringWidth(getText())) / 2, (h - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }
}
