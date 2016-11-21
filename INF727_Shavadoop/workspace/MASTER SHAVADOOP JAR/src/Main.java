import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main {	

	public static void main(String[] args) {

		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_  */ 
		
		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-         PARTIE 1 : DEMARRAGE        -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_	*/
		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_  */ 
		
		//////////             Création du fichier des machines en état de fonctionnement            /////////////
		
		
		// ******** TIMESTAMP ********
		long startTime = System.currentTimeMillis();
		
		// DECLARATION VARIABLE : liste de machines sur lesquelles tester la disponibilité
		// DECLARATION VARIABLE : liste des résultats de tests de connexion
		// DECLARATION VARIABLE : liste des machines disponibles liste_machines_ok
		List<String> machines;
		ArrayList<TestConnectionSSH> listeTests = new ArrayList<TestConnectionSSH>();
		ArrayList<String> liste_machines_ok = new ArrayList<String>();

		//Lance toutes les connexions ssh en même temps avec un timeout de 7 secondes
		Path filein = Paths.get("liste_machines.txt");
		try {
			machines = Files.readAllLines(filein, Charset.forName("UTF-8"));
			for (String machine : machines) {
				/*
				 * on teste la connection SSH pendant 7 secondes maximum
				 */
				TestConnectionSSH test = new TestConnectionSSH(machine, 7);
				test.start();
				listeTests.add(test);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Attend la fin de tentatives de connexion ssh ci-dessus pour récupérer les noms des machines
		for (TestConnectionSSH test : listeTests) {
			try {
				test.join();// on attend la fin du test
				if (test.isConnectionOK()) {
					liste_machines_ok.add(test.getMachine());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// Ecrit les noms des machines en état de fonctionnement dans un fichier
		// Et compte leur nombre
		// DECLARATION VARIABLE : nombre de machines disponibles
		int nb_machines = liste_machines_ok.size();
		Path file = Paths.get("liste_machines_OK.txt");
		try {
			Files.write(file, liste_machines_ok, Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// ******** TIMESTAMP ********
		long endTime   = System.currentTimeMillis();
		long totalTime1 = endTime - startTime;

		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_  */ 
		
		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-         PARTIE 2 : SPLITTING        -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_	*/
		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_  */ 
		
		///////// Lire le fichier INPUT et créer autant de fichier Sx qu'on a de machines disponibles ////////////
		
		
		// ******** TIMESTAMP ********
		startTime = System.currentTimeMillis();
		
		// Si il y a plus de machines que de lignes, on rentre dans la première condition du if ci-dessous.
		// Si il y a plus de lignes que de machines, on rentre dans la deuxième condition du if : chaque
		// fichier Sx contient donc le nombre de lignes du fichier initial divisé par le nombre de machines.
		Path fileinput = Paths.get(args[0]);
		// DECLARATION VARIABLE : Compteur de sections = compteur de fichiers Sx = compteur de fichiers UMx 
		int Sx_counter = 0;

		try {
			List<String> lines;
			lines = Files.readAllLines(fileinput, Charset.forName("UTF-8"));
			int nb_lignes = lines.size();
			//////  IF plus de machines que de lignes   //////
			if (nb_machines >= nb_lignes) {
				for(int i = 0; i<lines.size(); i++ ){
					String Sxname = "S"+i+".txt";
						try (PrintWriter out = new PrintWriter(Sxname);){
							out.println(lines.get(i));
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						Sx_counter ++;
					}
					
			} else {
			//////  IF plus de lignes que de machines   //////
				int nbLignesParFichier = (int) nb_lignes/nb_machines + 1;
				int ligneRepriseEcriture = 0;
				for(int f = 0; f < nb_machines; f++ ){
					String AEcrire = ""; 
					for (int i = ligneRepriseEcriture; i < Math.min(ligneRepriseEcriture + nbLignesParFichier, nb_lignes); i++){
						AEcrire += " " + lines.get(i);
					}
					ligneRepriseEcriture += nbLignesParFichier;

					String Sx_name = "S"+f+".txt";
						//try {
							PrintWriter p = new PrintWriter(Sx_name);
							p.println(AEcrire);
							p.close();
					Sx_counter ++;
					}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// ******** TIMESTAMP ********
		endTime   = System.currentTimeMillis();
		long totalTime2 = endTime - startTime;
		
		
		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_  */ 
		
		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-      ON LANCE LE MAP      -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_	*/
		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_  */ 
		
		/////////              Lancer en parallèle le SLAVE sur toutes ces machines                   ////////////
		///////// Le dictionnaire UMx_Machines mappe le nom de fichier UMx à la machine qui l'a créé. ////////////

		
		// ******** TIMESTAMP ********
		startTime = System.currentTimeMillis();
		
		// lancer en parallèle le SLAVE sur toutes ces machines
		// Le dictionnaire UMx_Machines mappe le nom de fichier UMx à la machine qui l'a créé.
		Map<String, LaunchSlaveShavadoop> slaves = new HashMap<String, LaunchSlaveShavadoop>();
		Map<String, String> UMx_Machines = new HashMap<String, String>();
		HashMap<String, ArrayList<String>> attributions = new HashMap<String, ArrayList<String>>(); // UMx + clés

		try {
			machines = Files.readAllLines(file, Charset.forName("UTF-8"));
			System.out.println(machines);
			for (int i = 0; i < Sx_counter; i++) {
				// on lance les threads de slave
				LaunchSlaveShavadoop slave = new LaunchSlaveShavadoop(machines.get(i),"cd workspace/;java -jar SLAVESHAVADOOP.jar map " + i, 20);
				slave.start();
				slaves.put(machines.get(i), slave);
				String um_name = "UM"+i+".txt";
				UMx_Machines.put(machines.get(i), um_name);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (Map.Entry<String, LaunchSlaveShavadoop> entry : slaves.entrySet()) {
			LaunchSlaveShavadoop slave = entry.getValue();
			try {
				slave.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ArrayList<String> liste = slave.getSortie();
			String umx = UMx_Machines.get(slave.getMachine());
			System.out.println(umx);
			attributions.put(umx, liste);
		}
		
		//System.out.println("slaves");
		//System.out.println(slaves);
		System.out.println("UMx_machines");
		System.out.println(UMx_Machines);
		System.out.println("\nATTRIBUTIONS : \n");
		System.out.println(attributions);
		
		// ******** TIMESTAMP ********
		endTime   = System.currentTimeMillis();
		long totalTime3 = endTime - startTime;
		
		
		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_  */ 
		
		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_      ON LANCE LE SHUFFLE      _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_	*/
		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_  */ 

		
		// ******** TIMESTAMP ********
		startTime = System.currentTimeMillis();
		
		///////// création du dictionnaire "Clé - liste des fichier UMx" à partir du dictionnaire d'attributions
		HashMap <String, ArrayList<String>> cle_UMx = new HashMap <String, ArrayList<String>>() ;
		for (String file_UM : attributions.keySet()) {
			for (String mot : attributions.get(file_UM)) {
				if (! cle_UMx.containsKey(mot)){
					// on met en attribut un tableau avec juste la machine
					ArrayList<String> liste_file = new ArrayList<String>(); 
					liste_file.add(file_UM); 
					cle_UMx.put(mot,liste_file);
				} else {
					// on le rajoute au tableau 
					ArrayList<String> liste_file = cle_UMx.get(mot);
					liste_file.add(file_UM);
					cle_UMx.put(mot,liste_file);
				}
			}	
		}
		
		System.out.println(cle_UMx);
		System.out.println("Le map est fini");
		
		
		// ******** TIMESTAMP ********
		endTime   = System.currentTimeMillis();
		long totalTime4 = endTime - startTime;
		
		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_  */ 
		
		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-      ON LANCE LE REDUCE      -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_	*/
		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_  */ 
		
		
		// ******** TIMESTAMP ********
		startTime = System.currentTimeMillis();
		
		// Lire la liste des machines en état de fonctionnement et
		// lancer en parallèle le SLAVE sur toutes ces machines
		// Le dictionnaire RMx_Machines mappe le nom de fichier RMx à la machine qui l'a créé (le possède)
		int s = 0;
		//slaves.clear();
		ArrayList<LaunchSlaveShavadoop> slaves2 = new ArrayList<LaunchSlaveShavadoop>();
		attributions.clear();
		ArrayList<String> AllRMaps = new ArrayList<String>(); 
		try {
			machines = Files.readAllLines(file, Charset.forName("UTF-8"));
			for(String key : cle_UMx.keySet()){
				// on lance les threads de slave
				String UMList = "";
				for(String um : cle_UMx.get(key)){
					UMList += " " + um;
				}
				LaunchSlaveShavadoop slave = new LaunchSlaveShavadoop(machines.get(s%machines.size()),"cd workspace/;java -jar SLAVESHAVADOOP.jar reduce "+key+" RM"+s+UMList, 20);
				slave.start();
				//slaves.put(machines.get(s%machines.size()), slave);
				slaves2.add(slave);
				s++;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Attente de la fin des threads de Reduce
		for (LaunchSlaveShavadoop slave : slaves2){
		//for (Map.Entry<String, LaunchSlaveShavadoop> entry : slaves.entrySet()) {
		//	LaunchSlaveShavadoop slave = entry.getValue();
			try {
				slave.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ArrayList<String> cles = slave.getSortie();
			for (String st : cles){
				AllRMaps.add(st);
			}
		}
		
		
		// ******** TIMESTAMP ********
		endTime   = System.currentTimeMillis();
		long totalTime5 = endTime - startTime;

		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_  */ 
		
		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-      ON LANCE L'ASSEMBLING      -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_	*/
		/* _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_  */ 
		
		// ******** TIMESTAMP ********
		startTime = System.currentTimeMillis();


		Path out = Paths.get("output_Shavadoop");
		try {
			Files.write(out, AllRMaps, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(AllRMaps);
		System.out.println("Le reduce est fini");
		
		// ******** TIMESTAMP ********
		endTime   = System.currentTimeMillis();
		long totalTime6 = endTime - startTime;

		
		/* _-_-_-_-_-_-_-_-_-_-_-_      ON AFFICHE LES TEMPS DE TRAITEMENT      _-_-_-_-_-_-_-_-_-_-_-_-_-_-_	*/

		System.out.print("Durée du démarrage : "+totalTime1/1000.0+"s\n");
		System.out.print("Durée du split : "+totalTime2/1000.0+"s\n");
		System.out.print("Durée du map : "+totalTime3/1000.0+"s\n");
		System.out.print("Durée du shuffle : "+totalTime4/1000.0+"s\n");
		System.out.print("Durée du reduce : "+totalTime5/1000.0+"s\n");
		System.out.print("Durée de l'assembling : "+totalTime6/1000.0+"s\n");
	}

}
