package rmi;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class ArraySondage extends UnicastRemoteObject implements IArraySondage  {

	private ArrayList<ISondage> sondages;
	private ArrayList<Compta> compta;
	private String fichierCompta;
	
    public ArraySondage(ArrayList<ISondage> sondages) throws RemoteException {
    	this.sondages = new ArrayList<>(sondages);
    	this.compta = new ArrayList<>();
    	this.fichierCompta = "comptasond.json";
    }

	public ArrayList<ISondage> getSondages() throws RemoteException {		
		return this.sondages;
	}

	public void chargerCompta() {
		FileInputStream fs = null;
		try {
		    fs = new FileInputStream(this.fichierCompta);
		} catch(FileNotFoundException e) {
		    System.err.println("Fichier '" + this.fichierCompta + "' introuvable");
		    System.exit(-1);
		}
	 
		// Récupération de la chaîne JSON depuis le fichier
		String json = new String();
		Scanner scanner = new Scanner(fs);
		while(scanner.hasNext())
		    json += scanner.nextLine();
		scanner.close();
		
		// Création d'un objet JSON
		JSONObject objet = new JSONObject(json);		
		JSONArray tableau = objet.getJSONArray("compte");
		System.out.println(tableau);
		ArrayList<Compta> nouveau = new ArrayList<>();
		for (int i = 0 ; i < tableau.length();i++) {			
			JSONObject element =tableau.getJSONObject(i);
			JSONArray questions = element.getJSONArray("questions");
			int [][] r = new int[questions.length()][];
			Compta c = new Compta(element.getInt("sondage"), null);
			for (int j = 0 ; j < questions.length() ; j++) {
				JSONObject q =questions.getJSONObject(j);
				JSONArray reponses = q.getJSONArray("reps");
				r[j] = new int[reponses.length()];
				for (int k = 0 ; k < reponses.length() ; k++) {
					JSONObject rep = reponses.getJSONObject(k);
					String lettre = rep.getString("lettre");
					if (lettre.equals("A")) {
						r[j][0] = rep.getInt("nb");
					} else if (lettre.equals("B")) {
						r[j][1] = rep.getInt("nb");
					} else if (lettre.equals("C")) {
						r[j][2] = rep.getInt("nb");
					} else if (lettre.equals("D")) {
						r[j][3] = rep.getInt("nb");
					}
				}
			}
			c.setRes(r);
			nouveau.add(c);
		}
		this.compta = nouveau;
		
		/*for (int t = 0 ; t < this.compta.size() ; t++) {
			Compta c = this.compta.get(t);
			System.out.println(c.getSondage());
			for (int tt = 0 ; tt < c.getRes().length ; tt++) {
				for (int ttt = 0 ; ttt < c.getRes()[tt].length ; ttt++) {
					System.out.print(c.getRes()[tt][ttt] + " ");
				}
				System.out.println(" ");
			}
			System.out.println(c.getSondage());
		}*/
	}

	public void sauvegarderCompta() {
		JSONObject main = new JSONObject();
		JSONArray a = new JSONArray();
		for (Compta c : this.compta) {
			JSONObject comptajson = new JSONObject();
			comptajson.put("sondage", c.getSondage());
			JSONArray questions = new JSONArray();
			for (int i = 0 ; i < c.getRes().length ; i++) {				
				JSONObject q = new JSONObject();
				q.put("numQ", i+1);
				JSONArray reponses = new JSONArray();
				for (int j = 0 ; j < c.getRes()[i].length ; j++) {
					JSONObject r = new JSONObject();
					if (j == 0) {
						r.put("lettre", "A");
						r.put("nb", c.getRes()[i][0]);
					} else if (j == 1) {
						r.put("lettre", "B");
						r.put("nb", c.getRes()[i][1]);
					} else if (j == 2) {
						r.put("lettre", "C");
						r.put("nb", c.getRes()[i][2]);
					} else if (j == 3) {
						r.put("lettre", "D");
						r.put("nb", c.getRes()[i][3]);
					}
					reponses.put(r);
				}
				q.put("reps", reponses);
				questions.put(q);
			}
			comptajson.put("questions", questions);
			a.put(comptajson);
		}
		main.put("compte", a);	
		FileWriter fs = null ; 
		
		try {
		    fs = new FileWriter(this.fichierCompta);
		} catch(IOException e) {
		    System.err.println("Erreur lors de l'ouverture du fichier " + this.fichierCompta);
		    System.err.println(e);
		    System.exit(-1);
		}
		try {
		    main.write(fs);
		    fs.flush();
		} catch(IOException e) {
		    System.err.println("Erreur lors de l'écriture dans le fichier : " + e);
		    System.exit(-1);
		}
	}

	public void updateCompta(String json) throws RemoteException {
		//System.out.println(json);
		this.chargerCompta();
		
		JSONObject objet = new JSONObject(json);
		int numSondage = Integer.parseInt(objet.getString("sondage"));
		for (int i = 0 ; i < this.compta.size() ; i++) {
			if (this.compta.get(i).getSondage() == numSondage) {
				JSONArray a = objet.getJSONArray("liste");
				for (int j = 0 ; j < a.length() ; j++) {
					JSONObject element = a.getJSONObject(j);										
					if (element.getString(String.valueOf(j+1)).equals("A")){
						this.compta.get(i).getRes()[j][0]++;
					} else if (element.getString(String.valueOf(j+1)).equals("B")) {
						this.compta.get(i).getRes()[j][1]++;
					} else if (element.getString(String.valueOf(j+1)).equals("C")) {
						this.compta.get(i).getRes()[j][2]++;
					} else if (element.getString(String.valueOf(j+1)).equals("D")) {
						this.compta.get(i).getRes()[j][3]++;
					}
				}
			}
		}
		this.sauvegarderCompta();
	}

	public String affichageTotal(int sondage) throws RemoteException {
		String s = "<p>Les résultats totaux: </p>";
		for (int recherche = 0 ; recherche < this.compta.size() ; recherche++) {
			if (this.compta.get(recherche).getSondage() == sondage) {
				Compta c = this.compta.get(recherche);
				for (int i = 0 ; i < c.getRes().length ; i++) {
					s += "Question " + (i+1) + ": <br>"; 
					for (int j = 0 ; j < c.getRes()[i].length; j++) {
						s+= "     ";
						if (j == 0) s+= "A";
						else if (j == 1) s+= "B";
						else if (j == 2) s+= "C";
						else if (j == 3) s+= "D";
						s+= ": " + c.getRes()[i][j] + "<br>";
					}
				}
			}
		}

		return s;
	}
}
