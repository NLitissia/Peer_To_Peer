import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.JSpinner;

public class Interface {

	public JFrame frame;
	public JTextField Adresse_Ip;
	public JTextField Port;
	public JTextField Taille_fichier;
	public JTextField port1;
	public JTextField taille_fragment;
    public static	JTextArea Affichage; 
	static Server server;
	static Client client;
	static HashMap<String, Long> Fichier_re�u;
	static HashMap<String, Long> Fichier_recu;
	private Lock lock = new ReentrantLock();
	private JTextField Position;
	private JTextField nomfichier;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interface window = new Interface();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Interface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
	    
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setSize(1200, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel lblPeerToPeer = new JLabel("      Peer To Peer");
		lblPeerToPeer.setFont(new Font("Bitstream Charter", Font.BOLD | Font.ITALIC, 20));
		lblPeerToPeer.setBackground(Color.LIGHT_GRAY);
		lblPeerToPeer.setBounds(479, 29, 315, 42);
		panel.add(lblPeerToPeer);
		
		JLabel lblSaisirLadresseIp = new JLabel("Saisir l'adresse Ip");
		lblSaisirLadresseIp.setBounds(50, 80, 143, 27);
		panel.add(lblSaisirLadresseIp);
		
		Adresse_Ip = new JTextField();
		Adresse_Ip.setBounds(228, 84, 177, 19);
		panel.add(Adresse_Ip);
		Adresse_Ip.setColumns(10);
		Port = new JTextField();
		Port.setBounds(715, 80, 143, 19);
		panel.add(Port);
		Port.setColumns(10);
		
		JLabel lblNumrosDePort = new JLabel("Numéros de Port");
		lblNumrosDePort.setBounds(541, 80, 129, 27);
		panel.add(lblNumrosDePort);
		

		Affichage = new JTextArea();
		Affichage.setBounds(479, 169, 645, 475);
		panel.add(Affichage);
		
		JButton Connecter = new JButton("Connecter");
		Connecter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String adresse =  Adresse_Ip.getText();
				int port = Integer.parseInt(Port.getText());
				if(adresse != null)
				{
					try {
		
						client = new Client(adresse,port);
						Thread t = new Thread(client);
						t.start();
						 
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		Connecter.setBounds(987, 78, 117, 25);
		panel.add(Connecter);
		
		
		
		
		JButton Demander_paire = new JButton("Demander Liste_Paire");
		Demander_paire.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    try {
			    	lock.lock();
					client.Demande_Paires();
					lock.unlock();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		Demander_paire.setBounds(36, 211, 208, 25);
		panel.add(Demander_paire);
		
		JButton Demander_Fichier = new JButton("Demander Liste_Fichier");
		Demander_Fichier.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					 lock.lock();
					 client.Demander_Fichiers();
					 lock.unlock();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		Demander_Fichier.setBounds(36, 266, 208, 25);
		panel.add(Demander_Fichier);
		
		JLabel lblTelechargerUnFragment = new JLabel("Telecharger un fragment d'un fichier");
		lblTelechargerUnFragment.setBounds(12, 331, 343, 15);
		panel.add(lblTelechargerUnFragment);
		
		JLabel lblNomFichier = new JLabel("Nom Fichier");
		lblNomFichier.setBounds(22, 353, 117, 29);
		panel.add(lblNomFichier);
		
		JLabel lblPosition = new JLabel("Position");
		lblPosition.setBounds(32, 417, 79, 27);
		panel.add(lblPosition);
		
		JLabel lblTaillefichier = new JLabel("Taille_fichier");
		lblTaillefichier.setBounds(22, 388, 117, 27);
		panel.add(lblTaillefichier);
		
		Taille_fichier = new JTextField();
		Taille_fichier.setBounds(153, 392, 114, 19);
		panel.add(Taille_fichier);
		Taille_fichier.setColumns(10);
		
		port1 = new JTextField();
		port1.setColumns(10);
		port1.setBounds(277, 164, 114, 19);
		panel.add(port1);
		
		taille_fragment = new JTextField();
		taille_fragment.setBounds(153, 445, 114, 19);
		panel.add(taille_fragment);
		taille_fragment.setColumns(10);
		
		
		JLabel lblNewLabel = new JLabel("Taille_fragment");
		lblNewLabel.setBounds(12, 447, 117, 15);
		panel.add(lblNewLabel);
		
		Position = new JTextField();
		Position.setColumns(10);
		Position.setBounds(153, 421, 114, 19);
		panel.add(Position);
		
		nomfichier = new JTextField();
		nomfichier.setColumns(10);
		nomfichier.setBounds(153, 358, 114, 19);
		panel.add(nomfichier);
		JButton btnTlcharger = new JButton("Télécharger");
		btnTlcharger.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
			 try {
				lock.lock();
				client.Demander_Fragement(nomfichier.getText(), Long.parseLong(Taille_fichier.getText()), Long.parseLong(Position.getText()), Integer.parseInt(taille_fragment.getText()));
				lock.unlock();
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			}
		});
		btnTlcharger.setBounds(68, 497, 143, 25);
		panel.add(btnTlcharger);
		
	
		
		JLabel port = new JLabel("Port");
		port.setBounds(228, 164, 49, 19);
		panel.add(port);
		
		JButton DemandeAjout = new JButton("Demande d'ajout");
		DemandeAjout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					lock.lock();
					client.Demande_Ajout(Integer.parseInt(Port.getText()));
					lock.unlock();
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		DemandeAjout.setBounds(34, 164, 177, 25);
		panel.add(DemandeAjout);
		
		
		
		
	}
}
