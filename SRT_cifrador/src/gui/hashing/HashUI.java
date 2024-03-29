package gui.hashing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

public class HashUI extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7155697168794874224L;

	private static final Dimension MIN_SIZE = new Dimension(350, 290);
	private static final Dimension DEFAULT_SIZE = new Dimension(550, 340);

	MainMenu parentUI;
	Hash hash;

	private Boolean opSuccessfull; // bool to determine if an operation was sucessfully executed

	private JLabel rootLabel;
	private JLabel hashLabel;
	private JLabel statusLabel;
	private JLabel pwLabel;
	private JLabel hashResultLabel;
	private JTextField rootTextField;
	private JPasswordField passwordField;
	private JComboBox<String> hashComboBox;
	private JButton rootButton;
	private JButton acceptButton;
	private JButton backButton;

	private JScrollPane hashPane;
	private JTextArea hashResultArea;

	private File rootPath;

	public HashUI(MainMenu parentUI) {
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
		String[] hashes = { "MD2", "MD5", "SHA-1", "SHA-256", "SHA-384", "SHA-512", "Hmac MD5", "Hmac SHA1", "Hmac SHA256", "Hmac SHA384", "Hmac SHA512"};

		opSuccessfull = false;

		rootLabel = new JLabel();
		hashLabel = new JLabel();
		statusLabel = new JLabel();
		pwLabel = new JLabel();
		hashResultLabel = new JLabel();

		rootTextField = new JTextField();
		passwordField = new JPasswordField();

		hashComboBox = new JComboBox<String>(hashes);

		rootButton = new JButton();
		acceptButton = new JButton();
		backButton = new JButton();

		hashResultArea = new JTextArea(6, 50);
		hashPane = new JScrollPane(hashResultArea);

		rootLabel.setText("Ruta de fichero:");
		hashLabel.setText("Seleccionar hash:");
		rootButton.setText("�");
		acceptButton.setText("Crear hash");
		backButton.setText("Volver");
		pwLabel.setText("Contrase�a:");
		hashResultLabel.setText("Resultado del hash");

		rootTextField.setEditable(false);
		hashResultArea.setEditable(false);
		hashComboBox.setEditable(false);
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

		acceptButton.addActionListener(this::startHash);
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
						.addGroup(layout.createSequentialGroup().addComponent(hashLabel)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(hashComboBox))
						.addGroup(layout.createSequentialGroup().addComponent(pwLabel)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(passwordField)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(acceptButton))
						.addComponent(hashResultLabel).addComponent(hashPane).addComponent(backButton)
						.addComponent(statusLabel))
				.addContainerGap());

		// Vertical groups
		layout.setVerticalGroup(layout.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup().addComponent(rootLabel).addComponent(rootTextField)
						.addComponent(rootButton))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup().addComponent(hashLabel).addComponent(hashComboBox))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup().addComponent(pwLabel).addComponent(passwordField))
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(hashResultLabel)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(hashPane)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup().addComponent(acceptButton).addComponent(backButton))
				.addPreferredGap(ComponentPlacement.RELATED).addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(statusLabel).addContainerGap());

		// Link size of labels
		layout.linkSize(SwingConstants.HORIZONTAL, rootLabel, hashLabel, pwLabel);
	}

	/*
	 * Set the last parameters of the main window
	 */
	private void finishGui() {
		pack();
		setTitle("Cifrador 2020 SRT - Crear Hash");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(MIN_SIZE);
		setSize(DEFAULT_SIZE);
		setLocationRelativeTo(null); // center the window on screen

		updateStatus("Preparado para crear el hash.");
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
	 * Hash of the current message 
	 * The longitude of the message in bytes
	 */
	private void previewHash() {

		StringBuffer sb = new StringBuffer();

		// Get the byte array
		String auxS = new String(hash.getCalculatedHash());

		// Transform into char array
		char ch[] = auxS.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			String hexString = Integer.toHexString(ch[i]); // Convert to HEX
			sb.append(hexString);
		}
		String result = sb.toString(); // Transform into String again

		hashResultArea.setText(result + "\n\nHecho: " + Integer.toString(hash.getMessageBytes()) + " bytes"); // Show the String in the UI
		hashResultArea.setCaretPosition(0); // Scroll back to the top
	}
	
	/*
	 * Method to initiate the hashing process once the "accept" button is clicked
	 */
	private void startHash(ActionEvent event) {

		if (rootPath != null) {
			if (passwordField.getPassword().length != 0) {
				updateStatus("Aplicando hash al archivo");

				opSuccessfull = true;

				try {
					hash.hashFile(rootPath, parseHashChosen(hashComboBox.getSelectedIndex()),
							String.valueOf(passwordField.getPassword()));
				} catch (Exception e) {
					e.printStackTrace();
					opSuccessfull = false;
				}

				if (opSuccessfull) { // If the file could be hashed

					previewHash(); // Show the result in the UI

					JOptionPane.showMessageDialog(this, "Se ha creado el hash correctamente."); // Tell the user
					updateStatus("Se ha creado el hash correctamente.");
					
				} else {
					JOptionPane.showMessageDialog(this, "Se ha producido un error al crear el hash.", "ERROR", JOptionPane.ERROR_MESSAGE);
					updateStatus("ERROR : No se ha podido crear el hash.");
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
	 * Method to translate the drop down list into the exact algorithm name for
	 * later calls
	 */
	private String parseHashChosen(int op) {

		String chosen;

		switch (op) {

		case 0:
			chosen = "MD2";
			break;

		case 1:
			chosen = "MD5";
			break;

		case 2:
			chosen = "SHA-1";
			break;
			
		case 3:
			chosen = "SHA-256"; 
			break;

		case 4:
			chosen = "SHA-384";
			break;

		case 5:
			chosen = "SHA-512";
			break;
			
		case 6:
			chosen = "HmacMD5";
			break;

		case 7:
			chosen = "HmacSHA1";
			break;

		case 8:
			chosen = "HmacSHA256";
			break;
			
		case 9:
			chosen = "HmacSHA384";
			break;

		case 10:
			chosen = "HmacSHA512";
			break;

		default:
			chosen = "MD2";
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
