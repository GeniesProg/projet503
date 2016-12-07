package rmi;
import java.util.ArrayList;

public class Question {
	private int numero;
	private String intitule;
	private ArrayList<Reponse> reponses;
	
	public Question(int numero, String intitule) {
		this.numero = numero;
		this.intitule = intitule;
		this.reponses = null;
	}
	
	public void setReponses(ArrayList<Reponse> reponses) {
		this.reponses = new ArrayList<>(reponses);
	}
	
	@Override 
	public String toString() {
		String res = this.numero + ":" + this.intitule;
		for (int i = 0 ; i < this.reponses.size() ; i++) {
			res+= "<li>"+this.reponses.get(i)+"</li>";
		}
		return res;
	}
}
