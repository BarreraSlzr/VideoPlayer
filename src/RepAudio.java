import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.media.*;
import javax.media.format.AudioFormat;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import net.sourceforge.jffmpeg.demux.ogg.OggDemux;

@SuppressWarnings("serial")
public class RepAudio extends JApplet implements ActionListener {
	static JLabel cancion = new JLabel("Reproduciendo ");
	static Font fuente = new Font("Courier", Font.PLAIN, 24);
	static JPanel panBot = new JPanel();
	static JButton repaus  = new JButton(),
			       detiene = new JButton();
	static boolean pausa = false, close = true;
	Icon pause, play, stop;
	JFileChooser arch = new JFileChooser();
	String song = null;
	Player reproductor;
	URL url = null;
	//Componentes
	Component videos, controles;
	
	public RepAudio() {
		//super("Reproductor de Audio");
		
		cancion.setFont(fuente);
		cancion.setHorizontalAlignment(SwingConstants.CENTER);
		
		pause = new ImageIcon(getClass().getResource("Pause.png"));
		play = new ImageIcon(getClass().getResource("Play.jpg"));
		stop = new ImageIcon(getClass().getResource("Stop.jpg")); 
		repaus.setIcon( play );
		detiene.setIcon( stop );
		repaus.addActionListener(this);
		detiene.addActionListener(this);
		
		FileNameExtensionFilter audioFilter = new FileNameExtensionFilter(
		        "Archivos de Audio (*.mp3, *.ogg)", "mp3", "ogg");
                FileNameExtensionFilter videoFilter = new FileNameExtensionFilter(
                        "Archivos de Video (*.avi)", "avi");
		arch.addChoosableFileFilter(audioFilter);
                arch.addChoosableFileFilter(videoFilter);
                arch.removeChoosableFileFilter(arch.getAcceptAllFileFilter());
                
		
		int val = arch.showOpenDialog(null);
		
		try {
			url = arch.getSelectedFile().toURI().toURL();
			close = false;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if( val == JFileChooser.APPROVE_OPTION) {
		    cancion.setText("Reproduciendo " + arch.getSelectedFile().getName() );
		}	
		
            Format input1 = new AudioFormat(AudioFormat.MPEGLAYER3);
	    Format input2 = new AudioFormat(AudioFormat.MPEG_RTP);
	    Format input3 = new AudioFormat(AudioFormat.MPEG);
	    Format output = new AudioFormat(AudioFormat.LINEAR);
	    PlugInManager.addPlugIn(
	        "com.sun.media.codec.audio.mp3.JavaDecoder",
	        new Format[]{input1, input2, input3},
	        new Format[]{output},
	        PlugInManager.CODEC
	    );
	    
	    String JFFMPEG_AUDIO = "net.sourceforge.jffmpeg.AudioDecoder";
	    Codec audio=null;
		try {
			audio = (Codec)Class.forName( JFFMPEG_AUDIO ).newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e1) {
			e1.printStackTrace();
		}
        
	    PlugInManager.addPlugIn( JFFMPEG_AUDIO,
	    		audio.getSupportedInputFormats(),
	    		audio.getSupportedInputFormats(),
	    		PlugInManager.CODEC 
	    );
            
            PlugInManager.addPlugIn("com.sun.media.codec.video.cinepak.JavaDecoder",
                        new Format[] { new VideoFormat("cvid", null, -1,
                                        Format.byteArray, -1.0f), },
                        new Format[] { new RGBFormat(null, -1,
                                        Format.intArray, -1.0f, 32, 0xff, 0xff00,
                                        0xff0000, 1, -1, 0, -1), },
                        PlugInManager.CODEC);
            
	    //Demultiplexer, Formato y CODEC para OGG
            Format[] informat= new OggDemux().getSupportedInputContentDescriptors();
            PlugInManager.addPlugIn("net.sourceforge.jffmpeg.demux.ogg.OggDemux",informat,null,PlugInManager.DEMULTIPLEXER);
	    String JFFMPEG_OGG = "net.sourceforge.jffmpeg.codecs.audio.vorbis.VorbisDecoder";
	    Codec audioOGG=null;
		try {
			audioOGG = (Codec)Class.forName( JFFMPEG_OGG ).newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e1) {
			e1.printStackTrace();
		}
        
	    PlugInManager.addPlugIn( JFFMPEG_OGG,
	    		audioOGG.getSupportedInputFormats(),
	    		audioOGG.getSupportedInputFormats(),
	    		PlugInManager.CODEC 
	    );
            
            
            
//            String JAVE_MEDIA = "it.sauronsoftware.jave.Encoder";
//	    Codec javeMedia=null;
//		try {
//			javeMedia = (Codec)Class.forName( JAVE_MEDIA ).newInstance();
//		} catch (InstantiationException | IllegalAccessException
//				| ClassNotFoundException e1) {
//			e1.printStackTrace();
//		}
//        
//	    PlugInManager.addPlugIn( JAVE_MEDIA,
//	    		javeMedia.getSupportedInputFormats(),
//	    		javeMedia.getSupportedInputFormats(),
//	    		PlugInManager.CODEC
//	    );
		try {
				reproductor = Manager.createRealizedPlayer( new MediaLocator( url ) );
				reproductor.prefetch();
			    videos = reproductor.getVisualComponent();
			    controles = reproductor.getControlPanelComponent();
			    
			    if ( videos != null ) {
			       add( videos, BorderLayout.CENTER );
			       setSize(1080, 720);
			    }
			    else setSize(500, 200);

		} catch (NoPlayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (CannotRealizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		panBot.setLayout( new GridLayout(1, 2, 5, 5) );
		panBot.add(repaus);
		panBot.add(detiene);
		add( cancion, BorderLayout.NORTH );
		add( controles, BorderLayout.SOUTH );
		setLocation(300, 200);
		setVisible(true);
	}
// 
//    private AudioFormat getOutFormat(AudioFormat inFormat) {
//        final int ch = inFormat.getChannels();
//        final float rate = (float) inFormat.getSampleRate();
//        return new AudioFormat(, rate, 16, ch, ch * 2, rate, false);
//    }
// 
//    private void stream(AudioInputStream in, SourceDataLine line) 
//        throws IOException {
//        final byte[] buffer = new byte[4096];
//        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
//            line.write(buffer, 0, n);
//        }
//    }

	public static void main(String[] args) {
        Component miApp = new RepAudio();
        JFrame marco = new JFrame("Reproductor MP3 y AVI de Interaccion Humano Máquina");
        marco.getContentPane().add(miApp);
        marco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        marco.setSize(700, 500);
        marco.pack();
        marco.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == repaus && !pausa ) {
			if (close) {
				int val = arch.showOpenDialog(null);
				try {
					url = arch.getSelectedFile().toURI().toURL();
					close = false;
				} catch (MalformedURLException mfe) {
					mfe.printStackTrace();
				}
				if( val == JFileChooser.APPROVE_OPTION) {
				    cancion.setText("Reproduciendo " + arch.getSelectedFile().getName() );
				}	
				try {
					reproductor = Manager.createRealizedPlayer( new MediaLocator( url ) );
					reproductor.prefetch();
				    videos = reproductor.getVisualComponent();
				    detiene.setEnabled(true);
				    if ( videos != null ) {
				       add( videos, BorderLayout.CENTER );
				       setSize(1080, 720);
				    }
				    else setSize(500, 200);

				} catch (NoPlayerException npe) {
					// TODO Auto-generated catch block
					npe.printStackTrace();
				} catch (IOException ioe) {
					// TODO Auto-generated catch block
					ioe.printStackTrace();
				} catch (CannotRealizeException cre) {
				// TODO Auto-generated catch block
				  cre.printStackTrace();
				}
			}
			repaus.setIcon(pause);
			pausa = true;
			reproductor.prefetch();
			reproductor.start();
		}
		else if( e.getSource() == repaus && pausa ) {
			repaus.setIcon(play);
                        reproductor.stop();
			pausa = false;			
		}
		else if( e.getSource() == detiene ) {
			repaus.setIcon(play);
			reproductor.close();
			reproductor.deallocate();
			detiene.setEnabled(false);
			setBackground(Color.WHITE);
			videos = null;
			close = true;
			pausa = false;
		}
	}

}
