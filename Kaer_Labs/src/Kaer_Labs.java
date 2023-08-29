import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ij.plugin.PlugIn;

public class Kaer_Labs implements PlugIn{
	private JFrame frame;
	@Override
	public void run(String arg0) {
		 frame=new JFrame(); 
		 JPanel contentPane=new JPanel(new FlowLayout());
		contentPane.setLayout((LayoutManager) new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));	    
		frame.setContentPane(contentPane);
		
		
		JButton KisOverlay=new JButton("Kis Overlay");
		KisOverlay.setBackground(Color.GREEN);
		KisOverlay.setPreferredSize(new Dimension(150, 30));
		
		
		JButton KisOverlayFolder=new JButton("Kis Overlay Folder");
		KisOverlayFolder.setBackground(Color.lightGray);
		KisOverlayFolder.setPreferredSize(new Dimension(200, 30));
		
		JButton cancel=new JButton("Exit");
		cancel.setBackground(Color.white);
		cancel.setPreferredSize(new Dimension(100, 30));
		
		 JLabel label=new JLabel("Choose a plugin to continue!");
		 label.setBackground(Color.RED);
		 JPanel pan1=new JPanel(new FlowLayout());
		 pan1.add(label);
		 pan1.setBorder(new EmptyBorder(0, 20, 0, 0));
		 contentPane.add(pan1);
		//panel subtrated 
		  JPanel pan=new JPanel(new FlowLayout());
        
      
        pan.add(KisOverlay);
        pan.add(KisOverlayFolder);
        pan.add(cancel);
        pan.setBorder(new EmptyBorder(0, 0, 20, 0));
       
        contentPane.add(pan);
        
		
        // Définir la taille et la position de la fenêtre
        frame.setSize(new Dimension(500, 150));
        //frame.setLocation(0, 20);
        frame.setLocationRelativeTo(null);
        // Permettre le redimensionnement de la fenêtre
        frame.setResizable(true);
        // Terminer l'application lorsque la fenêtre est fermée
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		
		
		Kis_Overlay kisoverlay=new Kis_Overlay();
		Kis_Folder kisoverlayfolder=new Kis_Folder();
		
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
			
		});
		KisOverlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				kisoverlay.run("");
				frame.dispose();
			}
			
		});
		KisOverlayFolder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				kisoverlayfolder.run("");
				frame.dispose();
			}
			
		});
		
	}

}
