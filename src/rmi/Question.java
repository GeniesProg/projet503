package rmi;
import java.util.ArrayList;

public class Question {
	private int numero;
	private String intitule;
	private ArrayList<Reponse> reponses;
	private int numSondage;
	
	public Question(int numero, String intitule, int numSondage) {
		this.numero = numero;
		this.intitule = intitule;
		this.reponses = null;
		this.numSondage = numSondage;
	}
	
	public int getNumSondage() {
		return this.numSondage;
	}
	
	public int nbReponses() {
		return this.reponses.size();
	}
	
	public void setReponses(ArrayList<Reponse> reponses) {
		this.reponses = new ArrayList<>(reponses);
	}
	
	@Override 
	public String toString() {
		String res = this.numero + ":" + this.intitule;
		res += "<fieldset id=\""+this.numSondage + "-group_" + this.numero + "\">";
		for (int i = 0 ; i < this.reponses.size() ; i++) {
			res+= this.reponses.get(i);
		}
		res += "</fieldset>";
		return res;
	}

	public int getNumero() {
		return numero;
	}

	public String getIntitule() {
		return intitule;
	}

	public ArrayList<Reponse> getReponses() {
		return reponses;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public void setIntitule(String intitule) {
		this.intitule = intitule;
	}

	public void setNumSondage(int numSondage) {
		this.numSondage = numSondage;
	}
}
