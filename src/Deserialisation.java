import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;

public class Deserialisation implements Serializable {
	public static final Charset c = Charset.forName("UTF-8");
/**
 * String deserialisationChaine : recupere le contenu du buffer et le deserialise
 * @param buffer
 * @return
 */
	public synchronized static String desarialisationchaine(ByteBuffer buffer) {
        int n = buffer.getInt();
		int lim = buffer.limit();
		buffer.limit(buffer.position() + n);
		String message = c.decode(buffer).toString();
		buffer.limit(lim);
		return message;

	}
	/**
	 * Deserialiser paires recuperer le contenu du byte buffer et le deserialiser et les 
	 * 
	 * @param bytebuffer
	 * @param nbpair
	 */

	public synchronized static HashMap<Integer, String> desarialiserPaire(ByteBuffer bytebuffer, int nbpair) {
		int port;
		String adresse;
		 HashMap<Integer, String>  map = new HashMap<>();
		for (int i = 0; i < nbpair; i++) {
			port = bytebuffer.getInt();
			adresse = desarialisationchaine(bytebuffer);
			map.put(port, adresse);
			
			
		}
		return map;

	}
	/**
	 * 
	 * @param bytebuffer
	 * @param nbfichier recuperer dans le byte buffer 
	 * 
	 */

	public synchronized static HashMap<String, Long>  desarialiserficher(ByteBuffer bytebuffer, int nbfichier) {
		long taille;
		String nomfichier;
		HashMap<String,Long> map = new HashMap<>();
		for (int i = 0; i < nbfichier; i++) {
			nomfichier = desarialisationchaine(bytebuffer);
			taille = bytebuffer.getLong();
			map.put(nomfichier, taille);
			

		}
		return map;

	}

}
