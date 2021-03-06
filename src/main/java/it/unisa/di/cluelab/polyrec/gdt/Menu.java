package it.unisa.di.cluelab.polyrec.gdt;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Menu.
 */
@SuppressWarnings("checkstyle:multiplestringliterals")
public class Menu {

    protected MainFrame mainFrame;

    // JMenuItem exportTemplatesFile;
    protected JMenuItem exit;
    protected JMenuItem settings;
    protected JMenuItem send;
    protected JMenuItem replaceClassesSamples;
    protected JMenuItem addClassesSamples;
    protected JMenuItem replaceClasses;
    protected JMenuItem addTemplates;
    protected JMenuItem exportRecognizer;

    protected JMenuItem save;

    protected JMenuItem saveas;

    protected JMenuItem newGS;

    @SuppressWarnings({"checkstyle:executablestatementcount", "checkstyle:javancss"})
    public Menu(MainFrame mainFrame) throws IOException {
        this.mainFrame = mainFrame;
        final JMenuBar menuBar = new JMenuBar();
        final MenuListener listener = new MenuListener(this.mainFrame);
        final JMenu menu = new JMenu("MENU");

        menu.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/menu.png"))));

        exit = new JMenuItem("Exit");
        exit.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/exit.png"))));
        exit.addActionListener(listener);

        settings = new JMenuItem("Settings");
        settings.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/settings.png"))));
        settings.addActionListener(listener);

        send = new JMenuItem("Send App Via Bluetooth");
        send.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/bluetooth.png"))));
        send.addActionListener(listener);

        final JMenu addSamplesMenu = new JMenu("Import Example Templates");

        addSamplesMenu.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/incoming.png"))));

        replaceClassesSamples = new JMenuItem("Replace current templates");
        replaceClassesSamples.setToolTipText("Replace all classes and templates (remove all current)");
        replaceClassesSamples.addActionListener(listener);
        addSamplesMenu.add(replaceClassesSamples);

        addClassesSamples = new JMenuItem("Add");
        addClassesSamples.setToolTipText("Add new classes (add new templates to classes  with same name)");
        addClassesSamples.addActionListener(listener);
        addSamplesMenu.add(addClassesSamples);

        final JMenu importMenu = new JMenu("Open Gesture Set");
        importMenu.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/folder.png"))));

        replaceClasses = new JMenuItem("Replace current templates");
        replaceClasses.setToolTipText("Replace all classes and templates (remove all current)");
        replaceClasses.addActionListener(listener);
        importMenu.add(replaceClasses);

        addTemplates = new JMenuItem("Add");
        addTemplates.setToolTipText("Add new classes (add new templates to classes  with same name)");
        addTemplates.addActionListener(listener);
        importMenu.add(addTemplates);

        exportRecognizer = new JMenuItem("Export Recognizer (Java)");
        exportRecognizer.addActionListener(listener);
        exportRecognizer.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/send.png"))));

        newGS = new JMenuItem("New Gesture Set");
        newGS.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/tabs.png"))));
        newGS.addActionListener(listener);

        save = new JMenuItem("Save Gesture Set");
        save.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/save-16-blue.png"))));
        save.addActionListener(listener);

        saveas = new JMenuItem("Save Gesture Set As..");
        saveas.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/save-16-blue.png"))));
        saveas.addActionListener(listener);

        updateMenu();
        menu.add(send);
        menu.add(exportRecognizer);
        menu.addSeparator();
        menu.add(addSamplesMenu);

        menu.add(newGS);
        menu.add(importMenu);
        menu.add(save);
        menu.add(saveas);
        menu.addSeparator();

        menu.add(settings);

        menu.add(exit);
        menuBar.add(menu);
        mainFrame.setJMenuBar(menuBar);

    }

    void updateMenu() {

        if (mainFrame.getRecognizer().getClassNames().size() == 0) {

            saveas.setEnabled(false);

            save.setEnabled(false);
        } else {
            saveas.setEnabled(true);
            if (mainFrame.getOpenedFile() != null && mainFrame.getExtOpenedFile() != null
                    && !mainFrame.getOpenedFile().equals("") && !mainFrame.getExtOpenedFile().equals("")) {
                save.setEnabled(true);
            }

        }

    }

}
