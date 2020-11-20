package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.LayoutStyle.ComponentPlacement;
/*
 * Developed by:
 * 
 * Carlos Salguero Sánchez
 * Javier Tovar Pacheco
 * 
 * UNEX - 2020 - SRT
 */

public class MainMenu extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6311365754074042278L;

	private static final Dimension MIN_SIZE = new Dimension(370, 200);
	private static final Dimension DEFAULT_SIZE = new Dimension(420, 220);

	private JLabel welcomeLabel;

	private JButton encryptButton;
	private JButton decryptButton;
	private JButton hashButton;
	private JButton verifyHashButton;

	/*
	 * Initiate GUI components
	 */
	private void initComponents() {

		welcomeLabel = new JLabel();

		encryptButton = new JButton();
		decryptButton = new JButton();
		hashButton = new JButton();
		verifyHashButton = new JButton();

		welcomeLabel.setText("<html>Bienvenido al cifrador de SRT<br/><br/>¿Qué operación desea realizar?</html>");
		encryptButton.setText("Encriptar un fichero");
		decryptButton.setText("Desencriptar un fichero");
		hashButton.setText("Hash un fichero");
		verifyHashButton.setText("Verificar hash");

		welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		encryptButton.setFocusable(false);
		decryptButton.setFocusable(false);
		hashButton.setFocusable(false);
		verifyHashButton.setFocusable(false);

		encryptButton.addActionListener(this::encryptionUI);
		decryptButton.addActionListener(this::decryptionUI);
		hashButton.addActionListener(this::hashUI);
		verifyHashButton.addActionListener(this::verifyHashUI);
	}

	/*
	 * Initiate the layout for the JFrame window
	 */
	private void initLayout() {
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		// Horizontal groups
		layout.setHorizontalGroup(layout.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(welcomeLabel)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(encryptButton)
										.addComponent(decryptButton))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)	
										.addComponent(hashButton)
										.addComponent(verifyHashButton)))));

		// Vertical groups
		layout.setVerticalGroup(layout.createSequentialGroup().addContainerGap()
				.addComponent(welcomeLabel)
				.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, 35)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						        	.addComponent(encryptButton)
						        	.addComponent(hashButton))
						        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						        	.addComponent(decryptButton)
						        	.addComponent(verifyHashButton)))));	
				
		// Link size of buttons
		layout.linkSize(SwingConstants.HORIZONTAL, encryptButton, decryptButton, hashButton, verifyHashButton);
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

		setVisible(true);
	}

	/*
	 * Go to the Encryption window
	 */
	private void encryptionUI(ActionEvent event) {

		new EncryptionUI(this);
		setVisible(false);
	}

	/*
	 * Go to the Decryption window
	 */
	private void decryptionUI(ActionEvent event) {

		new DecryptionUI(this);
		setVisible(false);
	}
	
	/*
	 * Go to the Hashing window
	 */
	private void hashUI(ActionEvent event) {

		new HashUI(this);
		setVisible(false);
	}
	
	/*
	 * Go to the Hashing window
	 */
	private void verifyHashUI(ActionEvent event) {

		new VerifyHashUI(this);
		setVisible(false);
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
