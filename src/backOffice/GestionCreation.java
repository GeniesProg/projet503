package backOffice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import rmi.IGestionnaireSondages;
import rmi.Question;
import rmi.Reponse;
import rmi.Sondage;

public class GestionCreation implements HttpHandler {

	@Override
	public void handle(HttpExchange t) throws IOException {
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				try {
					GestionCreation.main(t);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}					
			}
		});
		t1.start();
	}
	
	public static void main(HttpExchange t) throws RemoteException {
		String reponse = 
				"<html>"
				  +"<head>"
				   + "<title>Page admin</title>"
				    +"<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>"
				  +"</head>"
				  +"<body style=\"font-family: Georgia, Times, serif;padding:20px;width:400px;border:1px solid #172183;\">"
					+"<p style=\"text-align:center;padding:5px;color:white;background:#172183;\">Vos changements ont bien été pris en compte!</p>"
					+ "<form action=\"http://localhost:8080/admin.html\">"
					+ "<button style=\"border: none;color: #ffffff;display: block;margin: auto;background: #172183;padding: 5px 20px;cursor:pointer;\">Retour</button>"
					+ "</form>";
		
				URI requestedUri = t.getRequestURI();
			    String query = requestedUri.getRawQuery();
			        
			    BufferedReader br = null;
			    try {
			    	br = new BufferedReader(new InputStreamReader(t.getRequestBody(),"utf-8"));
			    } catch(UnsupportedEncodingException e) {
			    	System.err.println("Erreur lors de la récupération du flux " + e);
			    	System.exit(-1);
			    }

			    try {
			    	query = br.readLine();
			    } catch(IOException e) {
			    	System.err.println("Erreur lors de la lecture d'une ligne " + e);
			    	System.exit(-1);
			    }
	   
			   /*String [] p = query.split("&");
			   for (int i = 0 ; i < p.length ; i++) {
				   reponse += p[i] + "<br>";
			   }*/
			    IGestionnaireSondages array = null;
				try {
				    array = (IGestionnaireSondages)Naming.lookup("rmi://localhost/sondages");
				} catch(NotBoundException e) {
				    System.err.println("Pas possible d'accéder à l'objet distant : " + e);
				    System.exit(-1);
				} catch(MalformedURLException e) {
				    System.err.println("URL mal forme : " + e);
				    System.exit(-1);
				} catch(RemoteException e) {
				    System.err.println("Pas possible d'accéder à l'objet distant : " + e);
				    System.exit(-1);
				}	
				System.out.println(array);
					try {
						array.chargerSondages();
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				int length = 0;
				try {
					length = array.getSondages().size();
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int nsondage = length + 1;
		
				String[] op = query.split("&");
				String titre = op[0].split("=")[1];
			   //System.out.println(titre);
			   
			   
				String[] params = Arrays.copyOfRange(op, 1, op.length);
			   
			   /*for (int a = 0 ; a < params.length ; a++) {
				   System.out.println(a + " " +params[a]);
			   }*/
	   
				ArrayList<Question> questions = new ArrayList<Question>();
			   //System.out.println("taille " + params.length );
			   
				for (int i = 0 ; i < params.length ; i++) {
				   //System.out.println(i);
				   if (i%5 == 0) {
					   //System.out.println("oui au tour " + i);
					   String[] detQ = params[i].split("=");
					   int nq = Integer.parseInt(detQ[0].substring(1, detQ[0].length()));
					   String libq = detQ[1];
					   Question q = new Question(nq, libq, nsondage);
					   q.setReponses(new ArrayList<Reponse>());
					   questions.add(q);
				   } else {
					   //System.out.println("non au tour " + i);
					   String[] detR = params[i].split("=");
					   //System.out.println("pour " + i + " " + detR.length);
					   
					   if (detR.length == 2) {
						   String lib = detR[1];
						   int q = Integer.parseInt(detR[0].split("_")[0]);
						   //System.out.println("question " + q);
						   int num = Integer.parseInt(detR[0].split("_")[1]);
						   //System.out.println("num " + num);
						   String l = "";
						   if (num == 1) {
							   l = "A";
						   } else if (num == 2) {
							   l = "B";
						   } else if (num == 3) {
							   l = "C";
						   } else if (num == 4) {
							   l = "D";
						   }
						   //System.out.println("lettre " + l);
						   //System.out.println("pour " + l + " " +lib.equals(""));   				  
						   Reponse r = new Reponse(l, lib, q, nsondage);
						   questions.get(q-1).getReponses().add(r);
					   }
					   
				   }
			   }
				
				System.out.println(titre);
				System.out.println(array);
			   Sondage s = null;
			try {
				s = new Sondage(nsondage, titre, questions, 1);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println(s.affichage());
			   try {
				array.ajouterSondage(nsondage, titre, questions, 1);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			   //System.out.println(s.affichage());
			   //System.out.println(nsondage);
	   
			   	reponse += "</body></html>";
		
				try {
		            Headers h = t.getResponseHeaders();
		            h.set("Content-Type", "text/html; charset=utf-8");
		            t.sendResponseHeaders(200, 0);
		        } catch(IOException e) {
		            System.err.println("Erreur lors de l'envoi de l'en-tête : " + e);
		            System.exit(-1);
		        }

		

		        // Envoi du corps (données HTML)
		        try {
		            OutputStream os = t.getResponseBody();
		            os.write(reponse.getBytes());
		            os.close();
		        } catch(IOException e) {
		            System.err.println("Erreur lors de l'envoi du corps : " + e);
		        }
	}
	
}