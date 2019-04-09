import java.awt.image.ByteLookupTable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server implements Runnable {
	/**
	 * la classe Server
	 */
	static public final Charset c = Charset.forName("UTF-8");
	public static final String PATH ="../MesFichiers/";
	ServerSocketChannel serversocketchannel;
	Selector selector;
	ByteBuffer bytebuffer = ByteBuffer.allocateDirect(70000);;
	private Lock lock = new ReentrantLock();
	HashMap<Integer, String> Paires_connectes = new HashMap<Integer,String>();
	HashMap<String, Long> Fichiers_partages = new HashMap<String,Long>();

	/**
	 * Constructeur Selctor permet de surveiller plusieurs cannaux
	 * 
	 * @param port
	 * @throws IOException
	 */
	public Server(int port) throws IOException {
		serversocketchannel = ServerSocketChannel.open();
		selector = Selector.open();
		SocketAddress sa = new InetSocketAddress(port);
		serversocketchannel.bind(sa);
		serversocketchannel.configureBlocking(false);
		serversocketchannel.register(selector, SelectionKey.OP_ACCEPT);

	}

	/**
	 * Accepter la connexion et envoyer un Id = 1 et un message
	 * 
	 * @throws IOException
	 */
	public void accept() throws IOException {
		bytebuffer.clear();
		SocketChannel socketChannel = serversocketchannel.accept();
		bytebuffer.put((byte) 1);
		String reponse = "You are connected to " + socketChannel.getRemoteAddress().toString() + " From "
				+ socketChannel.socket().getInetAddress().toString();
		Serialisation.Serialiserchaine(reponse, bytebuffer);
		bytebuffer.flip();
		socketChannel.write(bytebuffer);
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_READ);

	}

	/**
	 * Tant que le client et connecté on verifie le bytebuffer si le client demande
	 * , On lui offre le service qu'il veut
	 * 
	 * @param sk
	 */

	/**
	 * Envoyer la liste des paires connectés
	 * 
	 * @throws IOException
	 */
	public void fournir_listePaire(SocketChannel socketChannel) throws IOException {
        System.out.println("-------Envois de la liste des paires connectées-------");
        lock.lock();
		Paires_connectes.put(1234, socketChannel.getRemoteAddress().toString());
		bytebuffer.clear();
		bytebuffer.put((byte) 4);
		bytebuffer.putInt(Paires_connectes.size());
		Serialisation.SerialisationListClient(Paires_connectes, bytebuffer);
		bytebuffer.flip();
		socketChannel.write(bytebuffer);
		lock.unlock();
		
	}

	/**
	 * Fournir la liste des fichier
	 */
	public void Recuperer_MesFichiers() {
		lock.lock();
		File repertoire = new File(PATH);
		//System.out.println(PATH);
		File[] files = repertoire.listFiles();
		for (int i = 0; i < files.length; i++) {
			Fichiers_partages.put(files[i].getName(), files[i].length());
			System.out.println(files[i].getName().toString());
		}
		lock.unlock();
	}

	/**
	 * Fournir la liste des fichier au serveur demandeur
	 * 
	 * @throws IOException
	 */

	public synchronized void fournir_listeFichier(SocketChannel socketChannel) throws IOException {
		System.out.println("-------Envois de la liste des fichiers---------------");
		lock.lock();
		Recuperer_MesFichiers();
		bytebuffer.clear();
		bytebuffer.put((byte) 6);
		bytebuffer.putInt(Fichiers_partages.size());
		Serialisation.SerialisationListFichier(Fichiers_partages, bytebuffer);
		bytebuffer.flip();
		socketChannel.write(bytebuffer);
		lock.unlock();
	}
/**
 * Recuperer le fragmenet demander
 * @param socketChannel
 * @param buffer
 * @throws IOException
 */
	public  void fragment_demander(SocketChannel socketChannel,ByteBuffer buffer) throws IOException
   {       
		    lock.lock();
   			String fichier_demander = Deserialisation.desarialisationchaine(buffer);
   			int Taille_total =(int)buffer.getLong();
   			long debut = buffer.getLong();
   			int taille_demander = buffer.getInt();
   			System.out.println(fichier_demander +" " +Taille_total +"   "+debut+ "  " +taille_demander);
   			byte Blob[] = new byte[taille_demander];
   			FileInputStream file  = new FileInputStream(new File(PATH+fichier_demander));
   			file.getChannel().position(debut);
	
   			for(int i =0;i<taille_demander;i++)
   			{
   				Blob[i] = (byte) file.read();
   			}
   			buffer.clear();
   			buffer.put((byte)8);
   			Serialisation.Serialiserchaine(fichier_demander, buffer);
   			buffer.putLong(Taille_total);
   			buffer.putLong(debut);
   			buffer.putInt(taille_demander);
   			buffer.put(Blob);
   			buffer.flip();
   			socketChannel.write(buffer);
   			lock.unlock();
	
   }

	public void repeat(SelectionKey sk) throws IOException {
		SocketChannel sc = (SocketChannel) sk.channel();
		bytebuffer.clear();
		if (sc.read(bytebuffer) > 0) {
			bytebuffer.flip();
			byte ID = bytebuffer.get();
			System.out.println(ID);

			if (ID == 2) {
				System.out.println("-------Ajouter à  la liste des paires connectées--------");
				int port = bytebuffer.getInt();
				Paires_connectes.put(port, (sc.socket().getInetAddress().getHostName()).toString());
				bytebuffer.clear();
				bytebuffer.put((byte) 1);
				sc.write(bytebuffer);
				for (int i : Paires_connectes.keySet()) {
					System.out.println(Paires_connectes.get(i) + " " + i);
				}

			}
			if (ID == 3) {
				System.out.println("-------Envois de la liste des paires connectées-------");
				fournir_listePaire(sc);
			}
			if (ID == 5) {
				System.out.println("-------Envois de la liste des fichiers---------------");
				fournir_listeFichier(sc);

			}
			if (ID == 7) {
				System.out.println("-------Envois du fragment demander ---------------");
				fragment_demander(sc,bytebuffer);

			}
			if (ID > 8) {
				bytebuffer.clear();
				bytebuffer.put((byte) 1);
				Serialisation.Serialiserchaine("Erreur , requéte invalide -Deconnexion ", bytebuffer);
				bytebuffer.flip();
				sc.write(bytebuffer);
				sc.close();
			}
		}
	}
	// }

	public void run() {
		while (true) {
			try {
				selector.select();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (SelectionKey sk : selector.selectedKeys()) {
				if (sk.isAcceptable()) {
					try {
						System.out.println("nouvelle connexion");
						accept();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (sk.isReadable()) {

					try {
						repeat(sk);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			selector.selectedKeys().clear();

		}

	}

	public static void main(String[] args) throws IOException, SQLException {
        System.out.println(PATH);
		Server server = new Server(1234);
		Thread t1 = new Thread(server);
		t1.start();

	}

}