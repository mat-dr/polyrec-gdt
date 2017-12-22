package it.unisa.di.cluelab.polyrec.gdt;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import it.unisa.di.cluelab.polyrec.TPoint;

/**
 * @author roberto
 *
 */
public class MainFrame extends JFrame implements WindowListener {
    static final String EXTENSION_PGS = "PolyRec gesture set (.pgs)";
    static final String EXTENSION_XML = "Extensible markup language (.xml)";

    static final int MAINSCREEN = 0;
    static final int DETAILSCREEN = 1;

    private static final long serialVersionUID = 4781489406562650482L;

    protected int screenMode = MAINSCREEN;

    // name of opened file
    private String openedFile;
    // extension of opened file
    private String extOpenedFile;

    private ExtendedPolyRecognizerGSS recognizer;

    private JPanel container;
    private TemplateScreen templateScreen;
    private final Menu menu;
    private Server server;

    public MainFrame() throws IOException {

        recognizer = new ExtendedPolyRecognizerGSS();

        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setTitle("PolyRec GDT");

        // setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 980));
        setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);

        setVisible(true);

        menu = new Menu(this);
        setScreen(new DashboardScreen(this, false));

    }

    public void setScreen(JPanel panel) {

        container = panel;
        templateScreen = panel instanceof TemplateScreen ? (TemplateScreen) panel : null;

        setContentPane(container);

        repaint();

    }

    public JPanel getScreen() {
        return container;

    }

    @Override
    public void paint(Graphics g) {

        paintComponents(g);

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        final File file = new File("gestures.pgs");
        file.deleteOnExit();
        int result = JOptionPane.CLOSED_OPTION;

        if (menu.save.isEnabled() || menu.saveas.isEnabled()) {
            result = JOptionPane.showConfirmDialog(null, "Save Gesture Set before closing?", "Confirm",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                if (menu.save.isEnabled()) {
                    menu.save.doClick();
                } else if (menu.saveas.isEnabled()) {
                    menu.saveas.doClick();
                }

                dispose();
                System.exit(0);
            } else if (result == JOptionPane.NO_OPTION) {

                dispose();
                System.exit(0);
            }

        } else {

            dispose();
            System.exit(0);
        }

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    public ExtendedPolyRecognizerGSS getRecognizer() {
        return recognizer;
    }

    public void setRecognizer(ExtendedPolyRecognizerGSS recognizer) {
        this.recognizer = recognizer;
    }

    public String getOpenedFile() {
        return openedFile;
    }

    public void setOpenedFile(String openedFile) {
        this.openedFile = openedFile;
    }

    public String getExtOpenedFile() {
        return extOpenedFile;
    }

    public void setExtOpenedFile(String extOpenedFile) {
        this.extOpenedFile = extOpenedFile;
    }

    public Menu getMenu() {
        return menu;
    }

    public static boolean isModalDialogShowing() {
        final Window[] windows = Window.getWindows();
        if (windows != null) {
            for (final Window w : windows) {
                if (w.isShowing() && w instanceof Dialog) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        new MainFrame();

        Settings.loadSettings();

    }

    public void stopServer() {
        if (server != null) {
            server.stopped = true;
            try {
                if (server.ssc != null) {
                    server.ssc.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (server.sc != null) {
                    server.sc.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startServer() {
        if (server == null || server.stopped) {
            server = new Server();
            new Thread(server).start();
        }
    }

    /**
     * WiFi server.
     */
    private class Server implements Runnable {
        static final int SERVER_PORT = 16579;
        private ServerSocketChannel ssc;
        private SocketChannel sc;
        private boolean stopped;

        private void displayWarningMessage(String message) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    final TemplateScreen ts = templateScreen;
                    if (ts != null && !stopped) {
                        ts.display.set(message, Display.DISPLAY_WARNING);
                    }
                }
            });
        }

        // CHECKSTYLE:OFF
        @Override
        public void run() {
            try {
                ssc = ServerSocketChannel.open();
                ssc.socket().bind(new InetSocketAddress(SERVER_PORT), 1);

                try (SocketChannel channel = ssc.accept()) {
                    sc = channel;
                    final ByteBuffer sbf = ByteBuffer.allocate(8);
                    while (!stopped && sbf.hasRemaining()) {
                        if (sc.read(sbf) == -1) {
                            displayWarningMessage("Connection closed.");
                            return;
                        }
                    }
                    sbf.flip();
                    final int width = sbf.getInt();
                    final int height = sbf.getInt();
                    final TemplateScreen tscreen = templateScreen;
                    if (tscreen == null) {
                        return;
                    }
                    final double sca = Math.min(tscreen.canvas.getWidth() / (double) width,
                            tscreen.canvas.getHeight() / (double) height);
                    final double scale = sca > 0 && sca < java.lang.Double.MAX_VALUE ? sca : 1;

                    final ByteBuffer bf = ByteBuffer.allocate(16);
                    boolean first = true;
                    read: for (;;) {
                        while (bf.hasRemaining()) {
                            if (stopped || sc.read(bf) == -1) {
                                break read;
                            }
                        }
                        if (stopped) {
                            break read;
                        }
                        bf.flip();
                        final float x = bf.getFloat();
                        final float y = bf.getFloat();
                        final long t = bf.getLong();
                        bf.clear();
                        if (TemplateScreen.getDrawMode() != TemplateScreen.SMARTPHONE_WIFI) {
                            continue;
                        }
                        System.out.println("Received: x=" + x + " y=" + y + " t=" + t);
                        if (t == -1) {
                            first = false;
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    final TemplateScreen ts = templateScreen;
                                    if (ts != null) {
                                        ts.strokeCompleted();
                                    }
                                }
                            });
                        } else if (t == -2) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    final TemplateScreen ts = templateScreen;
                                    if (ts != null) {
                                        ts.clearCanvas();
                                    }
                                }
                            });
                        } else if (t == -3) {
                            // NOP
                        } else if (t == -4) {
                            break read;
                        } else {
                            final boolean startStroke = first;
                            if (first) {
                                first = false;
                            }
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    final TemplateScreen ts = templateScreen;
                                    if (ts != null) {
                                        if (startStroke) {
                                            ts.startStroke();
                                        }
                                        ts.getCurrentGesture().addPoint(new TPoint(x * scale, y * scale, t));
                                        ts.setState(TemplateScreen.STROKE_IN_PROGRESS);
                                        ts.setMode(TemplateScreen.CURRENT);
                                        repaint();
                                        ts.repaintCanvas();
                                    }
                                }
                            });
                        }
                    }

                }
            } catch (IOException e) {
                String m = e.getMessage();
                if (m == null) {
                    m = e.toString();
                }
                displayWarningMessage(m);
            } finally {
                try {
                    if (ssc != null) {
                        ssc.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (sc != null) {
                        sc.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stopped = true;
            }
        }
        // CHECKSTYLE:ON
    }
}
