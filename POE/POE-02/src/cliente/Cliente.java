package cliente;

import java.io.IOException;
import java.net.UnknownHostException;

import lib.ChannelException;
import lib.CommClient;
import lib.Menu;
import lib.ProtocolMessages;
import lib.UnknownOperation;
import comun.AccionNoPermitida;

public class Cliente {

	private static CommClient com;	// canal de comunicación del cliente (singleton)
	private static Menu m;			// interfaz (singleton)
	
	private static void saludame() throws IOException, ChannelException {
		// crear mensaje a enviar
		ProtocolMessages peticion =
				new ProtocolMessages("saluda");
		// enviar mensaje de solicitud al servidor
		com.sendEvent(peticion);
		try {
			// esperar respuesta, excepto si la petición es oneway
			ProtocolMessages respuesta = com.waitReply();
			// procesar respuesta o excepción
			Object resultado = com.processReply(respuesta);
			System.out.println(resultado);
		} catch (ClassNotFoundException e) {
			System.err.printf("Recibido del servidor: %s\n",
					e.getMessage());
		} catch (UnknownOperation e) {
			System.err.printf("Recibido del servidor: %s\n",
					e.getMessage());
		} catch (IOException | ChannelException e) {
			throw e;
		} catch (Exception e) {
			System.err.printf("%s: %s\n", e.getClass().getSimpleName(),
					e.getMessage());
		}
		
	} // saludame
	
	private static void numUsuarios() throws IOException, ChannelException {
		// crear mensaje a enviar
		ProtocolMessages peticion =
				new ProtocolMessages("usuariosConectados");
		// enviar mensaje de solicitud al servidor
		com.sendEvent(peticion);
		try {
			// esperar respuesta, excepto si la petición es oneway
			ProtocolMessages respuesta = com.waitReply();
			// procesar respuesta o excepción
			Object resultado = com.processReply(respuesta);
			System.out.printf("Usuarios conectados: %s\n", resultado);
		} catch (ClassNotFoundException e) {
			System.err.printf("Recibido del servidor: %s\n",
					e.getMessage());
		} catch (UnknownOperation e) {
			System.err.printf("Recibido del servidor: %s\n",
					e.getMessage());
		} catch (IOException | ChannelException e) {
			throw e;
		} catch (Exception e) {
			System.err.printf("%s: %s\n", e.getClass().getSimpleName(),
					e.getMessage());
		}

	} // numUsuarios

	private static void cambiandoSaludo()
			throws Exception {
		// Implementar esta operación: el texto del nuevo saludo deberá
		// solicitarse por teclado y tratarse convenientemente todas las
		// excepciones. Sólo se llevarán al main las excepciones críticas
		// IOException y ChannelException para dar un mensaje de error y
		// parar el cliente.

		
	} // cambiandoSaludo
	
	private static void msgReset()
			throws IOException, ChannelException {
		// crear el mensaje a enviar al servidor
		ProtocolMessages peticion = new ProtocolMessages("reset");
		// enviar el mensaje de solicitud al servidor
		// petición 'oneway', sin respuesta
		com.sendEvent(peticion);	
	} // msgReset

	public static void interfazCliente() {
		// crea el menú m
    	m = new Menu("\nSaludador", "Opción ? ");
    	
    	// añadir al menú las opciones y la función anónima

	}	
	
    public static void main(String[] args) {
    	// cambia el main para tratar todas las excepciones
    	
		// 1. Crear el canal de comunicación y establecer la
		// conexión con el servicio por defecto en localhost
		com = new CommClient();
		
		// activar el registro de mensajes del cliente
		com.activateMessageLog();  // opcional

	
		// 2. Crear la interfaz
		interfazCliente();
		// 3. Lanzar eventos mediante la interfaz
		do {
			;	
		} while (m.runSelection());
		// 4. Cerrar la entrada de la interfaz
		m.close();
		// cierra el canal de comunicación y
		// 5. Desconectar el cliente
		com.disconnect();
		
	} // main

} // class Cliente
