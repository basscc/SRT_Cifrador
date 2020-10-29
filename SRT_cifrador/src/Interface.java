import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class Interface extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6163772603012575756L;

	private static final Dimension MIN_SIZE = new Dimension(320, 300);
	private static final Dimension DEFAULT_SIZE = new Dimension(640, 440);

	private JLabel rootLabel;
	private JLabel algoryLabel;
	private JLabel inFileLabel;
	private JLabel outFileLabel;
	private JLabel statusLabel;
	private JTextField rootTextField;
	private JComboBox<String> algoryComboBox;
	private JButton rootButton;
	private JButton acceptButton;
	private JScrollPane filePane;
	private JScrollPane resultsPane;
	private JTextArea previewFileArea;
	private JTextArea cipherFileArea;

	private File rootPath;

	public Interface() {
		initComponents();
		initLayout();
		finishGui();
	}

	private void initComponents() {

		String[] algorythms = { "SHA", "AES", "DES", "3DES" };

		rootLabel = new JLabel();
		algoryLabel = new JLabel();
		inFileLabel = new JLabel();
		outFileLabel = new JLabel();
		statusLabel = new JLabel();

		rootTextField = new JTextField();
		algoryComboBox = new JComboBox<String>(algorythms);

		rootButton = new JButton();
		acceptButton = new JButton();

		previewFileArea = new JTextArea(6, 50);
		cipherFileArea = new JTextArea(6, 50);
		filePane = new JScrollPane(previewFileArea);
		resultsPane = new JScrollPane(cipherFileArea);

		rootLabel.setText("Ruta de fichero:");
		algoryLabel.setText("Algoritmo:");
		inFileLabel.setText("Texto del fichero sin cifrar");
		outFileLabel.setText("Resultado del cifrado");
		rootButton.setText("…");
		acceptButton.setText("Aceptar");

		rootTextField.setEditable(false);
		algoryComboBox.setEditable(false);
		previewFileArea.setEditable(false);
		cipherFileArea.setEditable(false);
		rootButton.setFocusable(false);
		acceptButton.setFocusable(false);

		rootButton.addActionListener(e -> {
			try {
				selectRoot(e);
			} catch (IOException noFile) {
				noFile.printStackTrace();
			}
		});

		acceptButton.addActionListener(this::startEncryption);
	}

	private void initLayout() {
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup().addComponent(rootLabel)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(rootTextField)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(rootButton))
						.addGroup(layout.createSequentialGroup().addComponent(algoryLabel)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(algoryComboBox)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(acceptButton))
						.addComponent(inFileLabel).addComponent(filePane).addComponent(outFileLabel)
						.addComponent(resultsPane).addComponent(statusLabel))
				.addContainerGap());

		layout.setVerticalGroup(layout.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup().addComponent(rootLabel).addComponent(rootTextField)
						.addComponent(rootButton))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup().addComponent(algoryLabel).addComponent(algoryComboBox))
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(inFileLabel)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(filePane)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(outFileLabel)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(resultsPane)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(acceptButton)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(statusLabel).addContainerGap());

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

	private void selectRoot(ActionEvent event) throws IOException {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			statusLabel.setText("Fichero seleccionado.");
			rootPath = chooser.getSelectedFile();
			rootTextField.setText(rootPath.getAbsolutePath());
			previewFile(rootPath.getAbsolutePath());
		}
	}

	private void previewFile(String fileRoot) throws IOException {

		FileReader fileReader = new FileReader(fileRoot);
		char[] display = new char[300];
		fileReader.read(display, 0, 300);

		previewFileArea.setText("\\\\ Mostrando los primeros 300 caracteres del fichero: \n\n");
		previewFileArea.append(String.valueOf(display));

		fileReader.close();
	}

	private void startEncryption(ActionEvent event) {

		if (rootPath != null) {
			statusLabel.setText("Cifrando con algoritmo : " + algoryComboBox.getSelectedItem().toString());
			// algoryComboBox.getSelectedIndex();
		} else {
			statusLabel.setText("ERROR : No se ha seleccionado ningún fichero");
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
