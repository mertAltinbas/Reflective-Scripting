import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Element;

class ScriptEditorDialog extends JDialog {
    private JTextArea scriptTextArea;
    private boolean approved = false;
    private String script = "";

    public ScriptEditorDialog(JFrame parent) {
        super(parent, "Script Editor", true);
        initializeComponents();
        setupLayout();
        setupListeners();
        setSize(600, 400);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        scriptTextArea = new JTextArea();
        scriptTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        scriptTextArea.setTabSize(2);
        scriptTextArea.setLineWrap(true);
        scriptTextArea.setWrapStyleWord(true);

        JTextArea lineNumbers = new JTextArea("1");
        lineNumbers.setBackground(new Color(240, 240, 240));
        lineNumbers.setEditable(false);
        lineNumbers.setFont(new Font("Monospaced", Font.PLAIN, 14));
        lineNumbers.setBorder(new EmptyBorder(0, 5, 0, 5));

        scriptTextArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public String getText() {
                int caretPosition = scriptTextArea.getDocument().getLength();
                Element root = scriptTextArea.getDocument().getDefaultRootElement();
                StringBuilder text = new StringBuilder("1" + System.getProperty("line.separator"));
                for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
                    text.append(i).append(System.getProperty("line.separator"));
                }
                return text.toString();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                lineNumbers.setText(getText());
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                lineNumbers.setText(getText());
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                lineNumbers.setText(getText());
            }
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(scriptTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(e -> {
            approved = true;
            script = scriptTextArea.getText();
            dispose();
        });

        cancelButton.addActionListener(e -> {
            approved = false;
            dispose();
        });
    }


    private void setupListeners() {
        scriptTextArea.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "insertTab");
        scriptTextArea.getActionMap().put("insertTab", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                scriptTextArea.replaceSelection("  ");
            }
        });
    }

    public boolean isApproved() {
        return approved;
    }

    public String getScript() {
        return script;
    }
}