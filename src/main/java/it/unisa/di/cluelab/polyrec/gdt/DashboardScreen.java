package it.unisa.di.cluelab.polyrec.gdt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import it.unisa.di.cluelab.polyrec.Polyline;

/**
 * Dashboard panel.
 */
@SuppressWarnings({"checkstyle:classfanoutcomplexity", "checkstyle:classdataabstractioncoupling",
    "checkstyle:multiplestringliterals"})
public class DashboardScreen extends JPanel {

    static final String DEFAULT_USER_DEFINED_STRING = "new class name...";

    private static int verticalScrollValue;
    private static int horizontalScrollValue;

    private static final long serialVersionUID = -8454775515221613866L;

    // main screen

    protected MainFrame mainClass;
    protected DashboardListener dashboardListener;
    protected JButton addClass = new JButton();
    protected JTextField className = new JTextField();
    protected JPanel[] addGesturePanel;
    protected JButton[] addGestureButtons;
    protected Display display;
    protected JButton checkTemplates;
    protected JScrollPane scrollPane;
    protected JButton deleteAllClasses;
    protected HashMap<String, Box[]> panelsMap = new HashMap<String, Box[]>();
    protected JButton testRecognizer;
    protected JButton featuresButton;
    protected JButton mergeClasses;
    protected JPanel table;

    protected int templatesNum;
    protected int notEmptyClasses;

    private int classesNum;

    private JPanel addClassPanel = new JPanel();
    private JButton editing;
    private JPanel secondRow;
    private JButton classFeatures;
    private final Font fontButtons = new Font("Arial", Font.PLAIN, 16);
    private JPanel main;
    private JPanel commands;
    private JPanel statusBar;

    public DashboardScreen(MainFrame mainClass, boolean thread) {

        this.mainClass = mainClass;
        dashboardListener = new DashboardListener(this, mainClass);

        setLayout(new BorderLayout());
        mainClass.screenMode = MainFrame.MAINSCREEN;
        setBackground(Color.darkGray);

        initComponents(thread);

    }

    static void setScrollValue(int vertical, int horizontal) {
        verticalScrollValue = vertical;
        horizontalScrollValue = horizontal;
    }

    public void initComponents(boolean thread) {

        // top bar in north zone of mainscreen
        add(topBar(), BorderLayout.NORTH);

        // main

        final String[] classes = mainClass.getRecognizer().getClassNames().toArray(new String[0]);

        classesNum = classes.length;

        main = new JPanel(new BorderLayout());
        main.setBackground(Color.darkGray);

        templatesNum = 0;
        notEmptyClasses = 0;

        // COSTRUZIONE TABELLA CLASSI E TEMPLATE

        // per ogni classe
        // panelsMap = new HashMap<String, Box[]>();

        // table for classes and template in center zone of main

        main.add(table(classes, thread), BorderLayout.CENTER);

        // commands in north zone of main
        commands = new JPanel(new FlowLayout(FlowLayout.LEFT));
        commands.setBackground(new Color(28, 28, 28));
        newClassButton();
        classesButtons();
        updateClassesButtons();
        // if (!thread)
        commandsButtons();
        upadateCommandsButtons();

        main.add(commands, BorderLayout.NORTH);

        add(main, BorderLayout.CENTER);
        // status bar on south zone of mainscreen
        statusBar = new JPanel(new GridLayout());
        statusBar.setBackground(new Color(28, 28, 28));
        statusBar.setPreferredSize(new Dimension(getWidth(), 50));
        if (!thread) {
            statusMessage();
        }
        add(statusBar, BorderLayout.SOUTH);

        // mainClass.repaint();

    }

    @Override
    protected void paintComponent(Graphics g) {
        // verticalScrollValue = scrollPane.getVerticalScrollBar().getValue();
        super.paintComponent(g);

        scrollPane.getVerticalScrollBar().setValue(verticalScrollValue);

        scrollPane.getHorizontalScrollBar().setValue(horizontalScrollValue);

    }

