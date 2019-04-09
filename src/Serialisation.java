import java.io.Serializable;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;

public class Serialisation implements Serializable {
	static ByteBuffer buffer = ByteBuffer.allocate(1024);
	static public final Charset c = Charset.forName("UTF-8");
/**
 * Fonction SerialisationChaine : serialiser une chaine de caractére
 * @param chaine : la chaine de caractére qu'on veut serialiser
 * @param buffer : le byteBuffer qui sera envoyé.
 */
	public static void Serialiserchaine(String chaine, ByteBuffer buffer) {
		// buffer.clear();
		ByteBuffer buffer1 = c.encode(chaine);
		buffer.putInt(buffer1.remaining());
		buffer.put(buffer1);

	}
	/**
	 *  Serialisation de la liste des fichiers 
	 * @param fichiers : la liste de fichiers partagés 
	 * @param buffer : idem
	 */

	public static void SerialisationListFichier(HashMap<String, Long> fichiers,ByteBuffer buffer) {
			
			for(String file : fichiers.keySet())
			{
				Serialisation.Serialiserchaine(file, buffer);
				buffer.putLong(fichiers.get(file));
				
			}
	}
	/***
	 *  Serialisation de la liste des paires 
	 * @param Paires : la liste des clients connectés
	 * @param buffer : idem
	 */
	
	public static void SerialisationListClient(HashMap<Integer, String> Paires,ByteBuffer buffer) {
		
		for(Integer Port : Paires.keySet())
		{
			buffer.putInt(Port);
			Serialisation.Serialiserchaine(Paires.get(Port), buffer);
		}
}

}
