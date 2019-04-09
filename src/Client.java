import java.awt.List;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.swing.JTextArea;

public class Client implements Runnable {
	public static final String PATH ="MesFichiers/";
	private Lock lock = new ReentrantLock();
	SocketChannel socketChannel;
	Server server;
	ByteBuffer buffer = ByteBuffer.allocate(70000);
	HashMap<Integer, String> Paires_connectes = new HashMap<Integer, String>();
	HashMap<Integer, String> Paires_recu;
	HashMap<String, Long> Fichier_recu;
	HashMap<String, Long> Fichiers_partages = new HashMap<String, Long>();

	public Client(String url, int port) throws IOException {
		SocketAddress adresse = new InetSocketAddress(url, port);
		server = new Server(1234);
		Thread t = new Thread(server);
		t.start();
		socketChannel = socketChannel.open();
		socketChannel.connect(adresse);

	}

	/**
	 * Fonction Demande_Paires: Demender les paires connectés
	 * 
	 * @return
	 */
	public void Demande_Paires() throws IOException { // System.out.println("boutton demande_paire");
		lock.lock();
		Interface.Affichage.setText(" ");
		buffer.clear();
		buffer.put((byte) 3);
		buffer.flip();
		socketChannel.write(buffer);
		lock.unlock();

	}

	/**
	 * Fonction Recuperer_Paires: Recuperer les paires connectés
	 */
	public void Recuperer_Paires() {
		Paires_recu = new HashMap<>();
		int nbpair = buffer.getInt();
		if (nbpair == 0) {
			System.out.println("aucune paire connecté");

		}
		

		Interface.Affichage.append("Le nombre de paires connectés est " + nbpair + "\n");
		Interface.Affichage.append(" Le numéros de port  ----->   URL" + "\n");
		Paires_recu = Deserialisation.desarialiserPaire(buffer, nbpair);
		for (int i : Paires_recu.keySet()) {
			Interface.Affichage.append(i + "--------------->" + Paires_recu.get(i) + "\n");
		}
	}

	/**
	 * Fonction Demande_Fichier : demande les fichiers partagés.
	 */
	public void Demander_Fichiers() throws IOException { 
		lock.lock();
		buffer.clear();
		buffer.put((byte) 5);
		buffer.flip();
		socketChannel.write(buffer);
		lock.unlock();
	}

	/**
	 * Fonction Recuperer_Fichier : Recuperer les fichier partagés
	 */
	public void Recuperer_Fichier() {

		int nbfichier = buffer.getInt();
		Interface.Affichage.setText(" ");
		Interface.Affichage.append("Le nombre de fichier  est " + nbfichier + "\n");
		Interface.Affichage.append(" Nom du fichier -------------->   Taille" + "\n");
		if (nbfichier == 0) {
			System.out.println("Aucun fichier retrouver ");
			return;

		}
		System.out.println("nombre de fichiers " + nbfichier);
		Fichier_recu = Deserialisation.desarialiserficher(buffer, nbfichier);
		for (String s : Fichier_recu.keySet()) {
			Interface.Affichage.append(s + "   ----------------> " + Fichier_recu.get(s) + "\n");
		}

	}

	/**
	 * Demander_fragments : demander un fragments des fichiers partagés , en
	 * envoyant le nom du fichier la taille totale et la position du début du
	 * fragment et la taille à recuperer
	 */
	public void Demander_Fragement(String nom_fichier, long taille_fichier, long position, int taille_fragment)
			throws IOException {
        System.out.println(" boutton je suis dans demande fragmenet");
		lock.lock();
		buffer.clear();
		buffer.put((byte) 7);
		Serialisation.Serialiserchaine(nom_fichier, buffer);
		buffer.putLong(taille_fichier);
		buffer.putLong(position);
		buffer.putInt(taille_fragment);
		buffer.flip();
		socketChannel.write(buffer);
		lock.unlock();

	}

	/**
	 * Recuperer_fragment : recupere le fragment aprés avoir choisi un fichier
	 * 
	 */

	public void Recuperer_Fragment() throws SQLException, IOException {
        lock.lock();
		String nom_fichier = Deserialisation.desarialisationchaine(buffer);
		long taille_fichier = buffer.getLong();
		long position = buffer.getLong();
		int taille_fragment = buffer.getInt();
		byte[] blob = new byte[taille_fragment];
		for (int i = 0; i < taille_fragment; i++) {
			blob[i] = buffer.get();
		}
		File file = new File(PATH + nom_fichier);
		FileOutputStream fo = new FileOutputStream(file);
		fo.write(blob);
		fo.flush();
		fo.close();
		FileInputStream fileInputStream = new FileInputStream(file);
		lock.unlock();

	}

	/**
	 * 
	 * Demande d'ajout a la liste des paire
	 */
	public void Demande_Ajout(int port) throws IOException {
		lock.lock();
		buffer.clear();
		buffer.put((byte) 2);
		buffer.putInt(port);
		buffer.flip();
		socketChannel.write(buffer);
		Interface.Affichage.setText(" ");
		Interface.Affichage.append("\n"+"Votre demande d'ajout à été bien envoyé");
		lock.unlock();

	}

	public void Recevoir(byte id) {
		String message = Deserialisation.desarialisationchaine(buffer);
		Interface.Affichage.append("ID" + "=" + id + " " + message +"\n");
		buffer.clear();
	}

	public  void run() {

		while (socketChannel.isConnected()) {
			buffer.clear();
			try {
				lock.lock();
				int n = socketChannel.read(buffer);
				lock.unlock();

				if (n < 0) {
					System.out.println("Buffer vide rien a lire ");
				} else {
					buffer.flip();
					byte id = buffer.get();

					if (id == 1) {
						Recevoir(id);
					}

					if (id == 4) {
						Recuperer_Paires();
					}
					if (id == 6) {
						Recuperer_Fichier();

					}

					if (id == 8) {
						Recuperer_Fragment();
					}
					if (id == 3) {
						System.out.println("##### Demande de la liste des paires #####");
						server.fournir_listePaire(socketChannel);

					}

					if (id == 5) {
						System.out.println("##### Demande de la liste des fichiers #####");
						server.fournir_listeFichier(socketChannel);

					}
					if (id == 7) {
						System.out.println("##### Demande de la liste d'un fragment #####");
						server.fragment_demander(socketChannel, buffer);

					}
					
					
					if (id > 8) {
						lock.lock();
						buffer.clear();
						buffer.put((byte) 1);
						Serialisation.Serialiserchaine("Erreur , requéte invalide -Deconnexion ", buffer);
						buffer.flip();
						socketChannel.write(buffer);
						socketChannel.close();
					    lock.unlock();	
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public static void main(String[] args) throws IOException, SQLException {

		Client c = new Client("prog-reseau-m1.lacl.fr", 5486);
		Thread t1 = new Thread(c);
		t1.start();

	}

}