    private Box topBar() {
        // pannello TOP
        final Box top = Box.createVerticalBox();

        // titolo
        final JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        firstRow.setBackground(Color.black);
        final JLabel title = new JLabel("<html><font color='white'>Dashboard</font></html>");
        try {

            title.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/list.png"))));
        } catch (final IOException e2) {

            e2.printStackTrace();
        }
        title.setFont(new Font("Arial", Font.PLAIN, 34));
        firstRow.add(title);

        top.add(firstRow);

        // display info
        secondRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        secondRow.setBackground(new Color(145, 220, 90));
        display = new Display();

        secondRow.add(display);
        display.set("Classes ordered by name", 0);
        top.add(secondRow);

        return top;
    }

    private void newClassButton() {
        // coomands

        addClassPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addClassPanel.setPreferredSize(new Dimension(210, 50));

        addClassPanel.setOpaque(false);
        className.setPreferredSize(new Dimension(135, 25));
        className.setText(DEFAULT_USER_DEFINED_STRING);

        className.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent arg0) {

                if (className.getText().equals(DEFAULT_USER_DEFINED_STRING)) {
                    className.setText("");
                }

            }

            @Override
            public void focusLost(FocusEvent arg0) {
                if ("".equals(className.getText())) {
                    className.setText(DEFAULT_USER_DEFINED_STRING);
                }
            }
        });
        className.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addClass.doClick();

            }
        });
        addClassPanel.add(className);
        try {
            final String iPlsW = "/img/plus-white-32.png";
            addClass = new JButton(new ImageIcon(ImageIO.read(getClass().getResource(iPlsW))));
            addClass.setContentAreaFilled(false);
            addClass.setBorderPainted(false);

            addClass.setToolTipText("Add Class with specified name");
            addClass.setCursor(new Cursor(Cursor.HAND_CURSOR));
            addClass.addActionListener(dashboardListener);
            addClass.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    try {
                        ((JButton) evt.getSource())
                                .setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/plus-green-32.png"))));
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    try {
                        ((JButton) evt.getSource()).setIcon(new ImageIcon(ImageIO.read(getClass().getResource(iPlsW))));
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (final IOException e) {

            e.printStackTrace();
        }

        addClassPanel.add(addClass);
        commands.add(addClassPanel);

    }

    void updateClassesButtons() {
        if (classesNum > 1) {
            mergeClasses.setEnabled(true);
            mergeClasses.setToolTipText("Merge Classes");
        } else {
            mergeClasses.setEnabled(false);
            mergeClasses.setToolTipText("Disabled - At least two classes are required");
        }

        if (classesNum > 0) {
            deleteAllClasses.setEnabled(true);
            deleteAllClasses.setToolTipText("Remove all classes and templates");
        } else {
            deleteAllClasses.setEnabled(false);
            deleteAllClasses.setToolTipText("Disabled - Gesture set is empty");
        }

    }

    void classesButtons() {
        final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 30));
        panel.add(separator);
        panel.setOpaque(false);

        // merge classes
        mergeClasses = new JButton("<html><font color='white'>Merge Classes</font></html>");
        mergeClasses.setOpaque(false);
        mergeClasses.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mergeClasses.setFont(fontButtons);
        mergeClasses.setContentAreaFilled(false);
        mergeClasses.setBorderPainted(false);
        try {
            mergeClasses.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/merge.png"))));
        } catch (final IOException e2) {

            e2.printStackTrace();
        }

        mergeClasses.addActionListener(dashboardListener);
        mergeClasses.setEnabled(false);

        panel.add(mergeClasses);

        deleteAllClasses = new JButton("<html><font color='white'>Delete All Classes</font></html>");
        deleteAllClasses.setOpaque(false);
        deleteAllClasses.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteAllClasses.setFont(fontButtons);
        deleteAllClasses.setContentAreaFilled(false);
        deleteAllClasses.setBorderPainted(false);
        try {
            deleteAllClasses.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/error-24-white.png"))));
        } catch (final IOException e2) {

            e2.printStackTrace();
        }

        deleteAllClasses.addActionListener(dashboardListener);
        deleteAllClasses.setEnabled(false);
        panel.add(deleteAllClasses);

        commands.add(panel);

    }

    void upadateCommandsButtons() {
        if (notEmptyClasses > 1) {
            checkTemplates.setEnabled(true);
            checkTemplates.setToolTipText("Check if templates are too similar (based on score settings)");
        } else {
            checkTemplates.setEnabled(false);
            checkTemplates.setToolTipText("Disabled - At least two non-empty classes are required");
        }
        if (templatesNum > 0) {
            featuresButton.setEnabled(true);
            featuresButton.setToolTipText("Features Table");
            testRecognizer.setEnabled(true);
            testRecognizer.setToolTipText("Test Recognizer");
        } else {
            featuresButton.setEnabled(false);
            featuresButton.setToolTipText("Disabled - No template in the gesture set");
            testRecognizer.setEnabled(false);
            testRecognizer.setToolTipText("Disabled - No template in the gesture set");
        }
    }

    @SuppressWarnings("checkstyle:executablestatementcount")
    void commandsButtons() {
        final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.setOpaque(false);

        final JSeparator separator1 = new JSeparator(SwingConstants.VERTICAL);
        separator1.setPreferredSize(new Dimension(1, 30));
        panel.add(separator1);
        // verify templates similarity
        checkTemplates = new JButton("<html><font color='white'>Verify Similarity</font></html>");
        checkTemplates.setOpaque(false);
        checkTemplates.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkTemplates.setFont(fontButtons);
        checkTemplates.setContentAreaFilled(false);
        checkTemplates.setBorderPainted(false);
        try {
            checkTemplates.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/success.png"))));
        } catch (final IOException e2) {

            e2.printStackTrace();
        }

        checkTemplates.addActionListener(dashboardListener);
        checkTemplates.setEnabled(false);
        panel.add(checkTemplates);

        // feature table
        featuresButton = new JButton("<html><font color='white'>Features Table</font></html>");
        featuresButton.setOpaque(false);
        featuresButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        featuresButton.setFont(fontButtons);
        featuresButton.setContentAreaFilled(false);
        featuresButton.setBorderPainted(false);
        try {

            featuresButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/table.png"))));
        } catch (final IOException e2) {

            e2.printStackTrace();
        }

        featuresButton.setEnabled(false);
        featuresButton.addActionListener(dashboardListener);

        panel.add(featuresButton);

        testRecognizer = new JButton("<html><font color='white'>Test Recognizer</font></html>");
        try {

            testRecognizer.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/target.png"))));
        } catch (final IOException e2) {

            e2.printStackTrace();
        }

        testRecognizer.setOpaque(false);
        testRecognizer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        testRecognizer.setFont(fontButtons);
        testRecognizer.setContentAreaFilled(false);
        testRecognizer.setBorderPainted(false);

        testRecognizer.addActionListener(dashboardListener);
        testRecognizer.setEnabled(false);
        panel.add(testRecognizer);

        commands.add(panel);
        // commands.validate();

    }

    void statusMessage() {
        // status bar

        final JLabel statusLabel = new JLabel();

        if (classesNum > 0) {
            statusLabel.setText("<html><font color='white'>" + classesNum + " classes - " + templatesNum
                    + " templates - average number of templates in a class: "
                    + String.format(Locale.US, "%.2f", (float) templatesNum / classesNum) + "</font></html>");
        }

        // else statusLabel.setText("<html><font color='white'>" + classesNum +
        // " classes - " + templatesNum + " templates</font></html>");

        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusLabel.setFont(fontButtons);
        statusBar.add(statusLabel);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

    }

    @SuppressWarnings({"checkstyle:executablestatementcount", "checkstyle:javancss", "checkstyle:methodlength"})
    Box classPanel(String className, int templatesNum) {
        final Box classPanel = Box.createVerticalBox();
        classPanel.setMinimumSize(new Dimension(210, 10));

        final JPanel controlTemplate = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        // controlTemplate.setBorder(new EmptyBorder(5, 5, 5, 5));
        controlTemplate.setBackground(Color.gray);

        try {

            final JButton editClass = new JButton(new ImageIcon(ImageIO.read(getClass().getResource("/img/edit.png"))));
            editClass.setBorder(BorderFactory.createEmptyBorder());
            editClass.setContentAreaFilled(false);
            editClass.setName("editclass_" + className);
            editClass.setAlignmentX(Component.LEFT_ALIGNMENT);
            editClass.setToolTipText("Edit Class Name");
            editClass.setCursor(new Cursor(Cursor.HAND_CURSOR));
            editClass.addActionListener(dashboardListener);

            final String iMulW = "/img/multiply-white-16.png";
            final JButton deleteClass = new JButton(new ImageIcon(ImageIO.read(getClass().getResource(iMulW))));
            deleteClass.setBorder(BorderFactory.createEmptyBorder());
            deleteClass.setContentAreaFilled(false);
            deleteClass.setName("deleteclass_" + className);
            deleteClass.setAlignmentX(Component.LEFT_ALIGNMENT);
            deleteClass.setToolTipText("Delete Class");
            deleteClass.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteClass.addActionListener(dashboardListener);
            deleteClass.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    try {
                        ((JButton) evt.getSource()).setIcon(
                                new ImageIcon(ImageIO.read(getClass().getResource("/img/multiply-red-16.png"))));
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    try {
                        ((JButton) evt.getSource()).setIcon(new ImageIcon(ImageIO.read(getClass().getResource(iMulW))));
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            controlTemplate.add(editClass);
            controlTemplate.add(deleteClass);
            // controlTemplate.add(panelBuottons,BorderLayout.EAST);

            classPanel.add(controlTemplate);
        } catch (final IOException e1) {

            e1.printStackTrace();
        }

        // pannello nome classe
        final JPanel namePanel = new JPanel();

        // label nome classe
        final JTextArea label = new JTextArea();
        label.setSize(210, 200);
        if (templatesNum == 1) {
            label.setText(className.toUpperCase() + "\n(" + templatesNum + " template)");
        }
        label.setText(className.toUpperCase() + "\n(" + templatesNum + " templates)");

        label.setOpaque(false);
        label.setLineWrap(true);
        label.setWrapStyleWord(true);
        label.setForeground(Color.white);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        namePanel.setBackground(Color.lightGray);
        namePanel.add(label);
        classPanel.add(namePanel);

        if (mainClass.getRecognizer().getTemplate(className).size() > 0) {
            JPanel footerPanel = new JPanel();
            footerPanel.setOpaque(false);
            final JButton classRotInv = new JButton(/* "<html><font color='white'>Rotation Invariant</font></html>" */);
            classRotInv.setFont(fontButtons);
            classRotInv.setName("rotinv_" + className);
            classRotInv.setToolTipText("set rotation invariant attribute for this class");
            classRotInv.addActionListener(dashboardListener);
            classRotInv.setCursor(new Cursor(Cursor.HAND_CURSOR));
            classRotInv.setContentAreaFilled(false);
            classRotInv.setBorderPainted(false);

            try {
                classRotInv.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/repeat.png"))));
            } catch (final IOException e2) {

                e2.printStackTrace();
            }
            footerPanel.add(classRotInv);
            classPanel.add(footerPanel);

            // footerPanel = new JPanel();
            // footerPanel.setOpaque(false);
            final JButton classNotRotInv = new JButton();
            /* new JButton(LABEL_START + "Rotation Invariance" + LABEL_END) */
            classNotRotInv.setFont(fontButtons);
            classNotRotInv.setName("notrotinv_" + className);
            classNotRotInv.setToolTipText("unset rotation invariant attribute for this class");
            classNotRotInv.addActionListener(dashboardListener);
            classNotRotInv.setCursor(new Cursor(Cursor.HAND_CURSOR));
            classNotRotInv.setContentAreaFilled(false);
            classNotRotInv.setBorderPainted(false);

            try {
                classNotRotInv.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/notrotinv.png"))));
            } catch (final IOException e2) {

                e2.printStackTrace();
            }

            footerPanel.add(classNotRotInv);
            classPanel.add(footerPanel);
            footerPanel = new JPanel();
            footerPanel.setOpaque(false);

            classFeatures = new JButton("<html><font color='white'>Features</font></html>");
            classFeatures.setFont(fontButtons);
            classFeatures.setName("features_" + className);
            classFeatures.setToolTipText("unset rotation invariant attribute for this class");
            classFeatures.addActionListener(dashboardListener);
            classFeatures.setCursor(new Cursor(Cursor.HAND_CURSOR));
            classFeatures.setContentAreaFilled(false);
            classFeatures.setBorderPainted(false);

            try {
                classFeatures.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/table.png"))));
            } catch (final IOException e2) {

                e2.printStackTrace();
            }

            footerPanel.add(classFeatures);
            classPanel.add(footerPanel);

        }
        if (mainClass.getRecognizer().getTemplate(className).size() > 2) {
            final JPanel footerPanel = new JPanel();
            footerPanel.setOpaque(false);
            editing = new JButton("<html><font color='white'>AutoSelect Templates</font></html>");
            editing.setFont(fontButtons);
            editing.setName("editing_" + className);
            editing.setToolTipText("Selection of rapresentative templates");
            editing.addActionListener(dashboardListener);
            editing.setCursor(new Cursor(Cursor.HAND_CURSOR));
            editing.setContentAreaFilled(false);
            editing.setBorderPainted(false);
            try {
                editing.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/radar.png"))));
            } catch (final IOException e2) {

                e2.printStackTrace();
            }

            footerPanel.add(editing);
            classPanel.add(footerPanel);
        }
        return classPanel;
    }

    @SuppressWarnings({"checkstyle:executablestatementcount", "checkstyle:javancss", "checkstyle:methodlength"})
    Box templatePanel(String className, Polyline template, int templateIndex) {

        final Box templatePanel = Box.createVerticalBox();
        // toolbar.add(templatePanel);

        templatePanel.setBorder(new LineBorder(new Color(0, 0, 0, 0), 2));

        // riga panel delete template
        final JPanel controlTemplate = new JPanel(new BorderLayout());
        // controlTemplate.setBorder(new EmptyBorder(3, 3, 3, 3));

        controlTemplate.setBackground(Color.gray);

        try {
            final JButton detach = new JButton(
                    new ImageIcon(ImageIO.read(getClass().getResource("/img/move-window2.png"))));
            detach.setName("detach_" + className + "_" + templateIndex);
            detach.addActionListener(dashboardListener);
            detach.setBorder(BorderFactory.createEmptyBorder());
            detach.setContentAreaFilled(false);
            detach.setCursor(new Cursor(Cursor.HAND_CURSOR));
            detach.setToolTipText("Detach thumbnail window");

            final JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonsPanel.setOpaque(false);
            final JButton move = new JButton(
                    new ImageIcon(ImageIO.read(getClass().getResource("/img/exit-16-white.png"))));
            move.setBorder(BorderFactory.createEmptyBorder());
            move.setContentAreaFilled(false);
            move.setName("movetemplate_" + className + "_" + templateIndex);
            move.setToolTipText("Move Template to another class");
            move.setCursor(new Cursor(Cursor.HAND_CURSOR));
            move.addActionListener(dashboardListener);

            final JButton delete = new JButton(
                    new ImageIcon(ImageIO.read(getClass().getResource("/img/multiply-white-16.png"))));
            delete.setBorder(BorderFactory.createEmptyBorder());
            delete.setContentAreaFilled(false);
            delete.setName("deletetemplate_" + className + "_" + templateIndex);
            delete.setToolTipText("Delete Template");
            delete.setCursor(new Cursor(Cursor.HAND_CURSOR));
            delete.addActionListener(dashboardListener);
            delete.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    try {
                        delete.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/multiply-red-16.png"))));
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    try {
                        delete.setIcon(
                                new ImageIcon(ImageIO.read(getClass().getResource("/img/multiply-white-16.png"))));
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            buttonsPanel.add(detach);
            buttonsPanel.add(move);
            buttonsPanel.add(delete);

            final JLabel number = new JLabel("<html><font color='white'>&nbsp;" + templateIndex + "</font></html>");
            if (template.getGesture().getInfo() != null) {
                number.setToolTipText(template.getGesture().getInfo().toString());
                number.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            number.setFont(new Font("Arial", Font.PLAIN, 18));
            controlTemplate.add(number, BorderLayout.WEST);
            controlTemplate.add(buttonsPanel, BorderLayout.EAST);

        } catch (final IOException e) {

            e.printStackTrace();
        }

        templatePanel.add(controlTemplate);

        // Runnable r = new MyThread(template.getGesture(), templatePanel, className, this, templateIndex);
        // new Thread(r).start();

        final Thumbnail tempThumbnail = new Thumbnail(template.getGesture());
        tempThumbnail.addMouseListener(dashboardListener);
        tempThumbnail.setName("thumbnail_" + className + "_" + templateIndex);
        tempThumbnail.setToolTipText("Show Template Detail");
        tempThumbnail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tempThumbnail.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {

                if (!MainFrame.isModalDialogShowing()) {
                    templatePanel.setBorder(new LineBorder(Color.lightGray, 2));
                }

            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!MainFrame.isModalDialogShowing()) {
                    templatePanel.setBorder(new LineBorder(new Color(0, 0, 0, 0), 2));
                }

            }
        });

        templatePanel.add(tempThumbnail);

        // panel opzioni (rotinv)
        final JPanel optionPanel = new JPanel(new BorderLayout());
        optionPanel.setBackground(Color.lightGray);
        optionPanel.setPreferredSize(new Dimension(150, 35));

        final JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        final JButton rotInv = new JButton();
        rotInv.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rotInv.setName("rotinv_" + className + "_" + templateIndex);
        rotInv.addActionListener(dashboardListener);
        rotInv.setContentAreaFilled(false);
        rotInv.setBorder(BorderFactory.createEmptyBorder());
        try {
            if (template.getGesture().isRotInv()) {
                rotInv.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/repeat-24-black.png"))));
                rotInv.setToolTipText("Is Rotation Invariant (click to set not RI)");
            } else {
                rotInv.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/notrotinv-black.png"))));
                rotInv.setToolTipText("Is Not Rotation Invariant (click to set RI)");
            }
        } catch (final IOException e2) {

            e2.printStackTrace();
        }
        rightPanel.add(rotInv);
        optionPanel.add(rightPanel, BorderLayout.EAST);

        // System.out.println("NUMERO DI PUNTATORI:"+template.getGesture().getPointers());
        // if (template.getGesture().getPointers() > 1) {
        final JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        leftPanel.setOpaque(false);
        final JButton pointers = new JButton(template.getGesture().getPointers() + "P");
        pointers.setName("pointers_" + className + "_" + templateIndex);
        if (template.getGesture().getPointers() > 1) {
            pointers.setToolTipText(template.getGesture().getPointers() + " pointers");
        } else {
            pointers.setToolTipText(template.getGesture().getPointers() + " pointer");
        }
        pointers.addActionListener(dashboardListener);
        pointers.setContentAreaFilled(false);
        pointers.setFont(fontButtons);
        pointers.setBorder(BorderFactory.createEmptyBorder());
        pointers.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leftPanel.add(pointers);
        optionPanel.add(leftPanel, BorderLayout.WEST);
        // }

        templatePanel.add(optionPanel);

        return templatePanel;
    }

    @SuppressWarnings({"checkstyle:executablestatementcount", "checkstyle:javancss"})
    private JScrollPane table(String[] classes, boolean thread) {

        addGesturePanel = new JPanel[classes.length];
        addGestureButtons = new JButton[classes.length];

        table = new JPanel(new GridBagLayout());
        table.setBackground(Color.DARK_GRAY);

        if (thread) {
            final Runnable r = new TableThread(classes, this);
            new Thread(r).start();
        } else {
            JPanel panel;
            final GridBagConstraints c = new GridBagConstraints();
            final GridBagConstraints last = new GridBagConstraints();
            for (int m = 0; m < classes.length; m++) {
                final ArrayList<Polyline> polylines = mainClass.getRecognizer().getTemplate(classes[m]);

                templatesNum += polylines.size();

                if (polylines.size() > 0) {
                    notEmptyClasses++;
                }

                panel = new JPanel();

                panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
                panel.setOpaque(false);

                c.gridx = 0;
                c.gridy = m;
                c.ipady = 20;
                c.fill = GridBagConstraints.BOTH;
                c.anchor = GridBagConstraints.PAGE_START;
                c.weighty = 1;

                panel.add(classPanel(classes[m], polylines.size()));
                table.add(panel, c);

                panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
                panel.setOpaque(false);

                final Box[] templateBoxes = new Box[polylines.size()];

                for (int p = 0; p < polylines.size(); p++) {

                    templateBoxes[p] = templatePanel(classes[m], polylines.get(p), p);

                    panel.add(templateBoxes[p]);

                }
                System.out.println("già fuori");
                panelsMap.put(classes[m], templateBoxes);

                last.gridy = m;
                last.ipady = 20;
                last.weightx = 1;

                last.fill = GridBagConstraints.BOTH;
                last.gridx = 1;

                addGesturePanel[m] = new JPanel(new GridBagLayout());
                addGesturePanel[m].setPreferredSize(new Dimension(100, 90));
                addGesturePanel[m].setOpaque(false);

                try {

                    addGestureButtons[m] = new JButton(
                            new ImageIcon(ImageIO.read(getClass().getResource("/img/plus-white-32.png"))));
                    addGestureButtons[m].setContentAreaFilled(false);
                    addGestureButtons[m].setBorderPainted(false);

                    addGestureButtons[m].setName("addgesture_" + classes[m]);

                    addGestureButtons[m].setToolTipText("Add Template to " + classes[m] + " Class");
                    addGestureButtons[m].setCursor(new Cursor(Cursor.HAND_CURSOR));

                    @SuppressWarnings("checkstyle:anoninnerlength")
                    final java.awt.event.MouseAdapter mListener = new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseEntered(java.awt.event.MouseEvent evt) {
                            try {
                                ((JButton) evt.getSource()).setIcon(
                                        new ImageIcon(ImageIO.read(getClass().getResource("/img/plus-green-32.png"))));
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void mouseExited(java.awt.event.MouseEvent evt) {
                            try {
                                ((JButton) evt.getSource()).setIcon(
                                        new ImageIcon(ImageIO.read(getClass().getResource("/img/plus-white-32.png"))));
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    addGestureButtons[m].addMouseListener(mListener);
                    addGestureButtons[m].addActionListener(dashboardListener);
                } catch (final IOException e) {

                    e.printStackTrace();
                }

                addGesturePanel[m].add(addGestureButtons[m]);

                // table.add(addGesturePanel[m], c);
                panel.add(addGesturePanel[m]);
                table.add(panel, last);

            }
        }

        scrollPane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        repaint();
        return scrollPane;
    }

}
