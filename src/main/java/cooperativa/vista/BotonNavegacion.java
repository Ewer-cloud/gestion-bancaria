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

/** Botón del menú lateral. Puede quedar marcado como activo y mostrar una insignia con un contador (alertas nuevas). */
public class BotonNavegacion extends JButton {
    private static final int ALTO = 46;
    private boolean seleccionado = false;
    private boolean encima = false;
    private int insignia = 0;

    public BotonNavegacion(String texto) {
        super(texto);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setFont(Tema.fuente(Font.PLAIN, 14));
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

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
        repaint();
    }

    /** Número que se pinta como globo rojo a la derecha. Con 0 no se muestra. */
    public void setInsignia(int insignia) {
        this.insignia = insignia;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, ALTO);
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
        int w = getWidth();
        int h = getHeight();

        if (seleccionado) {
            g2.setColor(new Color(0x1C4A4F));
            g2.fillRoundRect(0, 0, w, h, 12, 12);
            g2.setColor(new Color(0x2DD4BF));
            g2.fillRoundRect(0, 11, 4, h - 22, 4, 4);
        } else if (encima) {
            g2.setColor(Tema.LATERAL_HOVER);
            g2.fillRoundRect(0, 0, w, h, 12, 12);
        }

        g2.setFont(getFont());
        g2.setColor(seleccionado ? Color.WHITE : new Color(0xB4C7CA));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(getText(), 20, (h - fm.getHeight()) / 2 + fm.getAscent());

        if (insignia > 0) {
            g2.setFont(Tema.fuente(Font.BOLD, 11));
            FontMetrics fi = g2.getFontMetrics();
            String texto = String.valueOf(insignia);
            int ancho = Math.max(22, fi.stringWidth(texto) + 14);
            int x = w - ancho - 12;
            int y = (h - 20) / 2;
            g2.setColor(new Color(0xE5484D));
            g2.fillRoundRect(x, y, ancho, 20, 20, 20);
            g2.setColor(Color.WHITE);
            g2.drawString(texto, x + (ancho - fi.stringWidth(texto)) / 2, y + (20 - fi.getHeight()) / 2 + fi.getAscent());
        }
        g2.dispose();
    }
}
