package cliente;

import java.io.IOException;
import java.net.UnknownHostException;

import lib.ChannelException;
import lib.CommClient;
import lib.Menu;
import lib.ProtocolMessages;
import lib.UnknownOperation;

public class Cliente {
	
	private static CommClient com;
	private static Menu m;
	
	private static void nBarcos () throws IOException, ChannelException {
		com.sendEvent(new ProtocolMessages("nBarcos"));
		try {
			System.out.println(com.processReply(com.waitReply()));
		} catch (ClassNotFoundException e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (UnknownOperation e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (IOException | ChannelException e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (Exception e) {
			System.err.printf("%s: %s\n", e.getClass().getSimpleName(), e.getMessage());
		}
	}

	private static void barcosPorColocar () throws IOException, ChannelException {
		com.sendEvent(new ProtocolMessages("barcosPorColocar"));
		try {
			System.out.println(com.processReply(com.waitReply()));
		} catch (ClassNotFoundException e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (UnknownOperation e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (IOException | ChannelException e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (Exception e) {
			System.err.printf("%s: %s\n", e.getClass().getSimpleName(), e.getMessage());
		}
	}

	private static void colocarBarco () throws IOException, ChannelException {
		System.out.println("Plantilla: A 1");
		com.sendEvent(new ProtocolMessages("colocarBarco", m.input().nextLine()));
		tableroBarcos();
		try {
			System.out.println(com.processReply(com.waitReply()));
		} catch (ClassNotFoundException e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (UnknownOperation e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (IOException | ChannelException e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (Exception e) {
			System.err.printf("%s: %s\n", e.getClass().getSimpleName(), e.getMessage());
		}
	}

	private static void tableroBarcos () throws IOException, ChannelException {
		com.sendEvent(new ProtocolMessages("tableroBarcos"));
		try {
			System.out.println(com.processReply(com.waitReply()));
		} catch (ClassNotFoundException e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (UnknownOperation e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (IOException | ChannelException e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (Exception e) {
			System.err.printf("%s: %s\n", e.getClass().getSimpleName(), e.getMessage());
		}
	}

	private static void iniciarJuego() throws IOException, ChannelException {
		com.sendEvent(new ProtocolMessages("iniciarJuego"));
		try {
			System.out.println(com.processReply(com.waitReply()));
		} catch (ClassNotFoundException e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (UnknownOperation e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (IOException | ChannelException e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (Exception e) {
			System.err.printf("%s: %s\n", e.getClass().getSimpleName(), e.getMessage());
		}
	}

	private static void estado () {
		try {
		com.sendEvent(new ProtocolMessages("estado"));
		} catch (ChannelException | IOException e) {
			e.printStackTrace();
		}
	}

	public static void interfazCliente () {
		m = new Menu ("\nJuego de Barcos", "Opcion Action? ");
		
		m.add("Numero de barcos en el oceano", () -> nBarcos());
		m.add("Barcos por colocar", () -> barcosPorColocar());
		m.add("Colocar barco", () -> colocarBarco());
		m.add("Ver tablero", () -> tableroBarcos());
		m.add("Iniciar Juego", () -> iniciarJuego());
		m.add("Estado", () -> estado());
	}

	public static void main (String[] args) {
		try {
			com = new CommClient();
			com.activateMessageLog();
			interfazCliente();
			
			do {
				;
			} while (m.runSelection());
		} catch (UnknownHostException e) {
			System.err.printf("Servidor desconocido: %s\n", e.getMessage());
			e.printStackTrace();
		} catch (IOException | ChannelException e) {
			System.err.printf("Error crítico: %s\n", e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		} catch (Exception e) {
			System.err.printf("Error: %s\n", e.getMessage());
			e.printStackTrace();
		} finally {
			m.close();
			com.disconnect();
		}
	}
}
