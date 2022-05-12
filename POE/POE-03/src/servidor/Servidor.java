package servidor;

import java.io.IOException;

import lib.ChannelException;
import lib.CommServer;
import optional.Trace;

public class Servidor {

	private static void registrarOperaciones (CommServer com) {
		com.addFunction("nBarcos", 
				(o, x) -> ((Servicio)o).numBarcosEnOceano());
		com.addFunction("barcosPorColocar",
				(o, x) -> ((Servicio)o).barcosPorColocar()); 
		com.addFunction("tableroBarcos", 
				(o, x) -> ((Servicio)o).tableroBarcos());
		com.addAction("colocarBarco", 
				(o, x) -> ((Servicio)o).colocarBarco((String) x[0]));
		com.addFunction("iniciarJuego",
				(o, x) -> ((Servicio)o).iniciarJuego());
		com.addFunction("turno", 
				(o, x) -> ((Servicio)o).turno());
		com.addFunction("tirar",
				(o, x) -> ((Servicio)o).coordenadasTiro((String) x[0]));
		com.addFunction("tableroTiros",
				(o, x) -> ((Servicio)o).tableroTiros());
	}
	
	public static void main (String[] args) {
		CommServer com;
		int idClient;
		
		try {
			com = new CommServer();
			Trace.activateTrace(com);
			com.activateMessageLog();
			registrarOperaciones(com);
			while (true) {
				idClient = com.waitForClient();
				new Thread (new Hilos(idClient, com)).start();
			}
		} catch (IOException | ChannelException e) {
			System.err.printf("Error: %s\n", e.getMessage());
			e.printStackTrace();
		}
	}
}
