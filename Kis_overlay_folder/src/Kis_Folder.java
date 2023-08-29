import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import ij.gui.ImageWindow;
import ij.io.FileSaver;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;

import org.json.JSONException;
import org.json.JSONObject;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.LutLoader;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.LUT;

public class Kis_Folder implements PlugIn{
	
	private JComboBox<String> selected;
	private JComboBox<String> selectedLUT;
	private JComboBox<Integer> selectedExpo;
	private DefaultComboBoxModel<Integer> comboBoxModel ;
	private JFrame frame;
	private String[] options = {"Max", "Add", "Soustract","Multiply","And","Or"};
	private String lutDirPath;
	private String lutName;
	private int nbrsutracted;
	private double lutmax=1.0;
	private LUT lut;
	private int[] time_expo;
	private int expo_fluo;
	private int valueSelected_time_expo;
	private Integer[] time_expo_wrapper ;
	private File file ;
	private File[] files;
	private File[] repertory, brightfiled_File;
	private ImagePlus [] overlays;
	private ImagePlus overlay;
	private File repertoir;
	 private String json_txt,json_txt1; 
	 private ImagePlus[]subtracted ;
	  private  ImagePlus[]brightfiled;
	  private  ImagePlus[]brightfiled_RVB;
	  private ImagePlus [] subtracted_Color;
	  private ImagePlus [] subtracted_Co;
	  private String optionSelected="Max";
	  private  String [] frameId_Sub;
	 // private String []timestamp;
	   private String [] frameId_Br;
	   private ImageStack stack ;
	  private ImagePlus stackImage;
	  private boolean isNoteSave=false;
	  private JTextField saisieLutmax;
	  
	@Override
	public void run(String arg0) {
		
		// Recup�ration le repertoir 
		 lutDirPath = IJ.getDirectory("luts");
   // R�cup�rer la liste des noms de fichiers de LUT dans le r�pertoire des LUT pr�d�finies
		File lutDir = new File(lutDirPath);
		String[] lutFiles = lutDir.list();
		for(int i=0;i<lutFiles.length;i++) {
			lutFiles[i]=lutFiles[i].replace(".lut", " ").trim();
		}
		Arrays.sort(lutFiles);
		lutName=lutFiles[0].concat(".lut");
		lut=getLUT(lutName);
		selectedLUT= new JComboBox<>(lutFiles);
		
		comboBoxModel = new DefaultComboBoxModel<>();
		selectedExpo = new JComboBox<>(comboBoxModel);
		
		overlay=new ImagePlus();
		  //stack = new ImageStack();
		   stackImage = new ImagePlus();
		   
		   //saissie de la lut max
		   saisieLutmax=new JTextField();
		   saisieLutmax.setText(String.valueOf((int)(lutmax*100)));
	 /**
	 * 	Boite de dialogue
	 */
		
	 		//OpenImage();
	 		
	 		//interface utilisateur
			 IHM();
		
		
		//end run
	}
	
	
	public void IHM() {
		 frame=new JFrame(); 
		 frame.setLayout(new FlowLayout());
		 JPanel contentPane=new JPanel(new FlowLayout());
		contentPane.setLayout((LayoutManager) new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));	    
		frame.setContentPane(contentPane);
		
		
		JButton save=new JButton("Save");
		save.setBackground(Color.GREEN);
		save.setPreferredSize(new Dimension(100, 30));
		save.setEnabled(false);
		
		JButton open=new JButton("Open Image");
		open.setBackground(Color.cyan);
		open.setPreferredSize(new Dimension(100, 30));
		
		
		JButton apply=new JButton("Apply");
		apply.setBackground(Color.lightGray);
		apply.setPreferredSize(new Dimension(100, 30));
		
		JButton cancel=new JButton("Exit");
		cancel.setBackground(Color.white);
		cancel.setPreferredSize(new Dimension(100, 30));
		
		//panel subtrated 
       
       JPanel pan1=new JPanel(new FlowLayout());
       JLabel label=new JLabel("Folder : ");
      JTextField text=new JTextField(" ");
      text.setEditable(false);
      text.setPreferredSize(new Dimension(250,30));
    
       
       pan1.add(label,BorderLayout.WEST);
       pan1.add(text,BorderLayout.CENTER);
       pan1.add(open,BorderLayout.EAST);
       contentPane.add(pan1);
       
       JPanel panNb=new JPanel(new FlowLayout());
       JLabel nbrlabel=new JLabel("");
       nbrlabel.setForeground(Color.BLUE);
       panNb.add(nbrlabel);
       contentPane.add(panNb);
       
       JPanel pan4=new JPanel(new FlowLayout());
       JLabel label3=new JLabel("LUT Color : ");
       
        selectedLUT.setPreferredSize(new Dimension(250,30));
       pan4.add(label3);
       pan4.add(selectedLUT);
       pan4.setBorder(new EmptyBorder(0, -100, 10, 0));
       contentPane.add(pan4);
       
       //Selecte exposition time
       
    	  JPanel pan_expo=new JPanel(new FlowLayout());
          JLabel labe_expo=new JLabel("Expo time : ");
          selectedExpo.setPreferredSize(new Dimension(250,30));
           pan_expo.add(labe_expo);
           pan_expo.add(selectedExpo);
           pan_expo.setBorder(new EmptyBorder(0, -100, 10, 0));
          contentPane.add(pan_expo);

          JPanel count_pan=new JPanel(new FlowLayout());
          JLabel count_label=new JLabel("");
          count_label.setForeground(Color.RED);
          count_pan.add(count_label);
          contentPane.add(count_pan);
       
