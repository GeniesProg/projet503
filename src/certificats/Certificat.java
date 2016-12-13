package certificats;

import java.io.Serializable;
import java.util.Date;

import org.json.JSONObject;


public class Certificat implements Serializable{
	public String nomAutorite;
	public String ip;
	public int port;
	public String nomAppli;
	public String clePublicAppli;
	public String dateValidite;
	
	public Certificat(){
		this.nomAutorite="";
		this.ip="0.0.0.0";
		this.port=0;
		this.nomAppli="";
		this.clePublicAppli="";
		this.dateValidite="";
	}
	
	public Certificat(String nomAutorite, String ip, int port, String nomAppli, String clePublicAppli, String dateValidite){
		this.nomAutorite=nomAutorite;
		this.ip=ip;
		this.port=port;
		this.nomAppli=nomAppli;
		this.clePublicAppli=clePublicAppli;
		this.dateValidite=dateValidite;
	}
	
	public JSONObject tojson() {
		JSONObject j = new JSONObject();
		j.put("nomAutorite", this.nomAutorite);
		j.put("ip", this.ip);
		j.put("port", this.port);
		j.put("nomAppli", this.nomAppli);
		j.put("clePublicAppli", this.clePublicAppli);
		j.put("dateValidite", this.dateValidite);
		return j;
	}
}
