import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.Border;

public class Interfaz extends JFrame{
	
	public Interfaz() {
		
		  JFrame frame = new JFrame(); // Crear nueva instancia de Jframe
		  frame.setTitle("Trabajo de SRT"); // Titulo de la ventana
		  frame.setSize(800,600); // Tamaño X, tamaño Y
		  frame.setLayout(new BorderLayout(6,6)); // Layout tipo margen
		  frame.setResizable(true); // Permitir redimensión de ventana
		  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Al pulsar la X, cerrar la app
		  frame.setVisible(true); // Hacer visible la ventana
		  
		  /* --- Divisores --- */
		  
		  JPanel p_archivo = new JPanel();
		  JPanel p_central = new JPanel();
		  JPanel p_inferior = new JPanel();

		  p_archivo.setBackground(Color.LIGHT_GRAY);
		  p_central.setBackground(Color.LIGHT_GRAY);
		  p_inferior.setBackground(Color.LIGHT_GRAY);
		  
		  p_archivo.setPreferredSize(new Dimension(500,60));
		  p_central.setPreferredSize(new Dimension(500,100));
		  p_inferior.setPreferredSize(new Dimension(500,60));

		  frame.getContentPane().add(p_archivo,BorderLayout.NORTH);
		  frame.getContentPane().add(p_central,BorderLayout.CENTER);
		  frame.getContentPane().add(p_inferior,BorderLayout.SOUTH);
		  
		  /* ----------------- Componentes ----------------------- */
		  
		  // Texto ruta de archivo seleccionado
			JTextPane texto_archivo_sel = new JTextPane();
			//texto_archivo_sel.setBackground(new Color(204, 204, 204));
			texto_archivo_sel.setEditable(false);
			texto_archivo_sel.setText("Ruta del archivo:");
		  
		  // Boton seleccionar archivo
		  	JButton sel_archivo = new JButton("Seleccionar archivo");
			//sel_archivo.setBackground(new Color(204, 204, 204));
			sel_archivo.setFocusable(false);
			sel_archivo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File(".")); // Iniciar por directorio actual

					int response = fileChooser.showOpenDialog(null); // Seleccionar archivo para abrir

					if(response == JFileChooser.APPROVE_OPTION) {
						File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
						texto_archivo_sel.setText(file.toString());
					}
				}
			});
			
			
			
			
			p_archivo.add(sel_archivo);
			p_archivo.add(texto_archivo_sel);
			
		frame.pack();	  
	}

}
