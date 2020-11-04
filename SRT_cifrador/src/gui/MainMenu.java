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
	
	private static final Dimension MIN_SIZE = new Dimension(250, 250);
	private static final Dimension DEFAULT_SIZE = new Dimension(300, 250);

	private JLabel welcomeLabel;

	private JButton encryptButton;
	private JButton decryptButton;

	/*
	 * Initiate GUI components
	 */
	private void initComponents() {

		welcomeLabel = new JLabel();

		encryptButton = new JButton();
		decryptButton = new JButton();

		welcomeLabel.setText("<html>Bienvenido al cifrador de SRT<br/><br/>¿Qué operación desea realizar?</html>");
		encryptButton.setText("Encriptar un fichero");
		decryptButton.setText("Desencriptar un fichero");

		welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		encryptButton.setFocusable(false);
		decryptButton.setFocusable(false);

		encryptButton.addActionListener(this::encryptionUI);
		// decryptButton.addActionListener(this::DecryptionUI);
	}

	/*
	 * Initiate the layout for the JFrame window
	 */
	private void initLayout() {
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		// Horizontal groups
		layout.setHorizontalGroup(layout.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(welcomeLabel)
						.addComponent(encryptButton).addComponent(decryptButton))
				.addContainerGap());

		// Vertical groups
		layout.setVerticalGroup(layout.createSequentialGroup().addContainerGap()
				.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, 25).addComponent(welcomeLabel)
				.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, 35).addComponent(encryptButton)
				.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, 15).addComponent(decryptButton)
				.addContainerGap());

		// Link size of buttons
		layout.linkSize(SwingConstants.HORIZONTAL, encryptButton, decryptButton);
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
	private void decryptionUI(ActionEvent event) {

		new Interface(this);
		setVisible(false);
	}
	*/

	public MainMenu() {
		initComponents();
		initLayout();
		finishGui();
	}

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(MainMenu::new); // Call EventQueue on the UI to avoid freezes
	}
}