       //Panel LUT max 
       
       JPanel pan3=new JPanel(new FlowLayout());
       JLabel label2=new JLabel("LUT Max : ");
      
       JSlider slider1 = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
       pan3.setBorder(new EmptyBorder(10, -150, 0, 0));
       slider1.setMajorTickSpacing(20);
       slider1.setMinorTickSpacing(5);
       slider1.setPaintTicks(true);
       slider1.setPaintLabels(true);
       
       
       saisieLutmax.setPreferredSize(new Dimension(50,30));
       pan3.add(label2);
       pan3.add(slider1);
       pan3.add(saisieLutmax);
       contentPane.add(pan3);
       
       //panel modes
       
       JPanel pan2=new JPanel(new FlowLayout());
       JLabel label1=new JLabel("Overlay Mode : ");
        selected = new JComboBox<>(options);
       
        
        selected.setPreferredSize(new Dimension(250,30));
       pan2.add(label1);
       pan2.add(selected);
       pan2.setBorder(new EmptyBorder(0, -120, 10, 0));
       contentPane.add(pan2);
       
       //panel boutton 
       
       JPanel pan=new JPanel(new FlowLayout());
       
      
      
       pan.add(save);
       pan.add(apply);
       pan.add(cancel);
       pan.setBorder(new EmptyBorder(0, 0, 20, 0));
      
       contentPane.add(pan);
       
		
       // D�finir la taille et la position de la fen�tre
       frame.setSize(new Dimension(550, 600));
       frame.setLocation(0, 20);
       //frame.setLocationRelativeTo(null);
       // Permettre le redimensionnement de la fen�tre
       frame.setResizable(true);
       // Terminer l'application lorsque la fen�tre est ferm�e
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		/*
		 * -------------------------------------------------------------
		 * 		cr�ation des �v�nements 
		 * --------------------------------------------------------------
		 */
		
		open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				 file=openImage();
				 text.setText(file.getAbsoluteFile().toString());
				 files=file.listFiles(); //load subtracted images
				 nbrsutracted=files.length;
				 nbrlabel.setText("There are "+nbrsutracted+" images found in the subtracted directory");
				 brightfiled_File=foundBrightfield(file);
				 subtracted=new ImagePlus[files.length];
				 time_expo=new int[subtracted.length];
				 if(repertoir.exists()) {
					 brightfiled=new ImagePlus [brightfiled_File.length];
				 }
				 frameId_Sub=new String[files.length];
				 
				 time_expo_wrapper = new Integer[files.length];
				 
				 if(repertoir.exists()) {
					 
					 frameId_Br=new String[brightfiled_File.length];
					 brightfiled_RVB=new ImagePlus [brightfiled_File.length];
					 overlays=new ImagePlus [brightfiled_File.length];
					 subtracted_Color=new ImagePlus [brightfiled_File.length];
					 time_expo=new int[brightfiled_File.length];
				 }
				 //get medata of the images
				 for(int i=0;i<files.length;i++) {
					 subtracted[i] = IJ.openImage(files[i].getAbsolutePath());
					  json_txt = subtracted[i].getProp("ImageDescription");
					  
					  if(json_txt == null ) {
				             IJ.showMessage("Error", "cannot extract meta field from file.");
				             return;
				            }
					  
					  expo_fluo=meta_data(json_txt);
					  //JOptionPane.showMessageDialog(null,expo_fluo);
					  time_expo[i]=expo_fluo;
					  
					  time_expo_wrapper[i] = time_expo[i];
					  //JOptionPane.showMessageDialog(null,time_expo_wrapper[i]);
					  
				 }
				 valueSelected_time_expo=time_expo_wrapper[0];
				  
				 DefaultComboBoxModel<Integer> newModel = new DefaultComboBoxModel<>(time_expo_wrapper);
	             selectedExpo.setModel(newModel);
				
				
				
				 subtracted_Co=new ImagePlus[files.length];
				
