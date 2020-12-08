package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import functions.DigitalSignature;
import functions.Hash;
import gui.digitalSignature.DecipheUI;
import gui.digitalSignature.SignUI;
import gui.digitalSignature.VerifySignUI;

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

public class FirmaDigitalUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7155697168794874224L;

	private static final Dimension MIN_SIZE = new Dimension(300, 200);
	private static final Dimension DEFAULT_SIZE = new Dimension(500, 200);

	MainMenu parentUI;
	DigitalSignature ds;

	private JLabel welcomeLabel;

	private JButton firmarButton;
	private JButton verificarFirmaButton;
	private JButton cifrarButton;
	private JButton descifrarButton;

	private JButton generarClaveButton;
	private JButton backButton;

	public FirmaDigitalUI(MainMenu parentUI) {
		this.parentUI = parentUI; // Get the instance of the parentUI to be able to return to the previous window
		initComponents();
		initLayout();
		finishGui();
	}

	/*
	 * Initiate GUI components
	 */
	private void initComponents() {

		ds = new DigitalSignature();
		welcomeLabel = new JLabel();

		firmarButton = new JButton();
		verificarFirmaButton = new JButton();
		cifrarButton = new JButton();
		descifrarButton = new JButton();

		generarClaveButton = new JButton();
		
		backButton = new JButton();
		
		

		welcomeLabel.setText("<html>¿Qué operación desea realizar?</html>");
		firmarButton.setText("Firmar un fichero");
		verificarFirmaButton.setText("Verificar firma");
		cifrarButton.setText("Cifrar fichero con clave");
		descifrarButton.setText("Descifrar fichero con clave");

		generarClaveButton.setText("Generar claves");
		
		backButton.setText("Volver");

		welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		firmarButton.setFocusable(false);
		verificarFirmaButton.setFocusable(false);
		cifrarButton.setFocusable(false);
		descifrarButton.setFocusable(false);

		generarClaveButton.setFocusable(false);
		
		backButton.setFocusable(false);

		firmarButton.addActionListener(this::firmarUI);
		verificarFirmaButton.addActionListener(this::verificarFirmaUI);
		cifrarButton.addActionListener(this::cifrarLlaveUI);
		descifrarButton.addActionListener(this::descifrarLlaveUI);
		generarClaveButton.addActionListener(this::keyGeneration);
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
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(welcomeLabel)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(firmarButton).addComponent(verificarFirmaButton))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(cifrarButton).addComponent(descifrarButton)))
						.addGroup(
								layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(generarClaveButton))
						.addGroup(
								layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(backButton))));

		// Vertical groups
		layout.setVerticalGroup(layout.createSequentialGroup().addContainerGap().addComponent(welcomeLabel)
				.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, 35)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(firmarButton).addComponent(cifrarButton))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(verificarFirmaButton).addComponent(descifrarButton))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(generarClaveButton))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(backButton)))));

		// Link size of buttons
		layout.linkSize(SwingConstants.HORIZONTAL, firmarButton, verificarFirmaButton, cifrarButton, descifrarButton, generarClaveButton);
	}

	/*
	 * Set the last parameters of the main window
	 */
	private void finishGui() {
		pack();
		setTitle("Cifrador 2020 SRT - Verificación de Hash");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setMinimumSize(MIN_SIZE);
		setSize(DEFAULT_SIZE);

		setVisible(true);
		// updateStatus("Preparado para verificar un hash.");
	}
	
	/*
	 * Go to the  window
	 */
	private void firmarUI(ActionEvent event) {

		new SignUI(this);
		setVisible(false);
	}

	/*
	 * Go to the  window
	 */
	private void verificarFirmaUI(ActionEvent event) {

		new VerifySignUI(this);
		setVisible(false);
	}
	
	/*
	 * Go to the  window
	 */
	private void cifrarLlaveUI(ActionEvent event) {

		//new CifrarLlaveUI(this);
		setVisible(false);
	}
	
	/*
	 * Go to the  window
	 */
	private void descifrarLlaveUI(ActionEvent event) {

		new DecipheUI(this);
		setVisible(false);
	}
	
	/*
	 * Go to the  window
	 */
	private void keyGeneration(ActionEvent event) {

	
	}
	
	/*
	 * Method to dispose of this window and go back to the parent UI
	 */
	private void goBackUI(ActionEvent event) {

		parentUI.setVisible(true); // Make the main menu visible again
		setVisible(false); // Hide this window
		dispose(); // Remove this window
	}

}
