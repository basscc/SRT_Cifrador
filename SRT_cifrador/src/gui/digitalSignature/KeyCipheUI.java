package gui.digitalSignature;

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
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import functions.DigitalSignature;
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

public class KeyCipheUI extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5723206829220885984L;

	private static final Dimension MIN_SIZE = new Dimension(400, 140);
	private static final Dimension DEFAULT_SIZE = new Dimension(500, 160);

	MainMenu parentUI;
	DigitalSignature ds;

	private Boolean opSuccessfull; // bool to determine if an operation was sucessfully executed

	private JLabel rootLabel;
	private JLabel statusLabel;
	private JTextField rootTextField;
	private JButton rootButton;
	private JButton acceptButton;
	private JButton backButton;

	private File rootPath;

	public KeyCipheUI(MainMenu mainMenu) {
		this.parentUI = mainMenu; // Get the instance of the parentUI to be able to return to the previous window
		this.setModalityType(ModalityType.APPLICATION_MODAL); // Make lower level windows to have blocked inputs 
		initComponents();
		initLayout();
		finishGui();
	}

	/*
	 * Initiate GUI components
	 */
	private void initComponents() {

		ds = new DigitalSignature();
		
		opSuccessfull = false;

		rootLabel = new JLabel();
		statusLabel = new JLabel();

		rootTextField = new JTextField();
		
		rootButton = new JButton();
		acceptButton = new JButton();
		backButton = new JButton();

		rootLabel.setText("Ruta de fichero:");
		rootButton.setText("�");
		acceptButton.setText("Cifrar");
		backButton.setText("Volver");

		rootTextField.setEditable(false);
		// Remove the ugly text boundary box when clicking the button
		rootButton.setFocusable(false);
		acceptButton.setFocusable(false);
		backButton.setFocusable(false);

		rootButton.addActionListener(e -> {
			try {
				selectRoot(e);
			} catch (IOException noFile) {
				noFile.printStackTrace();
			}
		});

		acceptButton.addActionListener(this::startCiphe);
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
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(backButton))
								.addPreferredGap(ComponentPlacement.RELATED, 250, Short.MAX_VALUE)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
										.addComponent(acceptButton)))
						.addComponent(statusLabel))
				.addContainerGap());

		// Vertical groups
		layout.setVerticalGroup(layout.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup().addComponent(rootLabel).addComponent(rootTextField)
						.addComponent(rootButton))
				.addPreferredGap(ComponentPlacement.RELATED, 15, 20)
				.addGroup(layout.createParallelGroup().addComponent(acceptButton).addComponent(backButton))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(statusLabel).addContainerGap());

		// Link size of labels
		layout.linkSize(SwingConstants.VERTICAL, rootTextField, rootButton);
	}

	/*
	 * Set the last parameters of the main window
	 */
	private void finishGui() {
		setTitle("Cifrador 2020 SRT - Cifrar con clave");
		pack();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(MIN_SIZE);
		setSize(DEFAULT_SIZE);
		setLocationRelativeTo(null); // center the window on screen

		updateStatus("Preparado para cifrar con clave.");
		setVisible(true);
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
	 * Method to initiate the key ciphering process once the "accept" button is clicked
	 */
	private void startCiphe(ActionEvent event) {

		if (rootPath != null) {

			updateStatus("Cifrando con clave. ");

			opSuccessfull = true;

			try {
				ds.keyCipher(rootPath);
			} catch (Exception e) {
				e.printStackTrace();
				opSuccessfull = false;
			}

			if (opSuccessfull) {

				JOptionPane.showMessageDialog(this, "El fichero ha sido cifrado."); // Tell the user
				updateStatus("Fichero cifrado correctamente.");
			} else {
				JOptionPane.showMessageDialog(this, "Se ha producido un error al cifrar.", "ERROR", JOptionPane.ERROR_MESSAGE);
				updateStatus("ERROR : No se ha podido cifrar el fichero.");
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
