package it.unisa.di.cluelab.polyrec.gdt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import it.unisa.di.cluelab.polyrec.Gesture;
import it.unisa.di.cluelab.polyrec.TPoint;

/**
 * Gesture canvas.
 */
public class Canvas extends JPanel {
    static final int WIDTH = 800;
    static final int HEIGHT = 800;

    private static final long serialVersionUID = -8911000506413208631L;

    private final TemplateScreen screen;

    public Canvas(TemplateScreen screen) {
        this.setBackground(Color.lightGray);

        this.screen = screen;
        this.setBorder(BorderFactory.createLineBorder(Color.black));
        final Dimension dim = new Dimension(WIDTH, HEIGHT);
        this.setPreferredSize(dim);

        setMaximumSize(dim);
        setMinimumSize(dim);

    }

    private void paintCurrentGesture(Graphics g) {

        final Graphics2D g2 = (Graphics2D) g;
        // final Graphics2D g2 = (Graphics2D) this.getGraphics().create();

        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(1));

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (this.screen.getCurrentGesture().getPoints().size() > 0) {

            final int pointCount = this.screen.getCurrentGesture().getPoints().size();
            if (pointCount < 2) {
                return;
            }
            // g2.drawRect((int)this.screen.getCurrentGesture().getBoundingBox().x,
            // (int)this.screen.getCurrentGesture().getBoundingBox().y,
            // (int)this.screen.getCurrentGesture().getBoundingBox().width,
            // (int)this.screen.getCurrentGesture().getBoundingBox().height);
            for (int i = 0; i < pointCount - 1; i++) {

                final TPoint p1 = this.screen.getCurrentGesture().getPoints().get(i);
                final TPoint p2 = this.screen.getCurrentGesture().getPoints().get(i + 1);

                g2.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());

            }
        }
        g2.dispose();
    }

    @SuppressWarnings({"checkstyle:cyclomaticcomplexity", "checkstyle:executablestatementcount"})
    void paintGestures(Graphics g) {

        final Graphics2D g2;
        if (g != null) {
            g2 = (Graphics2D) g;
        } else {
            g2 = (Graphics2D) this.getGraphics().create();
        }
        g2.setColor(this.getBackground());
        g2.fillRect(1, 1, this.getPreferredSize().width, this.getPreferredSize().height);

        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // TPoint centroid =
        // this.screen.canvasGestures.get(DetailScreen.GESTURE).getCentroid();
        // g2.rotate(Math.toRadians(screen.rotationAngle), centroid.getX(),
        // centroid.getY());*/
        Gesture gesture;
        boolean rotate = true;

        for (final Map.Entry<Integer, Gesture> entry : this.screen.canvasGestures.entrySet()) {

            gesture = entry.getValue();

            if (rotate) {
                g2.rotate(Math.toRadians(screen.rotationAngle), gesture.getCentroid().getX(),
                        gesture.getCentroid().getY());
                rotate = false;
            }

            final ArrayList<TPoint> points = gesture.getPoints();

            final int pointCount = points.size();
            if (pointCount < 2) {
                return;
            }

            // if (this.screen.canvasGestures.entrySet().size() == 1) {
            // TPoint centroid = gesture.getCentroid();
            //
            // g2.rotate(Math.toRadians((double) screen.rotationAngle), centroid.getX(), centroid.getY());
            // }

            if (entry.getKey() == TemplateScreen.VERTEX) {

                g2.setStroke(new BasicStroke(6));

                for (int i = 0; i < pointCount; i++) {
                    final TPoint p1 = gesture.getPoints().get(i);
                    g2.setColor(Color.red);

                    // if (i == 0) g2.setColor(Color.blue);
                    // else if (i == pointCount - 1) g2.setColor(Color.green);
                    // else g2.setColor(Color.red);

                    g2.drawLine((int) p1.getX(), (int) p1.getY(), (int) p1.getX(), (int) p1.getY());
                }
            } else {
                // POLYLINE o GESTURE

                g2.setStroke(new BasicStroke(5));
                if (entry.getKey() == TemplateScreen.POLYLINE) {
                    g2.setColor(new Color(0, 74, 160));
                } else {
                    g2.setColor(Color.RED);
                    // g2.drawLine((int)gesture.getCentroid().getX(),
                    // (int)gesture.getCentroid().getY(), (int)
                    // gesture.getCentroid().getX(),
                    // (int)gesture.getCentroid().getY());
                }

                g2.drawLine((int) gesture.getPoints().get(0).getX(), (int) gesture.getPoints().get(0).getY(),
                        (int) gesture.getPoints().get(0).getX(), (int) gesture.getPoints().get(0).getY());
                g2.setStroke(new BasicStroke(1));

                for (int i = 0; i < pointCount - 1; i++) {

                    final TPoint p1 = gesture.getPoints().get(i);
                    final TPoint p2 = gesture.getPoints().get(i + 1);

                    g2.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());

                    if (entry.getKey() == TemplateScreen.GESTURE) {

                        for (int pointer = 2; pointer <= gesture.getPointers(); pointer++) {
                            if (pointer % 2 != 0) {
                                g2.drawLine((int) p1.getX() + 10 * (pointer / 2), (int) p1.getY() + 10 * (pointer / 2),
                                        (int) p2.getX() + 10 * (pointer / 2), (int) p2.getY() + 10 * (pointer / 2));
                            } else {
                                g2.drawLine((int) p1.getX() - 10 * (pointer / 2), (int) p1.getY() - 10 * (pointer / 2),
                                        (int) p2.getX() - 10 * (pointer / 2), (int) p2.getY() - 10 * (pointer / 2));
                            }
                        }
                    }

                }

            }

        }
        g2.dispose();

    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (this.screen.mode == TemplateScreen.CURRENT && this.screen.getCurrentGesture().getPoints().size() > 0) {
            paintCurrentGesture(g);

        } else if (this.screen.mode != TemplateScreen.GESTURE_TIMED) {

            paintGestures(g);

        }

    }

    /**
     * Draw a line.
     * 
     * @param p1
     *            first point
     * @param p2
     *            second point
     */
    public void paintLine(TPoint p1, TPoint p2) {
        final Graphics2D g2 = (Graphics2D) this.getGraphics();
        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
    }

    public void paintTimedGesture() {

        final Graphics2D g2 = (Graphics2D) this.getGraphics().create();
        g2.setColor(this.getBackground());
        g2.fillRect(1, 1, this.getPreferredSize().width, this.getPreferredSize().height);

        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        final Gesture gesture = this.screen.canvasGestures.get(TemplateScreen.GESTURE);
        if (gesture.getPoints().size() > 0) {

            final int pointCount = gesture.getPoints().size();
            if (pointCount < 2) {
                return;
            }
            for (int i = 0; i < pointCount - 1; i++) {

                final TPoint p1 = gesture.getPoints().get(i);
                final TPoint p2 = gesture.getPoints().get(i + 1);

                final int wait = (int) (p2.getTime() - p1.getTime());

                try {
                    Thread.sleep(wait);

                } catch (final InterruptedException e) {

                    e.printStackTrace();
                }

                g2.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());

            }
        }
        g2.dispose();
    }

}
