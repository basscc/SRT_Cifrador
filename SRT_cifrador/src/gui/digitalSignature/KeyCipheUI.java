package gui.digitalSignature;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import gui.FirmaDigitalUI;

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

public class KeyCipheUI extends JFrame {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1182454551418726828L;
	
	private static final Dimension MIN_SIZE = new Dimension(300, 250);
	private static final Dimension DEFAULT_SIZE = new Dimension(500, 300);

	FirmaDigitalUI parentUI;

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

	public KeyCipheUI(FirmaDigitalUI parentUI) {
		this.parentUI = parentUI; // Get the instance of the parentUI to be able to return to the previous window
		initComponents();
		initLayout();
		finishGui();
	}

	/*
	 * Initiate GUI components
	 */
	private void initComponents() {


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
		rootButton.setText("…");
		acceptButton.setText("Verificar");
		backButton.setText("Volver");
		pwLabel.setText("Contraseña:");

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
		setTitle("Cifrador 2020 SRT - Cifrado con clave");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setMinimumSize(MIN_SIZE);
		setSize(DEFAULT_SIZE);

		setVisible(true);
		updateStatus("Preparado para verificar un hash.");
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
