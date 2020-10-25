import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class Interface extends JFrame{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -6163772603012575756L;
	
	private static final Dimension MIN_SIZE = new Dimension(320, 200);
    private static final Dimension DEFAULT_SIZE = new Dimension(640, 320);
	
    private JLabel rootLabel;
    private JLabel algoryLabel;
    private JLabel searchLabel;
    private JLabel statusLabel;
    private JTextField rootTextField;
    private JTextField searchTextField;
    private JComboBox<String> algoryComboBox;
    private JButton rootButton;
    private JButton acceptButton;
    private JScrollPane resultsPane;
    
    private File rootPath;
	
	public Interface() {
        initComponents();
        initLayout();
        finishGui();
    }
	
	private void initComponents() {
		
		String[] algorythms = {
				"SHA",
				"AES",
				"DES",
				"3DES"
		};
		
        rootLabel = new JLabel();
        algoryLabel = new JLabel();
        searchLabel = new JLabel();
        statusLabel = new JLabel();
        
        rootTextField = new JTextField();
        algoryComboBox = new JComboBox<String>(algorythms);
        searchTextField = new JTextField();
        
        rootButton = new JButton();
        acceptButton = new JButton();
        
        JTable resultsTable = new JTable();
        resultsPane = new JScrollPane(resultsTable);

        rootLabel.setText("Ruta de fichero:");
        algoryLabel.setText("Algoritmo:");
        searchLabel.setText("Search:");
        rootButton.setText("…");
        acceptButton.setText("Aceptar");

        rootTextField.setEditable(false);
        algoryComboBox.setEditable(false);
        rootButton.setFocusable(false);
        acceptButton.setFocusable(false);
        
        rootButton.addActionListener(this::selectRoot);
	}
	
	private void initLayout() {
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(rootLabel)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(rootTextField)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(rootButton)
                        )
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(algoryLabel)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(algoryComboBox)
                        )
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(searchLabel)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(searchTextField)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(acceptButton)
                        )
                        .addComponent(resultsPane)                  
                        .addComponent(statusLabel)
                )
                .addContainerGap()
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup()
                        .addComponent(rootLabel)
                        .addComponent(rootTextField)
                        .addComponent(rootButton)
                )
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup()
                        .addComponent(algoryLabel)
                        .addComponent(algoryComboBox)
                )
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup()
                        .addComponent(searchLabel)
                        .addComponent(searchTextField)
                )
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(resultsPane)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(acceptButton)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(statusLabel)
                .addContainerGap()
        );
        
        layout.linkSize(SwingConstants.HORIZONTAL, rootLabel, algoryLabel);    
    }
	
	private void finishGui() {
        pack();
        setTitle("Trabajo SRT");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(MIN_SIZE);
        setSize(DEFAULT_SIZE);

        setVisible(true);
        updateStatus("Preparado para encriptar.");
    }
	
	private void selectRoot(ActionEvent event) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            statusLabel.setText("Fichero seleccionado.");
            rootPath = chooser.getSelectedFile();
            rootTextField.setText(rootPath.getAbsolutePath());
        }
    }
	
	private void updateStatus(String status, Object... args) {
        String formatted = String.format(status, args);
        statusLabel.setText(formatted);
        statusLabel.setToolTipText(formatted);
    }
	
	public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(Interface::new);
    }

}
