
	import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;


	public class Slave {

		public static void main(String[] args) throws InterruptedException {
			
			if(args[0].equals("map")){
					
				Date ajdh = new Date();		
				DateFormat mediumDateFormat = DateFormat.getDateTimeInstance( DateFormat.MEDIUM, DateFormat.MEDIUM);

				// Question 26 : lire et afficher un fichier Sx
				Path fileinput = Paths.get("/cal/homes/epasquet/workspace/S"+args[1]+".txt");
				try {
					List<String> lines;
					lines = Files.readAllLines(fileinput, Charset.forName("UTF-8"));
					
					// Crée un dictionnaire non trié (unsorted map), bref une ArrayList du coup
					ArrayList<String> UM = new ArrayList<String>();
					ArrayList<String> uniqWords = new ArrayList<>();
					String[] words = lines.get(0).split(" ");
					  for (String word : words) {
					    if (word.equals("")) {
					      continue;
					    }
					    UM.add(word);
					    if (!uniqWords.contains(word)) {
							System.out.println(word);
					        uniqWords.add(word);
					      }
					  }
					  
					  
					// Crée un fichier UMx pour stocker l'UM
					//String UMname = "UM"+args[1]+".txt";
					Path out = Paths.get("UM"+args[1]+".txt");
					try /*(PrintWriter out = new PrintWriter(UMname);)*/{
						Files.write(out, UM, Charset.forName("UTF-8"));
						/*
						for (String word : UM){
							out.println(word);
						}
						*/
						//out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				/*
				System.out.println("Fin slave");
				*/
				
				
			} else if(args[0].equals("reduce")){
				
				// System.out.println("reduce mode on");
				// arg[0] est le mode, arg[1] est la clé
				// arg[2] est le nom du fichier sorted map de sortie,
				// arg[3] --> fin sont les fichiers d'entrée
				
				// Création du dictionnaire ReducedMap
				int count = 0;
				// ArrayList<String> SM = new ArrayList<String>();
				// Lire fichiers UMx et remplissage de l'objet RM
				for(int i = 3; i < args.length; i++){
					Path fileinput = Paths.get("/cal/homes/epasquet/workspace/" + args[i]);
					try {
						List<String> lines;
						lines = Files.readAllLines(fileinput, Charset.forName("UTF-8"));
						// Crée un dictionnaire SortedMap recueillant les clés des UMx et incrémentant leur nombre
						for (String word : lines) {
							if(word.equals(args[1])){
								count += 1;
								//SM.add(word);
							}
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				// Affiche le RM dans la sortie standard pour récupération par MASTER
				System.out.println(args[1]+" "+count);

				
			} else {
				System.out.println("Slave has been called with erroneous mode argument");
			}
		}
	}
