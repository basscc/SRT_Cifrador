package gui;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import functions.Cypher;

import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
/*
 * Developed by:
 * 
 * Carlos Salguero Sánchez
 * Javier Tovar Pacheco
 * 
 * UNEX - 2020 - SRT
 */

public class EncryptionUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6163772603012575756L;

	private static final Dimension MIN_SIZE = new Dimension(320, 330);
	private static final Dimension DEFAULT_SIZE = new Dimension(640, 470);

	MainMenu parentUI;
	Cypher cypher;

	private JLabel rootLabel;
	private JLabel algoryLabel;
	private JLabel inFileLabel;
	private JLabel outFileLabel;
	private JLabel statusLabel;
	private JLabel pwLabel;
	private JTextField rootTextField;
	private JPasswordField passwordField;
	private JComboBox<String> algoryComboBox;
	private JButton rootButton;
	private JButton acceptButton;
	private JButton backButton;

	private JScrollPane filePane;
	private JScrollPane resultsPane;
	private JTextArea previewFileArea;
	private JTextArea cipherFileArea;

	private File rootPath;

	public EncryptionUI(MainMenu parentUI) {
		this.parentUI = parentUI; // Get the instance of the parentUI to be able to return to the previous window
		initComponents();
		initLayout();
		finishGui();
	}

	/*
	 * Initiate GUI components
	 */
	private void initComponents() {

		String[] algorythms = { "SHA", "DES", "3DES" };
		cypher = new Cypher();

		rootLabel = new JLabel();
		algoryLabel = new JLabel();
		inFileLabel = new JLabel();
		outFileLabel = new JLabel();
		statusLabel = new JLabel();
		pwLabel = new JLabel();

		rootTextField = new JTextField();
		passwordField = new JPasswordField();

		algoryComboBox = new JComboBox<String>(algorythms);

		rootButton = new JButton();
		acceptButton = new JButton();
		backButton = new JButton();

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
		backButton.setText("Volver");
		pwLabel.setText("Contraseña:");

		rootTextField.setEditable(false);
		algoryComboBox.setEditable(false);
		previewFileArea.setEditable(false);
		cipherFileArea.setEditable(false);
		// Remove the ugly text boundary box when clicking the button
		rootButton.setFocusable(false);
		acceptButton.setFocusable(false);
		backButton.setFocusable(false);

		passwordField.setEchoChar('*'); // Type * as the user writes in the component

		rootButton.addActionListener(e -> {
			try {
				selectRoot(e);
			} catch (IOException noFile) {
				noFile.printStackTrace();
			}
		});

		acceptButton.addActionListener(this::startEncryption);
		backButton.addActionListener(this::goBackUI);
	}

	/*
	 * Initiate the layout for the JFrame window
	 */
	private void initLayout() {
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		// Horizontal groups
		layout.setHorizontalGroup(layout.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup().addComponent(rootLabel)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(rootTextField)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(rootButton))
						.addGroup(layout.createSequentialGroup().addComponent(algoryLabel)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(algoryComboBox)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(acceptButton))
						.addGroup(layout.createSequentialGroup().addComponent(pwLabel)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(passwordField))
						.addComponent(inFileLabel).addComponent(filePane).addComponent(outFileLabel)
						.addComponent(resultsPane).addComponent(backButton).addComponent(statusLabel))
				.addContainerGap());

		// Vertical groups
		layout.setVerticalGroup(layout.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup().addComponent(rootLabel).addComponent(rootTextField)
						.addComponent(rootButton))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup().addComponent(algoryLabel).addComponent(algoryComboBox))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup().addComponent(pwLabel).addComponent(passwordField))
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(inFileLabel)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(filePane)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(outFileLabel)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(resultsPane)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup().addComponent(acceptButton).addComponent(backButton))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(statusLabel).addContainerGap());

		// Link size of labels
		layout.linkSize(SwingConstants.HORIZONTAL, rootLabel, algoryLabel, pwLabel);
	}

	/*
	 * Set the last parameters of this window
	 */
	private void finishGui() {
		pack();
		setTitle("Cifrador 2020 SRT - Encriptando");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setMinimumSize(MIN_SIZE);
		setSize(DEFAULT_SIZE);

		setVisible(true);
		updateStatus("Preparado para encriptar.");
	}

	/*
	 * Method to dispose of this window and go back to the parent UI
	 */
	private void goBackUI(ActionEvent event) {

		parentUI.setVisible(true); // Make the main menu visible again
		setVisible(false); // Hide this window
		dispose(); // Remove this window
	}

	/*
	 * File chooser
	 */
	private void selectRoot(ActionEvent event) throws IOException {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			updateStatus("Fichero seleccionado.");
			rootPath = chooser.getSelectedFile();
			rootTextField.setText(rootPath.getAbsolutePath()); // Display file path once chosen
			previewFile(rootPath.getAbsolutePath()); // Show file content on preview zone
		}
	}

	/*
	 * Method to read the characters from the IN file and display them in the
	 * corresponding area
	 */
	private void previewFile(String fileRoot) throws IOException {

		FileReader fileReader = new FileReader(fileRoot);
		char[] display = new char[300];
		fileReader.read(display, 0, 300);

		previewFileArea.setText("\\\\ Mostrando los primeros 300 caracteres del fichero: \n\n");
		previewFileArea.append(String.valueOf(display));

		fileReader.close();
	}

	/*
	 * Method to read the characters from the OUTPUT file after the encryption and
	 * display them in the corresponding area
	 */
	private void previewEncryption() throws IOException {

		FileReader fileReader = new FileReader(rootPath.getAbsolutePath() + ".cif");
		char[] display = new char[300];
		fileReader.read(display, 0, 300);

		cipherFileArea.setText(String.valueOf(display));

		fileReader.close();
	}

	/*
	 * Method to initiate the cyphering process once the "accept" button is clicked
	 */
	private void startEncryption(ActionEvent event) {

		// If no file nor password is provided, then do nothing

		if (rootPath != null) {
			if (passwordField.getPassword().length != 0) {
				updateStatus("Cifrando con algoritmo : " + algoryComboBox.getSelectedItem().toString());

				try {
					cypher.cipherFile(rootPath, parseAlgoChosen(algoryComboBox.getSelectedIndex()),
							String.valueOf(passwordField.getPassword()));
					previewEncryption();
				} catch (Exception e) {
					e.printStackTrace();
				}

				statusLabel.setText("Fichero cifrado correctamente.");
				// TODO: mostrar mensaje de fichero cifrado correctamente ventana emergente con
				// boton aceptar
			} else {
				updateStatus("ERROR : No se ha insertado ninguna contraseña.");
			}
		} else {
			updateStatus("ERROR : No se ha seleccionado ningún fichero.");
		}
	}

	/*
	 * Method to translate the drop down list into the exact algorithm name for
	 * later calls
	 */
	private String parseAlgoChosen(int op) {

		String chosen;

		switch (op) {

		case 0:
			chosen = "PBEWithSHA1AndDESede";
			break;

		case 1:
			chosen = "PBEWithMD5AndDES";
			break;

		case 2:
			chosen = "PBEWithMD5AndTripleDES";
			break;

		default:
			chosen = "PBEWithMD5AndDES";
			break;
		}

		return chosen;
	}

	/*
	 * Update the status label at the bottom
	 */
	private void updateStatus(String status, Object... args) {
		String formatted = String.format(status, args);
		statusLabel.setText(formatted);
		statusLabel.setToolTipText(formatted);
	}
}
