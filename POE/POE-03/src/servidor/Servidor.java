package servidor;

import java.io.IOException;

import lib.ChannelException;
import lib.CommServer;
import optional.Trace;

public class Servidor {

	private static void registrarOperaciones (CommServer com) {
		com.addFunction("nBarcos", 
				(o, x) -> ((Servicio)o).numBarcosEnOceano());
		com.addAction("colocarBarco", 
				(o, x) -> ((Servicio)o).colocarBarco((String) (x[0])));
		com.addFunction("barcosPorColocar",
				(o, x) -> ((Servicio)o).barcosPorColocar()); 
		com.addFunction("tableroBarcos", 
				(o, x) -> ((Servicio)o).tableroBarcos());
		com.addAction("iniciarJuego", 
				(o, x) -> ((Servicio)o).iniciarJuego());
		com.addAction("estado", (o, x) -> System.out.println(((Servicio)o).datos()));
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
