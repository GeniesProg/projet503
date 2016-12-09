package rmi;
public class Reponse {
	private String num;
	private String texte;
	private int numQuestion;
	private int numSondage;
	public Reponse(String num, String texte, int numQuestion, int numSondage) {
		this.num = num;
		this.texte = texte;
		this.numQuestion = numQuestion;
		this.numSondage = numSondage;
	}
	
	@Override
	public String toString() {
		return "<input type=\"radio\" value=\"" + this.num + "\" name=\""+ this.numSondage+ "-group_" + this.numQuestion + "\">" + this.num + " : " + this.texte;
	}
}
