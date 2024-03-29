package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.LayoutStyle.ComponentPlacement;

import functions.DigitalSignature;
import functions.KeyStorage;
import gui.digitalSignature.*;
import gui.encrypt_decrypt.*;
import gui.hashing.*;
/*
 * Developed by:
 * 
 * Carlos Salguero S�nchez
 * Javier Tovar Pacheco
 * 
 * UNEX - 2020 - SRT
 */

public class MainMenu extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6311365754074042278L;

	private static final Dimension MIN_SIZE = new Dimension(360, 330);
	private static final Dimension DEFAULT_SIZE = new Dimension(440, 380);

	private boolean areKeysGenerated;

	private DigitalSignature ds;
	private KeyStorage ks;

	private JLabel welcomeLabel;
	private JLabel dsLabel;

	private JButton encryptButton;
	private JButton decryptButton;
	private JButton hashButton;
	private JButton verifyHashButton;

	private JButton signButton;
	private JButton verifySignButton;
	private JButton keyCiphButton;
	private JButton keyDeCiphButton;
	private JButton genKeys;
	
	private JButton keyStorageButton;
	private JPasswordField passwordField;

	/*
	 * Initiate GUI components
	 */
	private void initComponents() {

		if (new File("practica5.key").exists()) {
			areKeysGenerated = true;
		} else {
			areKeysGenerated = false;
		}
		
		ds = new DigitalSignature();
		ks = new KeyStorage();

		welcomeLabel = new JLabel();
		dsLabel = new JLabel();

		encryptButton = new JButton();
		decryptButton = new JButton();
		hashButton = new JButton();
		verifyHashButton = new JButton();
		signButton = new JButton();
		verifySignButton = new JButton();
		keyCiphButton = new JButton();
		keyDeCiphButton = new JButton();
		genKeys = new JButton();
		
		keyStorageButton = new JButton();
		passwordField = new JPasswordField();

		welcomeLabel.setText("<html>Bienvenido a la herramienta de SRT<br/><br/>�Qu� operaci�n desea realizar?</html>");
		welcomeLabel.setFont(new Font("", Font.ITALIC, 14));
		dsLabel.setText("<html><b>Firma digital<b/></html>");
		dsLabel.setFont(new Font("", Font.BOLD, 14));

		encryptButton.setText("Encriptar un fichero");
		decryptButton.setText("Desencriptar un fichero");
		hashButton.setText("Hash un fichero");
		verifyHashButton.setText("Verificar hash");
		signButton.setText("Firmar un fichero");
		verifySignButton.setText("Verificar firma");
		keyCiphButton.setText("Cifrar con clave");
		keyDeCiphButton.setText("Descifrar con clave");
		genKeys.setText("Generar claves");
		
		keyStorageButton.setText("Almacen de claves");

		welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		dsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		encryptButton.setFocusable(false);
		decryptButton.setFocusable(false);
		hashButton.setFocusable(false);
		verifyHashButton.setFocusable(false);
		signButton.setFocusable(false);
		verifySignButton.setFocusable(false);
		keyCiphButton.setFocusable(false);
		keyDeCiphButton.setFocusable(false);
		genKeys.setFocusable(false);
		keyStorageButton.setFocusable(false);
		
		passwordField.setEchoChar('*'); // Type * as the user writes in the component

		encryptButton.addActionListener(this::encryptionUI);
		decryptButton.addActionListener(this::decryptionUI);
		hashButton.addActionListener(this::hashUI);
		verifyHashButton.addActionListener(this::verifyHashUI);

		signButton.addActionListener(this::signUI);
		verifySignButton.addActionListener(this::verifySignUI);
		keyCiphButton.addActionListener(this::keyCipheUI);
		keyDeCiphButton.addActionListener(this::decipheUI);
		genKeys.addActionListener(this::keyGeneration);
		
		keyStorageButton.addActionListener(this::keyStorageGen);
	}

	/*
	 * Initiate the layout for the JFrame window
	 */
	private void initLayout() {
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		// Horizontal groups
		layout.setHorizontalGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout
				.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(welcomeLabel).addComponent(dsLabel)
				.addComponent(genKeys).addComponent(keyStorageButton)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(encryptButton)
								.addComponent(decryptButton).addComponent(signButton).addComponent(verifySignButton))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(hashButton)
								.addComponent(verifyHashButton).addComponent(keyCiphButton)
								.addComponent(keyDeCiphButton)))));

		// Vertical groups
		layout.setVerticalGroup(layout.createSequentialGroup().addContainerGap().addComponent(welcomeLabel)
				.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, 35)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(encryptButton).addComponent(hashButton))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(decryptButton).addComponent(verifyHashButton))
								.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, 25)
								.addComponent(dsLabel).addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(signButton).addComponent(keyCiphButton))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(verifySignButton).addComponent(keyDeCiphButton))))
				.addComponent(genKeys)
				.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, 15)
				.addComponent(keyStorageButton));

		// Link size of buttons
		layout.linkSize(SwingConstants.HORIZONTAL, encryptButton, decryptButton, hashButton, verifyHashButton,
				signButton, verifySignButton, keyCiphButton, keyDeCiphButton, genKeys, keyStorageButton);
	}

	/*
	 * Set the last parameters of this window
	 */
	private void finishGui() {
		pack();
		setTitle("Cifrador 2020 SRT");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setMinimumSize(MIN_SIZE);
		setSize(DEFAULT_SIZE);
		setLocationRelativeTo(null); // center the window on screen

		setVisible(true);
	}

	/*
	 * Go to the Encryption window
	 */
	private void encryptionUI(ActionEvent event) {

		new EncryptionUI(this);
	}

	/*
	 * Go to the Decryption window
	 */
	private void decryptionUI(ActionEvent event) {

		new DecryptionUI(this);
	}

	/*
	 * Go to the Hashing window
	 */
	private void hashUI(ActionEvent event) {

		new HashUI(this);
	}

	/*
	 * Go to the Hashing window
	 */
	private void verifyHashUI(ActionEvent event) {

		new VerifyHashUI(this);
	}
	
	//
	// DIGITAL SIGNATURE
	//

	/*
	 * Check if keys are generated, then
	 * Go to the Sign window
	 */
	private void signUI(ActionEvent event) {

		if (areKeysGenerated) {
			new SignUI(this);
		} else {
			JOptionPane.showMessageDialog(this, "No se han generado las claves.", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	/*
	 * Check if keys are generated, then
	 * Go to the VerifySign window
	 */
	private void verifySignUI(ActionEvent event) {

		if (areKeysGenerated) {
			new VerifySignUI(this);
		} else {
			JOptionPane.showMessageDialog(this, "No se han generado las claves.", "ERROR", JOptionPane.ERROR_MESSAGE);
		}

	}

	/*
	 * Check if keys are generated, then
	 * Go to the KeyCipher window
	 */
	private void keyCipheUI(ActionEvent event) {

		if (areKeysGenerated) {
			new KeyCipheUI(this);
		} else {
			JOptionPane.showMessageDialog(this, "No se han generado las claves.", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	/*
	 * Check if keys are generated, then
	 * Go to the KeyDecipher window
	 */
	private void decipheUI(ActionEvent event) {
		if (areKeysGenerated) {
			new KeyDecipheUI(this);
		} else {
			JOptionPane.showMessageDialog(this, "No se han generado las claves.", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	/*
	 * Attempt to generate keys for digital signature
	 */
	private void keyGeneration(ActionEvent event) {

		try {
			ds.keyGeneration();
			areKeysGenerated = true;
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
			areKeysGenerated = false;
		}
		if (areKeysGenerated) {
			JOptionPane.showMessageDialog(this, "Se han generado las claves.");
		} else {
			JOptionPane.showMessageDialog(this, "No se han generado las claves.", "ERROR", JOptionPane.ERROR_MESSAGE);
		}

	}
	
	/*
	 * Initialize key storage
	 * Generate key file from selected key in key storage
	 */
	private void keyStorageGen(ActionEvent event) {
		
		String password;
		
		// Create popup window
		JLabel labelch= new JLabel("Seleccione archivo para el almacen de claves:");
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		JLabel labelpw = new JLabel("Introduzca su contrase�a:");
		JPasswordField jpf = new JPasswordField();
		JOptionPane.showConfirmDialog(null, new Object[] {labelch, chooser, labelpw, jpf}, "Almacen de claves", JOptionPane.OK_CANCEL_OPTION);
		
		// Read the password provided by the user
		password = String.valueOf(jpf.getPassword());
		
		// Read the file
		File inFile = chooser.getSelectedFile();
		System.out.println("Fichero seleccionado : " + chooser.getSelectedFile().getAbsolutePath() + "\n");
		
		// Generate the key storage with file and password
		try {
			ks = new KeyStorage(inFile, password);
			
			ks.showKeys();
			
			// Create popup window
			JLabel label2 = new JLabel("Selecciona una clave de las mostradas por consola:");
			JTextField jtf = new JTextField();
			JOptionPane.showConfirmDialog(null, new Object[] {label2, jtf}, "Seleccionar una clave", JOptionPane.OK_CANCEL_OPTION);
			
			try {
				int selectedKey = Integer.parseInt(jtf.getText());
				
				try {
					ks.exportKey(selectedKey);
					areKeysGenerated = true;
				// Catch error during file generation
				} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
					JOptionPane.showMessageDialog(this, "No se ha podido generar el fichero de clave practica5.", "ERROR", JOptionPane.ERROR_MESSAGE);
				// Catch selected key does not exist
				} catch (NullPointerException b) {
					JOptionPane.showMessageDialog(this, "La clave seleccionada no existe en el almacen.", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			
		    // Catch input not a number
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "No se ha reconocido el parametro introducido como un n�mero v�lido.", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		
		// Catch wrong password
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException
				| IOException e) {
			JOptionPane.showMessageDialog(this, "La contrase�a es incorrecta.", "ERROR", JOptionPane.ERROR_MESSAGE);
		}	
	}

	public MainMenu() {
		initComponents();
		initLayout();
		finishGui();
	}

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(MainMenu::new); // Call EventQueue on the UI to avoid freezes
	}
}