				 save.setEnabled(false);
			}
			
		});
		
		selected.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				 optionSelected = (String) selected.getSelectedItem();	
				 save.setEnabled(false);
			}
			
		});
		
		selectedLUT.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lutName=(String) selectedLUT.getSelectedItem();
				lut=getLUT(lutName.concat(".lut"));
				 save.setEnabled(false);
				
			}
			
		});
		
		slider1.addChangeListener(new ChangeListener() {
           public void stateChanged(ChangeEvent e) {
        	   try {
        		   int value = slider1.getValue();
                   lutmax=(double)value/100;
            	   save.setEnabled(false);
            	   saisieLutmax.setText(String.valueOf(value));
        	   }catch(Exception ex) {
        		   ex.getMessage();
        	   }
        	   
           }
       });
	 
		selectedExpo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				valueSelected_time_expo= (int) selectedExpo.getSelectedItem();
				
				String response=tem_expo_saturation(subtracted,valueSelected_time_expo);
				count_label.setText(response);
				save.setEnabled(false);
			}
			
		});
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//JOptionPane.showMessageDialog(null,lutmax);
				FoundImage(files);
				
				//Affichage des images overlay
				if(!repertoir.exists()) {
					stack = new ImageStack();
					for(ImagePlus imageColor: subtracted_Co) {
						if(imageColor!=null) {
							stack.addSlice(imageColor.getTitle(), imageColor.getProcessor());
							// overlayImage.show();
							stackImage.setStack("Stack Image", stack);
							stackImage.show();
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							ImageWindow win=stackImage.getWindow() ;
							win.setLocation(600, 0);
							save.setEnabled(true);
							isNoteSave=true;
						}
						
					}
				}else if(repertoir.exists()&& brightfiled_File.length==0){
					stack = new ImageStack();
					for(ImagePlus imageColor: subtracted_Co) {
						if(imageColor !=null) {
							stack.addSlice(imageColor.getTitle(), imageColor.getProcessor());
							// overlayImage.show();
							stackImage.setStack("Stack Image", stack);
							stackImage.show();
							ImageWindow win=stackImage.getWindow();
							win.setLocation(600, 0);
							save.setEnabled(true);
							isNoteSave=true;
						}
						
					}
				}else {
				
					stack = new ImageStack();
					for(ImagePlus overlayImage: overlays) {
						if(overlayImage!=null)  {
							stack.addSlice(overlayImage.getTitle(), overlayImage.getProcessor());
							// overlayImage.show();
							stackImage.setStack("Stack Image", stack);
							stackImage.show();
							ImageWindow win=stackImage.getWindow();
							win.setLocation(600, 0);
							save.setEnabled(true);
							isNoteSave=true;
						}
						
					}
				}
				 
				// save.setEnabled(true);
				 // Activation du bouton save
				
			}
		});
		
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(isNoteSave==false) {
					frame.dispose();
				}
					else {
						int isSaving=JOptionPane.showConfirmDialog(null, "Do you want to saving image before exit ?");
						if(isSaving==0) {
							save_overlay_images(overlays,file,optionSelected);
							frame.dispose();
							stackImage.close();
							
						}else if(isSaving==1) {
							frame.dispose();
							stackImage.close();
						}else {
							return;
						}
					}
				}
				
			}
			
		);
		
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(!repertoir.exists()) {
					save_color_images(subtracted_Co,file,optionSelected);
					
					save.setEnabled(false);
					isNoteSave=false;
				}else if(repertoir.exists() && brightfiled_File.length==0) {
					save_color_images(subtracted_Co,file,optionSelected);
					
					save.setEnabled(false);
					isNoteSave=false;
				}else {
					
					save_overlay_images(overlays,file,optionSelected);
					
					save.setEnabled(false);
					isNoteSave=false;
				}
			}
			
		});
		
		saisieLutmax.getDocument().addDocumentListener(new DocumentListener() {
           

            @Override
            public void removeUpdate(DocumentEvent e) {
            	String input = saisieLutmax.getText();

                try {
                    int entier = Integer.parseInt(input);
                    lutmax=(double)entier/100;
                    slider1.setValue(entier);
                    save.setEnabled(false);
                } catch (NumberFormatException ex) {
                    // G�rer le cas d'une saisie invalide ici, si n�cessaire
                	
                }
            }

            
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				String input = saisieLutmax.getText();

                try {
                    int entier = Integer.parseInt(input);
                    lutmax=(double)entier/100;
                    slider1.setValue(entier);
                    save.setEnabled(false);
                } catch (NumberFormatException ex) {
                    // G�rer le cas d'une saisie invalide ici, si n�cessaire
                }
				
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				String input = saisieLutmax.getText();

                try {
                    int entier = Integer.parseInt(input);
                    lutmax=(double)entier/100;
                    slider1.setValue(entier);
                    save.setEnabled(false);
                } catch (NumberFormatException ex) {
                    // G�rer le cas d'une saisie invalide ici, si n�cessaire
                }
				
			}

			
        });
       
	}
	//Fonction permettant d'ouvire un r�pertoire
	public File openImage() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
	    jfc.setDialogTitle("S�lectionnez un r�pertoire");
	    jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

	    int returnValue = jfc.showOpenDialog(null);

	    if (returnValue == JFileChooser.APPROVE_OPTION) {
	        return jfc.getSelectedFile();
	    } else {
	        return null; // L'utilisateur a annul� la s�lection ou a ferm� la bo�te de dialogue
	    }
	}
	
	//Fonction permettant d'avoir la lut 
	public LUT getLUT(String lutFilename) {
		 
        String lutPath = lutDirPath + lutFilename;
         lut =  LutLoader.openLut(lutPath);
	
	return lut;
}
	
	
	// Fonction permettant de trouver chaque image correspondant au subtacted
	
	public void FoundImage(File[] files) {
		   if( !repertoir.exists()) {
			   for(int i=0;i<files.length;i++) {
				   subtracted[i] = IJ.openImage(files[i].getAbsolutePath());
				   if( subtracted[i]!=null) {
					   int expo_time_now=meta_data(json_txt);
						 
						 double N=((float)(valueSelected_time_expo)/(float)(expo_time_now));
					   subtracted_Co[i]=Image_Calculator_Color(subtracted[i],lut,lutmax,N);
					   json_txt = subtracted[i].getProp("ImageDescription");
						  
						  if(json_txt == null ) {
					             IJ.showMessage("Error", "cannot extract meta field from file.");
					             return;
					            }
						  
						  frameId_Sub[i]=frameId_for_Image(json_txt);
						  
				   }
			   }
			   
		   }else if(repertoir.exists() && brightfiled_File.length==0) {
			   for(int i=0;i<files.length;i++) {
				   subtracted[i] = IJ.openImage(files[i].getAbsolutePath());
				   if( subtracted[i]!=null) {
					   
					   int expo_time_now=meta_data(json_txt);
						 
						 double N=((float)(valueSelected_time_expo)/(float)(expo_time_now));
					   subtracted_Co[i]=Image_Calculator_Color(subtracted[i],lut,lutmax,N);
					   
					   json_txt = subtracted[i].getProp("ImageDescription");
						  
						  if(json_txt == null ) {
					             IJ.showMessage("Error", "cannot extract meta field from file.");
					             return;
					            }
			
						  frameId_Sub[i]=frameId_for_Image(json_txt);
				   }
			   }
		   } else {
			   for(int i=0;i<files.length;i++) {
				 
					 subtracted[i] = IJ.openImage(files[i].getAbsolutePath());
					  json_txt = subtracted[i].getProp("ImageDescription");
					  
					  if(json_txt == null ) {
				             IJ.showMessage("Error", "cannot extract meta field from file.");
				             return;
				            }
			
					 frameId_Sub[i]=frameId_for_Image(json_txt);
					 int expo_time_now=meta_data(json_txt);
					 
					 double N=((float)(valueSelected_time_expo)/(float)(expo_time_now));
					 
					 //JOptionPane.showMessageDialog(null,"valueSelected_time_expo: "+valueSelected_time_expo);
					 //JOptionPane.showMessageDialog(null,"expo_time_now: "+expo_time_now);
					 //JOptionPane.showMessageDialog(null,"N: "+N);
					 
					 //r�cuperation des images brightfield
					  
					  for(int j=0; j<brightfiled_File.length;j++) {
						  brightfiled[j] = IJ.openImage(brightfiled_File[j].getAbsolutePath());
						  json_txt1=brightfiled[j].getProp("ImageDescription");
						  if(json_txt1 == null ) {
					             IJ.showMessage("Error", "cannot extract meta field from file.");
					             return;
					            }
						  
						  frameId_Br[j]=frameId_for_Image(json_txt1);
						  if( frameId_Br[j].equals(frameId_Sub[i])) {
							  //conversion de l'image brightfield en RVB 24 bits
							  brightfiled_RVB[j]=CovertTO_Brightfield_Rgb(brightfiled[j]);
							//conversion de l'image subtracted en RVB color 24 bits
							 subtracted_Color[j]= Image_Calculator(subtracted[i],lut,lutmax,optionSelected,N);
							 
							 //Fusion des deux images
							 overlays[j]=Overlay_images(subtracted_Color[j],brightfiled_RVB[j],optionSelected);	
							  break;
						  }
					  }
		   }
		     	  
		}
	}
	
	// Fonction permettant de r�cup�rer le frameId des images
	@SuppressWarnings("unused")
	public String frameId_for_Image(String json_txt) {
		String frameId = null;
		
		try {
			int open_braces = 0;
		      int close_braces = 0;

		      // Handle missing last char in KC3.0.
		      for( int i1 = 0; i1 < json_txt.length(); i1++ ) {
		      char c = json_txt.charAt(i1);
		      if( c == '{' ) open_braces += 1;
		      if( c == '}' ) close_braces += 1;
		      }
		      if( open_braces != close_braces) {
		         json_txt = json_txt + "}";
		      }
			JSONObject json = new JSONObject(json_txt);
			
			if( json == null ) {
		         IJ.showMessage("Error", "cannot parse meta field from file: no kiscontrol metadata found.");
			}
			 frameId=json.getJSONObject("frame_id").getString("String");
			 
			// expo_fluo=json.getJSONObject("expo_fluo").getInt("F64");
			// JOptionPane.showMessageDialog(null,expo_fluo);
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return frameId;
		
	}
	
	
	//Fonction permettant de r�cuperer les images brightfield
	public File[] foundBrightfield(File Path) {
		
		 repertoir=new File(Path.getAbsoluteFile().toString().replaceFirst("subtracted", "brightfield"));
		 if(repertoir.exists()) {
			 repertory=repertoir.listFiles();
		 }
		
		 return repertory;
	}
	
	//conversion d'une image brightfield en rvb 24 bits 
	public ImagePlus CovertTO_Brightfield_Rgb(ImagePlus brightfield) {
		ImageProcessor ip=brightfield.getProcessor();
		 ColorProcessor cp = new ColorProcessor(ip.getWidth(), ip.getHeight());
	        // Parcourir chaque pixel et diviser la valeur 16 bits par 256
	        for (int x = 0; x < ip.getWidth(); x++) {
	            for (int y = 0; y < ip.getHeight(); y++) {
	                int value = ip.getPixel(x, y);
	                int r = value / 256;
	                int g = value / 256;
	                int b = value / 256;
	                int rgb = (r << 16) + (g << 8) + b; // Combinaison des canaux RGB
	                cp.putPixel(x, y, rgb); // Ajout du pixel � l'image RGB
	            }
	        }
	        //Mettre � jour l'image brightfield pour obtenir une image brightfield_rgb
	        brightfield.setProcessor(cp);;
		  
		return brightfield;
	}
	
	
	//Conversion de l'image subtracted en RVB color
	public ImagePlus Image_Calculator(ImagePlus image_sub,LUT _lut_,double lutMax,String option,double N) {
	    ImageProcessor ip = image_sub.getProcessor();
	    ImageProcessor byteIp = ip.convertToByte(true);

	       
	       if( _lut_ !=null) {
	    	   
	    	   ip.setLut(_lut_);
	        		      
	    	   IndexColorModel icm = (IndexColorModel) ip.getLut().getColorModel();
		          
		          int mapSize = icm.getMapSize();
		          byte[] reds = new byte[mapSize];
		          byte[] greens = new byte[mapSize];
		          byte[] blues = new byte[mapSize];
		          icm.getReds(reds);
		          icm.getGreens(greens);
		          icm.getBlues(blues);
		          
		          
		         
		          for (int i = 0; i < mapSize; i++) {
		        	 double  value = ((double)i / (double)mapSize);
		              if (value > lutMax*65535) {
		                  reds[i] = (byte) ((byte)(lutMax * 255.0)*N);
		                  greens[i] = (byte) ((byte)(lutMax * 255.0)*N);
		                  blues[i] = (byte) ((byte)(lutMax * 255.0)*N);
		              }
		            
		          }
		         
		          IndexColorModel newIcm = new IndexColorModel(8, mapSize, reds, greens, blues);
		          
		          LUT newLut = new LUT(newIcm, ip.getLut().min, (int) (lutMax * 255));
		          byteIp.setLut(newLut);

		        ImageProcessor ipRGB=byteIp.convertToRGB();
		        image_sub.setProcessor(ipRGB);
		        
	    	   
	       }else {
	    	   IJ.showMessage("Sorry but the LUT max and LUT color are required !");
	       }	         
	    
	    return image_sub;
	}

	
	// Fusion des deux images en une image composite
	
public ImagePlus Overlay_images(ImagePlus subtracted_rgb,ImagePlus brightfield_rgb,String option) {
		
		ImageProcessor ip_SubtractedRGB=subtracted_rgb.getProcessor();
		ImageProcessor ip_BrightfieldRGB=brightfield_rgb.getProcessor();
		
		int [] SubtractedRGB_Pixels=(int[]) ip_SubtractedRGB.getPixels();
		int [] BrightfieldRGB_Pxels=(int[]) ip_BrightfieldRGB.getPixels();
		
		int width=ip_SubtractedRGB.getWidth();
		int height=ip_SubtractedRGB.getHeight();
		
		ColorProcessor OverlayProcessor=new ColorProcessor(width,height);
		
		
		
		if(option.equals("or")) {
		 for (int y = 0; y < height; y++) {
	            for (int x = 0; x < width; x++) {
	                int index = y * width + x;
	                
	                // Extract the color components for each pixel
	                int rSubtractedColor = (SubtractedRGB_Pixels[index] >> 16) & 0xff;
	                int gSubtractedColor = (SubtractedRGB_Pixels[index] >> 8) & 0xff;
	                int bSubtractedColor = SubtractedRGB_Pixels[index] & 0xff;
	                
	                int rBrightfieldRGB = (BrightfieldRGB_Pxels[index] >> 16) & 0xff;
	                int gBrightfieldRGB = (BrightfieldRGB_Pxels[index] >> 8) & 0xff;
	                int bBrightfieldRGB = BrightfieldRGB_Pxels[index] & 0xff;
	                
	                
	              
	                int rOverlay = rSubtractedColor | rBrightfieldRGB;
	                int gOverlay = gSubtractedColor | gBrightfieldRGB;
	                int bOverlay = bSubtractedColor | bBrightfieldRGB;
	               
	                
	                // Combine the overlay values into a single RGB pixel value
	                int rgbOverlay = (rOverlay << 16) | (gOverlay << 8) | bOverlay;
	                
	                // Set the pixel value in the overlay image
	                OverlayProcessor.set(index, rgbOverlay);
	            }
	        }
		}else if(option.equals("Add")) {
			for (int y = 0; y < height; y++) {
	            for (int x = 0; x < width; x++) {
	                int index = y * width + x;
	                
	                // Extract the color components for each pixel
	                int rSubtractedColor = (SubtractedRGB_Pixels[index] >> 16) & 0xff;
	                int gSubtractedColor = (SubtractedRGB_Pixels[index] >> 8) & 0xff;
	                int bSubtractedColor = SubtractedRGB_Pixels[index] & 0xff;
	                
	                int rBrightfieldRGB = (BrightfieldRGB_Pxels[index] >> 16) & 0xff;
	                int gBrightfieldRGB = (BrightfieldRGB_Pxels[index] >> 8) & 0xff;
	                int bBrightfieldRGB = BrightfieldRGB_Pxels[index] & 0xff;
	                
	                // Apply the overlay formula to each color channel
	    
	                
	                int rOverlay = rSubtractedColor + rBrightfieldRGB;
	                int gOverlay = gSubtractedColor + gBrightfieldRGB;
	                int bOverlay = bSubtractedColor + bBrightfieldRGB;
	                
	                
	                // Combine the overlay values into a single RGB pixel value
	                int rgbOverlay = (rOverlay << 16) | (gOverlay << 8) | bOverlay;
	                
	                // Set the pixel value in the overlay image
	                OverlayProcessor.set(index, rgbOverlay);
	            }
	        }
		}else if(option.equals("Soustract")) {
			for (int y = 0; y < height; y++) {
	            for (int x = 0; x < width; x++) {
	                int index = y * width + x;
	                
	                // Extract the color components for each pixel
	                int rSubtractedColor = (SubtractedRGB_Pixels[index] >> 16) & 0xff;
	                int gSubtractedColor = (SubtractedRGB_Pixels[index] >> 8) & 0xff;
	                int bSubtractedColor = SubtractedRGB_Pixels[index] & 0xff;
	                
	                int rBrightfieldRGB = (BrightfieldRGB_Pxels[index] >> 16) & 0xff;
	                int gBrightfieldRGB = (BrightfieldRGB_Pxels[index] >> 8) & 0xff;
	                int bBrightfieldRGB = BrightfieldRGB_Pxels[index] & 0xff;
	                
	                // Apply the overlay formula to each color channel
	    
	                
	                int rOverlay = rSubtractedColor - rBrightfieldRGB;
	                int gOverlay = gSubtractedColor - gBrightfieldRGB;
	                int bOverlay = bSubtractedColor - bBrightfieldRGB;
	                
	                
	                // Combine the overlay values into a single RGB pixel value
	                int rgbOverlay = (rOverlay << 16) | (gOverlay << 8) | bOverlay;
	                
	                // Set the pixel value in the overlay image
	                OverlayProcessor.set(index, rgbOverlay);
	            }
	        }
		}else if(option.equals("Multiply")) {
			for (int y = 0; y < height; y++) {
	            for (int x = 0; x < width; x++) {
	                int index = y * width + x;
	                
	                // Extract the color components for each pixel
	                int rSubtractedColor = (SubtractedRGB_Pixels[index] >> 16) & 0xff;
	                int gSubtractedColor = (SubtractedRGB_Pixels[index] >> 8) & 0xff;
	                int bSubtractedColor = SubtractedRGB_Pixels[index] & 0xff;
	                
	                int rBrightfieldRGB = (BrightfieldRGB_Pxels[index] >> 16) & 0xff;
	                int gBrightfieldRGB = (BrightfieldRGB_Pxels[index] >> 8) & 0xff;
	                int bBrightfieldRGB = BrightfieldRGB_Pxels[index] & 0xff;
	                
	                // Apply the overlay formula to each color channel
	    
	                
	                int rOverlay = rSubtractedColor * rBrightfieldRGB;
	                int gOverlay = gSubtractedColor * gBrightfieldRGB;
	                int bOverlay = bSubtractedColor * bBrightfieldRGB;
	                
	                // Combine the overlay values into a single RGB pixel value
	                int rgbOverlay = (rOverlay << 16) | (gOverlay << 8) | bOverlay;
	                
	                // Set the pixel value in the overlay image
	                OverlayProcessor.set(index, rgbOverlay);
	            }
	        }
		}else if(option.equals("and")) {
			for (int y = 0; y < height; y++) {
	            for (int x = 0; x < width; x++) {
	                int index = y * width + x;
	                
	                // Extract the color components for each pixel
	                int rSubtractedColor = (SubtractedRGB_Pixels[index] >> 16) & 0xff;
	                int gSubtractedColor = (SubtractedRGB_Pixels[index] >> 8) & 0xff;
	                int bSubtractedColor = SubtractedRGB_Pixels[index] & 0xff;
	                
	                int rBrightfieldRGB = (BrightfieldRGB_Pxels[index] >> 16) & 0xff;
	                int gBrightfieldRGB = (BrightfieldRGB_Pxels[index] >> 8) & 0xff;
	                int bBrightfieldRGB = BrightfieldRGB_Pxels[index] & 0xff;
	                
	                // Apply the overlay formula to each color channel
	    
	                
	                int rOverlay = rSubtractedColor & rBrightfieldRGB;
	                int gOverlay = gSubtractedColor & gBrightfieldRGB;
	                int bOverlay = bSubtractedColor & bBrightfieldRGB;
	                
	                
	                // Combine the overlay values into a single RGB pixel value
	                int rgbOverlay = (rOverlay << 16) | (gOverlay << 8) | bOverlay;
	                
	                // Set the pixel value in the overlay image
	                OverlayProcessor.set(index, rgbOverlay);
	            }
	        }
		}else {
			for (int y = 0; y < height; y++) {
	            for (int x = 0; x < width; x++) {
	                int index = y * width + x;
	                
	                // Extract the color components for each pixel
	                int rSubtractedColor = (SubtractedRGB_Pixels[index] >> 16) & 0xff;
	                int gSubtractedColor = (SubtractedRGB_Pixels[index] >> 8) & 0xff;
	                int bSubtractedColor = SubtractedRGB_Pixels[index] & 0xff;
	                
	                int rBrightfieldRGB = (BrightfieldRGB_Pxels[index] >> 16) & 0xff;
	                int gBrightfieldRGB = (BrightfieldRGB_Pxels[index] >> 8) & 0xff;
	                int bBrightfieldRGB = BrightfieldRGB_Pxels[index] & 0xff;
	                
	                // Apply the overlay formula to each color channel
	                
	                int rOverlay = Math.max(rSubtractedColor, rBrightfieldRGB);
	                int gOverlay = Math.max(gSubtractedColor, gBrightfieldRGB);
	                int bOverlay = Math.max(bSubtractedColor, bBrightfieldRGB);
	                
	                
	                // Combine the overlay values into a single RGB pixel value
	                int rgbOverlay = (rOverlay << 16) | (gOverlay << 8) | bOverlay;
	                
	                // Set the pixel value in the overlay image
	                OverlayProcessor.set(index, rgbOverlay);
	            }
	        }
		}
		
		overlay=new ImagePlus(subtracted_rgb.getTitle().replace("subtracted", "overlay").replace(".tif", "_")+option.toLowerCase()+"_"+"LT"+Math.round(lutmax*100));
		overlay.setTitle(subtracted_rgb.getTitle().replace("subtracted", "overlay").replace(".tif", "_")+option.toLowerCase()+"_"+"LT"+Math.round(lutmax*100));
		overlay.setProcessor(OverlayProcessor);
		 
		
		return overlay;
	}


//Fonction save 
	public void save_overlay_images(ImagePlus []imgs,File path,String option) {
	
	File directory = new File(path.getAbsoluteFile().toString().replaceFirst("subtracted", "kis_overlay_folder"));
	if (!directory.exists()) {
         boolean success = directory.mkdir();
         if (success) {
        	 JSONObject data = new JSONObject();
			 for(String frameId : frameId_Sub ) {
				 if(frameId!=null) {
				 try {
					 data.put("LUT",lut );
					 data.put("subtrated_path",path.getAbsoluteFile().toString() );
					 data.put("subtrated_frameId", frameId);
					 data.put("brightfield_path",path.getAbsoluteFile().toString().replaceFirst("subtracted", "brightfield") );
					 data.put("brightfield_frameId", frameId);
					 data.put("LUTmax", lutmax);
					 data.put("LUT_name", lutName);
					 data.put("Overlay_mode", optionSelected);
					 data.put("expo_time", valueSelected_time_expo);
					 
				 } catch (JSONException e1) {
					 e1.printStackTrace();
				 }
				 }
			 }
			 
        	 for(ImagePlus image:imgs) {
        		 if(image!=null) {
        			 //Enregistrement des images overlay
        			 FileSaver fs = new FileSaver(image);
        			 fs.saveAsTiff(directory.getAbsolutePath()+"/"+image.getTitle()+".tif");
        		 
        			 //Enregistrement des m�tadonn�esS
        			 String chemin=directory.getAbsolutePath()+"/"+image.getTitle()+".json";
        			 try (FileWriter fileWriter = new FileWriter(chemin)) {
        				   fileWriter.write(data.toString());
        				} catch (IOException e) {
        					 IJ.showMessage("Error while writing the JSON file !");
        				    e.printStackTrace();
        				}
        			 
        		 }
        	 }
            
           
            	JOptionPane.showMessageDialog(null, "Overlay image successfully saved in the kis_overlay_folder directory !", "Information", JOptionPane.INFORMATION_MESSAGE);
			
			 
		    
		  } else {
        	 IJ.error("Directory creation failure !");
         }
         }else {
        	 JSONObject data = new JSONObject();
			 for(String frameId : frameId_Sub ) {
				 if(frameId!=null) {
				 try {
					 data.put("LUT",lut );
					 data.put("subtrated_path",path.getAbsoluteFile().toString() );
					 data.put("subtrated_frameId", frameId);
					 data.put("brightfield_path",path.getAbsoluteFile().toString().replaceFirst("subtracted", "brightfield") );
					 data.put("brightfield_frameId", frameId);
					 data.put("LUTmax", lutmax);
					 data.put("LUT_name", lutName);
					 data.put("Overlay_mode", optionSelected);
					 data.put("expo_time", valueSelected_time_expo);
					 
				 } catch (JSONException e1) {
					 e1.printStackTrace();
				 }
				 }
			 }
			 
        	 for(ImagePlus image:imgs) {
        		 if(image!=null) {
        			 //Enregistrement des images overlay
        			 FileSaver fs = new FileSaver(image);
        			 fs.saveAsTiff(directory.getAbsolutePath()+"/"+image.getTitle()+".tif");
        		 
        			 //Enregistrement des m�tadonn�esS
        			 String chemin=directory.getAbsolutePath()+"/"+image.getTitle()+".json";
        			 try (FileWriter fileWriter = new FileWriter(chemin)) {
        				   fileWriter.write(data.toString());
        				} catch (IOException e) {
        					 IJ.showMessage("Error while writing the JSON file !");
        				    e.printStackTrace();
        				}
        			 
        		 }
        	 }
        	 
 				JOptionPane.showMessageDialog(null, "Overlay image successfully saved in the kis_overlay_folder directory !", "Information", JOptionPane.INFORMATION_MESSAGE);
 			 
            
         }

	 
}
	
//Image color 

	public ImagePlus Image_Calculator_Color(ImagePlus image_sub,LUT _lut_,double lutMax,double N) {
    ImageProcessor ip = image_sub.getProcessor();
    ImageProcessor byteIp = ip.convertToByte(true);

       
       if( _lut_ !=null) {
    	   
    	   ip.setLut(_lut_);
        		      
    	   IndexColorModel icm = (IndexColorModel) ip.getLut().getColorModel();
	          
	          int mapSize = icm.getMapSize();
	          byte[] reds = new byte[mapSize];
	          byte[] greens = new byte[mapSize];
	          byte[] blues = new byte[mapSize];
	          icm.getReds(reds);
	          icm.getGreens(greens);
	          icm.getBlues(blues);
	          
	          
	         
	          for (int i = 0; i < mapSize; i++) {
	        	 double  value = ((double)i / (double)mapSize);
	              if (value > lutMax*65535) {
	                  reds[i] = (byte) ((byte)(lutMax * 255.0)*N);
	                  greens[i] = (byte) ((byte)(lutMax * 255.0)*N);
	                  blues[i] = (byte) ((byte)(lutMax * 255.0)*N);
	              }
	            
	          }
	          
	          IndexColorModel newIcm = new IndexColorModel(8, mapSize, reds, greens, blues);
	          LUT newLut = new LUT(newIcm, ip.getLut().min, (int) (lutMax * 255));
	          byteIp.setLut(newLut);

	        ImageProcessor ipRGB=byteIp.convertToRGB();
	        image_sub.setProcessor(ipRGB);
	        image_sub.setTitle(image_sub.getTitle().replace("subtracted", "color"));    	   
       }else {
    	   IJ.showMessage("Sorry but the LUT max and LUT color are required !");
       }	         
    
    return image_sub;
}

	public void save_color_images(ImagePlus []imgs,File path,String option) {
		
		File directory = new File(path.getAbsoluteFile().toString().replaceFirst("subtracted", "kis_color_folder"));
		if (!directory.exists()) {
	         boolean success = directory.mkdir();
	         if (success) {
	        	 JSONObject data = new JSONObject();
				 for(String frameId : frameId_Sub ) {
					 if(frameId!=null) {
					 try {
						 data.put("LUT",lut );
						 data.put("subtrated_path",path.getAbsoluteFile().toString() );
						 data.put("subtrated_frameId", frameId);
						 data.put("LUTmax", lutmax);
						 data.put("LUT_name", lutName);
						 data.put("Overlay_mode", optionSelected);
						 data.put("expo_time", valueSelected_time_expo);
						 
					 } catch (JSONException e1) {
						 e1.printStackTrace();
					 }
					 }
				 }
				 
	        	 for(ImagePlus image:imgs) {
	        		 if(image!=null) {
	        			 //Enregistrement des images overlay
	        			 FileSaver fs = new FileSaver(image);
	        			 fs.saveAsTiff(directory.getAbsolutePath()+"/"+image.getTitle()+".tif");
	        		 
	        			 //Enregistrement des m�tadonn�esS
	        			 String chemin=directory.getAbsolutePath()+"/"+image.getTitle()+".json";
	        			 try (FileWriter fileWriter = new FileWriter(chemin)) {
	        				   fileWriter.write(data.toString());
	        				} catch (IOException e) {
	        					 IJ.showMessage("Error while writing the JSON file !");
	        				    e.printStackTrace();
	        				}
	        			 
	        		 }
	        	 }
	            
	           
	            	JOptionPane.showMessageDialog(null, "Overlay image successfully saved in the kis_overlay_folder directory !", "Information", JOptionPane.INFORMATION_MESSAGE);
				
				 
			    
			  } else {
	        	 IJ.error("Directory creation failure !");
	         }
	         }else {
	        	 JSONObject data = new JSONObject();
				 for(String frameId : frameId_Sub ) {
					 if(frameId!=null) {
					 try {
						 data.put("LUT",lut );
						 data.put("subtrated_path",path.getAbsoluteFile().toString() );
						 data.put("subtrated_frameId", frameId);
						 data.put("LUTmax", lutmax);
						 data.put("LUT_name", lutName);
						 data.put("Overlay_mode", optionSelected);
						 data.put("expo_time", valueSelected_time_expo);
						 
					 } catch (JSONException e1) {
						 e1.printStackTrace();
					 }
					 }
				 }
				 
	        	 for(ImagePlus image:imgs) {
	        		 if(image!=null) {
	        			 //Enregistrement des images overlay
	        			 FileSaver fs = new FileSaver(image);
	        			 fs.saveAsTiff(directory.getAbsolutePath()+"/"+image.getTitle()+".tif");
	        		 
	        			 //Enregistrement des m�tadonn�esS
	        			 String chemin=directory.getAbsolutePath()+"/"+image.getTitle()+".json";
	        			 try (FileWriter fileWriter = new FileWriter(chemin)) {
	        				   fileWriter.write(data.toString());
	        				} catch (IOException e) {
	        					 IJ.showMessage("Error while writing the JSON file !");
	        				    e.printStackTrace();
	        				}
	        			 
	        		 }
	        	 }
	        	 
	 				JOptionPane.showMessageDialog(null, "Overlay image successfully saved in the kis_color_folder directory !", "Information", JOptionPane.INFORMATION_MESSAGE);
	 			 
	            
	         }

		 
	}

	@SuppressWarnings("unused")
	public int meta_data(String json_txt){
			int expo_fluo = 0;
		try {
			int open_braces = 0;
		      int close_braces = 0;

		      // Handle missing last char in KC3.0.
		      for( int i1 = 0; i1 < json_txt.length(); i1++ ) {
		      char c = json_txt.charAt(i1);
		      if( c == '{' ) open_braces += 1;
		      if( c == '}' ) close_braces += 1;
		      }
		      if( open_braces != close_braces) {
		         json_txt = json_txt + "}";
		      }
			JSONObject json = new JSONObject(json_txt);
			
			if( json == null ) {
		         IJ.showMessage("Error", "cannot parse meta field from file: no kiscontrol metadata found.");
			}
			
		 expo_fluo=json.getJSONObject("expo_fluo").getInt("F64");
		 
		}catch (JSONException e) {
			e.printStackTrace();
		}
		return expo_fluo;
}
	
	public String tem_expo_saturation(ImagePlus[]images,int expo) {
		int img_count=0;
		for(int i=0;i<images.length;i++) {
			String json_txt_image=images[i].getProp("ImageDescription");
			int expo_time_now=meta_data(json_txt_image);
			ImageProcessor ip=images[i].getProcessor();
			int bitDepth = ip.getBitDepth();
	        int maxValue = (bitDepth == 8) ? 255 : 65535;
			double intensiteMax=ip.getMax();
			
	        double N=expo/expo_time_now;
	        
	        int desiredMax = (int) (intensiteMax*N);
	        if(desiredMax>maxValue) {
	        	img_count +=1;
	        }
	        
		}
		String response=(img_count==0)?"For the exposure time: "+expo+" there will be no saturated image":"For exposure time: "+expo+" you'll get "+img_count+" saturated images";
		//JOptionPane.showMessageDialog(null,response);
		return response;
	}
//end class
}
