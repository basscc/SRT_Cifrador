package gui.hashing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
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

import functions.Hash;
import gui.MainMenu;

import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
/*
 * Developed by:
 * 
 * Carlos Salguero S�nchez
 * Javier Tovar Pacheco
 * 
 * UNEX - 2020 - SRT
 */

public class VerifyHashUI extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7155697168794874224L;

	private static final Dimension MIN_SIZE = new Dimension(300, 280);
	private static final Dimension DEFAULT_SIZE = new Dimension(500, 320);

	MainMenu parentUI;
	Hash hash;

	private Boolean opSuccessfull; // bool to determine if an operation was sucessfully executed

	private JLabel rootLabel;
	private JLabel hashLabel;
	private JLabel statusLabel;
	private JLabel pwLabel;
	private JTextField rootTextField;
	private JPasswordField passwordField;
	private JButton rootButton;
	private JButton acceptButton;
	private JButton backButton;

	private JScrollPane hashPane;
	private JTextArea hashResultArea;

	private File rootPath;

	public VerifyHashUI(MainMenu parentUI) {
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

		hash = new Hash();

		opSuccessfull = false;

		rootLabel = new JLabel();
		hashLabel = new JLabel();
		statusLabel = new JLabel();
		pwLabel = new JLabel();

		rootTextField = new JTextField();
		passwordField = new JPasswordField();

		rootButton = new JButton();
		acceptButton = new JButton();
		backButton = new JButton();

		hashResultArea = new JTextArea(6, 50);
		hashPane = new JScrollPane(hashResultArea);

		rootLabel.setText("Ruta de fichero:");
		hashLabel.setText("Resultado");
		rootButton.setText("�");
		acceptButton.setText("Verificar");
		backButton.setText("Volver");
		pwLabel.setText("Contrase�a:");

		rootTextField.setEditable(false);
		hashResultArea.setEditable(false);
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

		acceptButton.addActionListener(this::startVerifying);
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
						.addComponent(hashLabel).addComponent(hashPane).addComponent(backButton)
						.addComponent(statusLabel))
				.addContainerGap());

		// Vertical groups
		layout.setVerticalGroup(layout.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup().addComponent(rootLabel).addComponent(rootTextField)
						.addComponent(rootButton))
				.addPreferredGap(ComponentPlacement.RELATED).addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup().addComponent(pwLabel).addComponent(passwordField))
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(hashLabel)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(hashPane)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup().addComponent(acceptButton).addComponent(backButton))
				.addPreferredGap(ComponentPlacement.RELATED).addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(statusLabel).addContainerGap());

		// Link size of labels
		layout.linkSize(SwingConstants.HORIZONTAL, rootLabel, pwLabel);
	}

	/*
	 * Set the last parameters of the main window
	 */
	private void finishGui() {
		pack();
		setTitle("Cifrador 2020 SRT - Verificaci�n de Hash");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(MIN_SIZE);
		setSize(DEFAULT_SIZE);
		setLocationRelativeTo(null); // center the window on screen

		updateStatus("Preparado para verificar un hash.");
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
		}
	}

	/*
	 * Method to show the result of the hash verification in the UI 
	 * This includes: 
	 * Hash stored in the header 
	 * Hash of the current message 
	 * The longitude of the message in bytes
	 */
	private void previewHashVerification() {

		StringBuffer sCalculated = new StringBuffer();
		StringBuffer sStored = new StringBuffer();

		// Get the byte array
		String auxCalculated = new String(hash.getCalculatedHash());
		String auxStored = new String(hash.getStoredHash());

		// Transform into char array
		char ch[] = auxCalculated.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			String hexString = Integer.toHexString(ch[i]); // Convert to HEX
			sCalculated.append(hexString);
		}

		// Do the same for the other array
		char cj[] = auxStored.toCharArray();
		for (int i = 0; i < cj.length; i++) {
			String hexString = Integer.toHexString(cj[i]);
			sStored.append(hexString);
		}

		String resultCalc = sCalculated.toString(); // Transform into String again
		String resultStored = sStored.toString();

		hashResultArea.setText("Hash calculado:\t" + resultCalc + "\nHash almacenado:\t" + resultStored + "\n\nHecho: "
				+ Integer.toString(hash.getMessageBytes()) + " bytes"); // Show the String in the UI
		hashResultArea.setCaretPosition(0); // Scroll back to the top
	}

	/*
	 * Method to start the process of Hash verification 
	 */
	private void startVerifying(ActionEvent event) {

		if (rootPath != null) {
			if (passwordField.getPassword().length != 0) {
				updateStatus("Verificando archivo");

				opSuccessfull = true;

				try {
					hash.verifyHash(rootPath, String.valueOf(passwordField.getPassword()));
				} catch (Exception e) {
					e.printStackTrace();
					opSuccessfull = false;
				}

				if (opSuccessfull) { // If the file could be verified
					
					previewHashVerification(); // Show the result in the UI
					
					if (hash.isVerified()) { // If the hash matches
						JOptionPane.showMessageDialog(this, "CORRECTO : El hash calculado concuerda con el fichero."); // Tell the user															// user
						updateStatus("CORRECTO : Fichero verificado correctamente.");
					} else { // If the hash is different
						JOptionPane.showMessageDialog(this, "El hash del fichero no concuerda.", "ADVERTENCIA", JOptionPane.WARNING_MESSAGE);
						updateStatus("AVISO : Hash incorrecto.");
					}
				} else {
					JOptionPane.showMessageDialog(this, "La verificaci�n del fichero ha fallado.", "ERROR", JOptionPane.ERROR_MESSAGE);
					updateStatus("ERROR : La verificaci�n del fichero ha fallado.");
				}

			} else {
				JOptionPane.showMessageDialog(this, "No se ha insertado ninguna contrase�a.", "ERROR", JOptionPane.ERROR_MESSAGE);
				updateStatus("ERROR : No se ha insertado ninguna contrase�a.");
			}
		} else {
			JOptionPane.showMessageDialog(this, "No se ha seleccionado ning�n fichero.", "ERROR", JOptionPane.ERROR_MESSAGE);
			updateStatus("ERROR : No se ha seleccionado ning�n fichero.");
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
