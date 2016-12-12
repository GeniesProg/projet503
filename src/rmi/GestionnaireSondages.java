package rmi;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class GestionnaireSondages extends UnicastRemoteObject implements IGestionnaireSondages  {

	private ArrayList<ISondage> sondages;
	private ArrayList<Compta> compta;
	private ArrayList<Sondage> objSondages; 
	private String fichierCompta;
	private String fichierSondages;
	
    public GestionnaireSondages(ArrayList<ISondage> sondages) throws RemoteException {
    	this.sondages = new ArrayList<>(sondages);
    	this.compta = new ArrayList<>();
    	this.objSondages = new ArrayList<>();
    	this.fichierCompta = "comptasond.json";
    	this.fichierSondages = "sondages.json";
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
		this.chargerCompta();
		String s =  "<div><p style=\"text-align:center;padding:5px;color:white;background:#172183;\">Les résultats totaux du sondage" + " " + sondage +": </p>";
		for (int recherche = 0 ; recherche < this.compta.size() ; recherche++) {
			if (this.compta.get(recherche).getSondage() == sondage) {
				Compta c = this.compta.get(recherche);
				for (int i = 0 ; i < c.getRes().length ; i++) {
					s += "Question " + (i+1) + ": <br>"; 
					for (int j = 0 ; j < c.getRes()[i].length; j++) {
						s+= "<li>";
						if (j == 0) s+= "A";
						else if (j == 1) s+= "B";
						else if (j == 2) s+= "C";
						else if (j == 3) s+= "D";
						s+= ": " + c.getRes()[i][j] + "</li>";
					}
				}
			}
		}
		s+="</div>";
		return s;
	}

	@Override
	public void updateActivation(int sondage, int activation) throws RemoteException {
		this.chargerSondages();
		for (int i = 0 ; i < this.objSondages.size() ; i++ ) {
			Sondage actuel = this.objSondages.get(i);
			if (actuel.getId() == sondage) {
				actuel.setActive(activation);
			}
		}
		this.sauvegarderSondages();		
	}

	@Override
	public void chargerSondages() throws RemoteException {
		System.out.println("je suis passé par charger");
		FileInputStream fs = null;
    	try {
    	    fs = new FileInputStream(this.fichierSondages);
    	} catch(FileNotFoundException e) {
    	    System.err.println("Fichier '" + this.fichierSondages + "' introuvable");
    	    System.exit(-1);
    	}
    	
    	String json = new String();
    	Scanner scanner = new Scanner(fs);
    	while(scanner.hasNext())
    	    json += scanner.nextLine();
    	scanner.close();
    	
    	JSONObject objet = new JSONObject(json);
    	JSONArray tableau = objet.getJSONArray("sondages");
    	ArrayList<Sondage> sondages = new ArrayList<Sondage>();

    	for(int i = 0; i < tableau.length(); i++) {
    	    JSONObject sondage = tableau.getJSONObject(i);
    	    int numSondage = sondage.getInt("id");
    	    int active = sondage.getInt("active");
        	
    	    JSONArray questions = sondage.getJSONArray("questions");
    	    ArrayList<Question> tabQuestions = new ArrayList<>();
    	    for (int j = 0; j < questions.length() ; j++) {
    	    	JSONObject question = questions.getJSONObject(j);
    	    	Question q = new Question(question.getInt("numero"), question.getString("intitule"), numSondage);
    	    	JSONArray reponses = question.getJSONArray("reponses");
    	    	ArrayList<Reponse> tabReponses = new ArrayList<>();
    	    	for (int k = 0; k < reponses.length(); k++){
    	    		JSONObject reponse = reponses.getJSONObject(k);
    	    		Reponse r = new Reponse(reponse.getString("lettre"), reponse.getString("libelle"), q.getNumero(), numSondage);
    	    		tabReponses.add(r);
    	    	}
    	    	q.setReponses(tabReponses);
    	    	tabQuestions.add(q);
    	    }
    	    
    	    Sondage s = new Sondage(sondage.getInt("id"), sondage.getString("titre"), tabQuestions, active);
    	    
    	    sondages.add(s);
    	      	
    	}
    	
    	this.objSondages = sondages;
    	
	}

	@Override
	public void sauvegarderSondages() throws RemoteException {
		JSONObject main = new JSONObject();
		JSONArray a = new JSONArray();
		
		for (Sondage s : this.objSondages) {
			JSONObject objetSondage = new JSONObject();
			objetSondage.put("id", s.getId());
			objetSondage.put("titre", s.getTitre());
			objetSondage.put("active", s.getActive());
			JSONArray questions = new JSONArray();
			for (Question q : s.getQuesAndRep()) {
				JSONObject question = new JSONObject();
				question.put("numero", q.getNumero());
				question.put("intitule", q.getIntitule());
				JSONArray reponses = new JSONArray();
				for (Reponse r : q.getReponses()) {
					JSONObject reponse = new JSONObject();
					reponse.put("lettre", r.getNum());
					reponse.put("libelle", r.getTexte());
					reponses.put(reponse);
				}
				question.put("reponses", reponses);
				questions.put(question);
			}
			objetSondage.put("questions", questions);
			a.put(objetSondage);
		}
		main.put("sondages", a);
		FileWriter fs = null ; 
		
		try {
		    fs = new FileWriter(this.fichierSondages);
		} catch(IOException e) {
		    System.err.println("Erreur lors de l'ouverture du fichier " + this.fichierSondages);
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

	@Override
	public void ajouterSondage(int nsondage, String titre, ArrayList<Question>questions, int i) throws RemoteException { 		
		System.out.println("CONNARD MANIFESTE TOI PUTAIN");
		Sondage s = new Sondage(nsondage, titre, questions, i);
		try {
			Naming.rebind("sondage"+s.getId(), s);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.objSondages.add(s);	
		this.sondages.add((ISondage)s);
		//this.objSondages.add(s);		
		this.sauvegarderSondages();
		
	}

	@Override
	public Compta createComtpaFromSondage(Sondage s) throws RemoteException {
		int r [][] = new int[s.getQuesAndRep().size()][];
		for (int i = 0 ; i < s.getQuesAndRep().size() ; i++) {
			r[i] = new int[s.getQuesAndRep().get(i).getReponses().size()];
		}
		Compta c = new Compta(s.getId(), r);
		return c;
	}

	@Override
	public void ajouterCompta(Compta c) throws RemoteException {
		System.out.println("hello");
		this.chargerCompta();
		this.compta.add(c);
		this.sauvegarderCompta();
		
	}

	@Override
	public boolean estActive(ISondage s) throws RemoteException {
		this.chargerSondages();
		boolean b = false;
		for (int i = 0 ; i < this.objSondages.size() ; i++) {
			Sondage current = this.objSondages.get(i);
			if (s.getId() == current.getId()) {
				if (current.getActive() == 0) {
					b = false;
				} else {
					b= true;
				}
			}
		}
		return b;
	}
}
