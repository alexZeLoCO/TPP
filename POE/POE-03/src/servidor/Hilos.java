package servidor;

import java.io.IOException;

import lib.ChannelException;
import lib.CommServer;
import lib.ProtocolMessages;

public class Hilos implements Runnable {

	private int idClient;
	private CommServer com;

	public Hilos (int idClient, CommServer com) {
		this.idClient = idClient;
		this.com = com;
	}

	public void run () {
		Servicio objServicio = null;
		ProtocolMessages request;
		ProtocolMessages response;

		try {
			objServicio = new Servicio(idClient);
			while (!com.closed(idClient)) {
				try {
					request = com.waitEvent(idClient);
					response = com.processEvent(idClient, objServicio, request);
					if (response != null) {
						com.sendReply(idClient, response);
					}
				} catch (ClassNotFoundException e) {
					System.err.printf("Error en la petición del cliente %d: %s\n", idClient, e.getMessage());
				}
			}
		} catch (IOException | ChannelException e) {
			System.err.printf("Error: %s\n", e.getMessage());
		} finally {
			if (objServicio != null) {
				objServicio.close();
			}
		}
	}

}
