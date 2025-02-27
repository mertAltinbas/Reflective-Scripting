import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class GUI {
    private static final String MODELS_PACKAGE = "src\\models";
    private static final String DATA_DIRECTORY = "src\\datas";

    private JFrame frame;
    private JPanel mainPanel;
    private JScrollPane rightPanel;
    private JPanel leftPanel;
    private JList<String> modelList;
    private JList<String> dataList;
    private JButton runModelButton, runScButton, createRunButton;
    private JTable resultTable;
    private Controller controller;

    private JLabel modelLabel, dataLabel;
    private DefaultListModel<String> modelListModel;
    private DefaultListModel<String> dataListModel;

    public void createUI() {
        frame = new JFrame("ReflectiveScripting");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(800, 400);

        initializeComponents();
        setupLayout();
        setupListeners();

        frame.setVisible(true);
    }

    private void initializeComponents() {
        modelListModel = new DefaultListModel<>();
        dataListModel = new DefaultListModel<>();

        modelList = new JList<>(modelListModel);
        dataList = new JList<>(dataListModel);

        modelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        modelList.setCellRenderer(new StyledListCellRenderer());
        dataList.setCellRenderer(new StyledListCellRenderer());

        populateModelList();
        populateDataList();

        modelLabel = new JLabel("Selected Model: None");
        dataLabel = new JLabel("Selected Data: None");

        runModelButton = new JButton("Run Model");
        runModelButton.setEnabled(false);
        runScButton = new JButton("Run Script From File");
        createRunButton = new JButton("Create and Run Script");

        resultTable = new JTable();
        rightPanel = new JScrollPane(resultTable);
    }

    private void populateModelList() {
        modelListModel.clear();
        File dataDir = new File(MODELS_PACKAGE);
        if (dataDir.exists() && dataDir.isDirectory()) {
            File[] files = dataDir.listFiles((dir, name) -> name.endsWith(".java"));
            if (files != null) {
                for (File file : files) {
                    modelListModel.addElement(file.getName());
                }
            }
        }
    }

    private void populateDataList() {
        dataListModel.clear();
        File dataDir = new File(DATA_DIRECTORY);
        if (dataDir.exists() && dataDir.isDirectory()) {
            File[] files = dataDir.listFiles((dir, name) -> name.endsWith(".txt"));
            if (files != null) {
                for (File file : files) {
                    dataListModel.addElement(file.getName());
                }
            }
        }
    }

    private void setupLayout() {
        leftPanel = new JPanel(new BorderLayout());

        // Status panel
        JPanel statusPanel = new JPanel(new GridLayout(2, 1));
        statusPanel.add(modelLabel);
        statusPanel.add(dataLabel);

        // Lists panel
        JPanel listsPanel = new JPanel(new GridLayout(2, 1));

        // Model list with title
        JPanel modelPanel = new JPanel(new BorderLayout());
        modelPanel.setBorder(BorderFactory.createTitledBorder("models"));
        modelPanel.add(new JScrollPane(modelList));

        // Data list with title
        JPanel dataPanel = new JPanel(new BorderLayout());
        dataPanel.setBorder(BorderFactory.createTitledBorder("Data Files"));
        dataPanel.add(new JScrollPane(dataList));

        listsPanel.add(modelPanel);
        listsPanel.add(dataPanel);

        leftPanel.add(statusPanel, BorderLayout.NORTH);
        leftPanel.add(listsPanel, BorderLayout.CENTER);
        leftPanel.add(runModelButton, BorderLayout.SOUTH);

        // Right panel setup
        JPanel rightPanelContainer = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(runScButton);
        buttonPanel.add(createRunButton);

        rightPanelContainer.add(rightPanel, BorderLayout.CENTER);
        rightPanelContainer.add(buttonPanel, BorderLayout.SOUTH);

        // Main panel setup
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanelContainer, BorderLayout.CENTER);

        frame.add(mainPanel);
    }

    private void setupListeners() {
        modelList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedModel = modelList.getSelectedValue();
                modelLabel.setText("Selected Model: " + (selectedModel != null ? selectedModel : "None"));
                checkRunModelButton();
            }
        });

        dataList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedData = dataList.getSelectedValue();
                dataLabel.setText("Selected Data: " + (selectedData != null ? selectedData : "None"));
                checkRunModelButton();
            }
        });

        setupButtonActions();
    }

    private void checkRunModelButton() {
        runModelButton.setEnabled(modelList.getSelectedValue() != null && dataList.getSelectedValue() != null);
    }

    private void setupButtonActions() {
        runModelButton.addActionListener(e -> {
            String selectedModel = modelList.getSelectedValue();
            String selectedData = dataList.getSelectedValue();

            if (selectedModel != null && selectedData != null) {
                try {
                    controller = new Controller(selectedModel);
                    controller.readDataFrom(DATA_DIRECTORY + File.separator + selectedData).runModel();
                    updateTable(controller.getResultsAsTsv());
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(frame, "Error initializing model: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        runScButton.addActionListener(e -> {
            if (controller == null) {
                JOptionPane.showMessageDialog(frame, "Please run a model first.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select a Groovy Script");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Groovy Scripts", "groovy"));

            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    double[] scriptResults = controller.runScriptFile(selectedFile.getAbsolutePath());
                    updateTable(controller.getResultsAsTsv());
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(frame, "Error running script: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        createRunButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (controller == null) {
                    JOptionPane.showMessageDialog(frame, "Please run a model first.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                ScriptEditorDialog dialog = new ScriptEditorDialog(frame);
                dialog.setVisible(true);

                if (dialog.isApproved()) {
                    String script = dialog.getScript();
                    if (!script.isEmpty()) {
                        controller.runScript(script);
                        updateTable(controller.getResultsAsTsv());
                    }
                }
            }
        });
    }

    private void updateTable(String results) {
        String[] rows = results.split("\n");
        String[] columnNames = rows[0].split("\t");
        String[][] data = new String[rows.length - 1][columnNames.length];

        for (int i = 1; i < rows.length; i++) {
            data[i - 1] = rows[i].split("\t");
        }

        resultTable.setModel(new DefaultTableModel(data, columnNames));

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int i = 1; i < resultTable.getColumnCount(); i++) {
            resultTable.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI();
            gui.createUI();
        });
    }
}

