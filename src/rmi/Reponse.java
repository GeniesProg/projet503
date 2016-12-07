package rmi;
public class Reponse {
	private String num;
	private String texte;
	
	public Reponse(String num, String texte) {
		this.num = num;
		this.texte = texte;
	}
	
	@Override
	public String toString() {
		return this.num + ":" + this.texte;
	}
}
