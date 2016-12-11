package rmi;

public class Compta {

	private int sondage;
	private int[][] res;
	
	public Compta(int s, int[][] r) {
		this.sondage = s;
		this.res = r;
	}

	public int getSondage() {
		return sondage;
	}

	public void setSondage(int sondage) {
		this.sondage = sondage;
	}

	public int[][] getRes() {
		return res;
	}

	public void setRes(int[][] res) {
		this.res = res;
	}
	
	
}
