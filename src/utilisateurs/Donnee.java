package utilisateurs;

import java.io.Serializable;

public class Donnee implements Serializable {
	private String login;
	private int sondage;
	private String[] reponses;
	
	public Donnee(String login, int sondage, String[] reponses) {
		this.login = login;
		this.sondage = sondage;
		this.reponses = reponses;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public int getSondage() {
		return sondage;
	}

	public void setSondage(int sondage) {
		this.sondage = sondage;
	}

	public String[] getReponses() {
		return reponses;
	}

	public void setReponses(String[] reponses) {
		this.reponses = reponses;
	}
	
	
}
