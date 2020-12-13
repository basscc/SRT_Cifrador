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
 * Carlos Salguero Sánchez
 * Javier Tovar Pacheco
 * 
 * UNEX - 2020 - SRT
 */

public class VerifySignUI extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5723206829220885984L;

	private static final Dimension MIN_SIZE = new Dimension(400, 140);
	private static final Dimension DEFAULT_SIZE = new Dimension(500, 180);

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

	public VerifySignUI(MainMenu mainMenu) {
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
		rootButton.setText("…");
		acceptButton.setText("Verificar");
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
						.addGroup(layout.createSequentialGroup().addComponent(acceptButton))
						.addComponent(backButton)
						.addComponent(statusLabel))
				.addContainerGap());

		// Vertical groups
		layout.setVerticalGroup(layout.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup().addComponent(rootLabel).addComponent(rootTextField)
						.addComponent(rootButton))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(backButton)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup().addComponent(acceptButton).addComponent(backButton))
				.addPreferredGap(ComponentPlacement.RELATED).addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(statusLabel).addContainerGap());

		// Link size of labels
		layout.linkSize(SwingConstants.HORIZONTAL, rootLabel);
	}

	/*
	 * Set the last parameters of the main window
	 */
	private void finishGui() {
		setTitle("Cifrador 2020 SRT - Firma digital");
		pack();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(MIN_SIZE);
		setSize(DEFAULT_SIZE);

		setVisible(true);
		updateStatus("Preparado para la firma digital.");
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
	 * 
	 */
	private void startVerifying(ActionEvent event) {

		if (rootPath != null) {

			updateStatus("Verificando firma.");

			opSuccessfull = true;
			try {
				ds.verifySign(rootPath);
			} catch (Exception e) {
				e.printStackTrace();
				opSuccessfull=false;
			}

			if (opSuccessfull) { // If the file could be signed

				JOptionPane.showMessageDialog(this, "El fichero ha sido verificado."); // Tell the user
				updateStatus("Fichero verificado correctamente.");
			} else {
				JOptionPane.showMessageDialog(this, "Se ha producido un error al verificar.");
				updateStatus("ERROR : No se ha podido verificar el fichero.");
			}

		} else {
			JOptionPane.showMessageDialog(this, "ERROR : No se ha seleccionado ningún fichero.");
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
