package gui.encrypt_decrypt;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import functions.Cypher;
import gui.MainMenu;

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

public class DecryptionUI extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7155697168794874224L;

	private static final Dimension MIN_SIZE = new Dimension(320, 330);
	private static final Dimension DEFAULT_SIZE = new Dimension(640, 470);

	MainMenu parentUI;
	Cypher cypher;
	
	private Boolean opSuccessfull; // bool to determine if an operation was sucessfully executed

	private JLabel rootLabel;
	private JLabel inFileLabel;
	private JLabel outFileLabel;
	private JLabel statusLabel;
	private JLabel pwLabel;
	private JTextField rootTextField;
	private JPasswordField passwordField;
	private JButton rootButton;
	private JButton acceptButton;
	private JButton backButton;

	private JScrollPane filePane;
	private JScrollPane resultsPane;
	private JTextArea previewFileArea;
	private JTextArea cipherFileArea;

	private File rootPath;

	public DecryptionUI(MainMenu parentUI) {
		this.parentUI = parentUI; // Get the instance of the parentUI to be able to return to the previous window
		this.setModalityType(ModalityType.APPLICATION_MODAL); // Make lower level windows to have blocked inputs 
		initComponents();
		initLayout();
		finishGui();
	}

	/*
	 * Initiate GUI components
	 */
	private void initComponents() {

		cypher = new Cypher();
		
		opSuccessfull = false;

		rootLabel = new JLabel();
		inFileLabel = new JLabel();
		outFileLabel = new JLabel();
		statusLabel = new JLabel();
		pwLabel = new JLabel();

		rootTextField = new JTextField();
		passwordField = new JPasswordField();

		rootButton = new JButton();
		acceptButton = new JButton();
		backButton = new JButton();

		previewFileArea = new JTextArea(6, 50);
		cipherFileArea = new JTextArea(6, 50);
		filePane = new JScrollPane(previewFileArea);
		resultsPane = new JScrollPane(cipherFileArea);

		rootLabel.setText("Ruta de fichero:");
		inFileLabel.setText("Texto del fichero cifrado");
		outFileLabel.setText("Resultado del fichero descifrado");
		rootButton.setText("…");
		acceptButton.setText("Desencriptar");
		backButton.setText("Volver");
		pwLabel.setText("Contraseña:");

		rootTextField.setEditable(false);
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

		acceptButton.addActionListener(this::startDecryption);
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
						.addGroup(layout.createSequentialGroup().addComponent(pwLabel)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(passwordField)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(acceptButton))
						.addComponent(inFileLabel).addComponent(filePane).addComponent(outFileLabel)
						.addComponent(resultsPane).addComponent(backButton).addComponent(statusLabel))
				.addContainerGap());

		// Vertical groups
		layout.setVerticalGroup(layout.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup().addComponent(rootLabel).addComponent(rootTextField)
						.addComponent(rootButton))
				.addPreferredGap(ComponentPlacement.RELATED).addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup().addComponent(pwLabel).addComponent(passwordField))
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(inFileLabel)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(filePane)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(outFileLabel)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(resultsPane)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup().addComponent(acceptButton).addComponent(backButton))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(statusLabel).addContainerGap());

		// Link size of labels
		layout.linkSize(SwingConstants.HORIZONTAL, rootLabel, pwLabel);
	}

	/*
	 * Set the last parameters of the main window
	 */
	private void finishGui() {
		pack();
		setTitle("Cifrador 2020 SRT - Desencriptar fichero");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(MIN_SIZE);
		setSize(DEFAULT_SIZE);
		setLocationRelativeTo(null); // center the window on screen

		updateStatus("Preparado para verificar.");
		setVisible(true);	
	}

	/*
	 * Method to dispose of this window and go back to the parent UI
	 */
	private void goBackUI(ActionEvent event) {

		parentUI.setVisible(true); // Make the main menu visible again
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
		previewFileArea.setCaretPosition(0); // Scroll back to the top

		fileReader.close();
	}

	/*
	 * Method to read the characters from the OUTPUT file after the encryption and
	 * display them in the corresponding area
	 */
	private void previewDecryption() throws IOException {

		FileReader fileReader = new FileReader(
				rootPath.getAbsolutePath().substring(0, rootPath.getAbsolutePath().length() - 4));
		char[] display = new char[300];
		fileReader.read(display, 0, 300);

		cipherFileArea.setText(String.valueOf(display));
		cipherFileArea.setCaretPosition(0); // Scroll back to the top

		fileReader.close();
	}

	/*
	 * Method to initiate the deciphering process once the "accept" button is clicked
	 */
	private void startDecryption(ActionEvent event) {

		if (rootPath != null) {
			if (passwordField.getPassword().length != 0) {
				updateStatus("Verificando archivo");
				
				opSuccessfull = true;

				try {
					cypher.decipherFile(rootPath, String.valueOf(passwordField.getPassword()));
					previewDecryption();
				} catch (Exception e) {
					e.printStackTrace();
					opSuccessfull = false;
				}
				
				if(opSuccessfull) { // If the file could be decrypted
					
					try {
						previewDecryption(); // Show the plain text
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					JOptionPane.showMessageDialog(this, "El fichero ha sido descifrado."); // Tell the user
					updateStatus("Fichero descifrado correctamente.");
				} else {
					JOptionPane.showMessageDialog(this, "Se ha producido un error al descifrar.", "ERROR", JOptionPane.ERROR_MESSAGE);
					updateStatus("ERROR : Se ha producido un error al descifrar.");
				}
			} else {
				JOptionPane.showMessageDialog(this, "No se ha insertado ninguna contraseña.", "ERROR", JOptionPane.ERROR_MESSAGE);
				updateStatus("ERROR : No se ha insertado ninguna contraseña.");
			}
		} else {
			JOptionPane.showMessageDialog(this, "No se ha seleccionado ningún fichero.", "ERROR", JOptionPane.ERROR_MESSAGE);
			updateStatus("ERROR : No se ha seleccionado ningún fichero.");
		}
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
