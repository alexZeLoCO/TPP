package servidor;

import java.io.IOException;

import lib.ChannelException;
import lib.CommServer;
import optional.Trace;

class Servidor {
	private static void registrarOperaciones(CommServer com) {
		com.addFunction("tableroBarcos",
				(o, x) -> ((Servicio) o).tableroBarcos());
		com.addFunction("tableroTiros",
				(o, x) -> ((Servicio) o).tableroTiros());
		com.addFunction("barcosPorColocar",
				(o, x) -> ((Servicio) o).barcosPorColocar());
		com.addAction("colocarBarco",
				(o, x) -> ((Servicio) o).colocarBarco((String) x[0]));
		com.addFunction("iniciarJuego",
				(o, x) -> ((Servicio) o).iniciarJuego());
		com.addFunction("turno",
				(o, x) -> ((Servicio) o).turno());
		com.addFunction("coordenadasTiro",
				(o, x) -> ((Servicio) o).coordenadasTiro((String) x[0]));
		com.addFunction("sonar",
				(o, x) -> ((Servicio) o).sonar((String) x[0]));
		com.addFunction("numBarcosEnOceano",
				(o, x) -> ((Servicio) o).numBarcosEnOceano());
	}

	public static void main(String[] args) {
		CommServer com; // canal de comunicación del servidor
		int idCliente; // identificador del cliente

		try {
			// crear el canal de comunicación del servidor
			com = new CommServer();

			// activar la traza en el servidor (opcional)
			Trace.activateTrace(com);

			// activar el registro de mensajes del servidor (opcional)
			com.activateMessageLog();

			// registrar operaciones del servicio
			registrarOperaciones(com);

			// ofrecer el servicio (queda a la escucha)
			while (true) {
				// espera por un cliente
				idCliente = com.waitForClient();

				// conversación con el cliente en un hilo
				Trace.printf("-- Creando hilo para el cliente %d.\n",
						idCliente);
				new Thread(new Hilos(idCliente, com)).start();
				Trace.printf("-- Creado hilo para el cliente %d.\n",
						idCliente);
			}
		} catch (IOException | ChannelException e) {
			System.err.printf("Error: %s\n", e.getMessage());
			e.printStackTrace();
		}
	}

}
