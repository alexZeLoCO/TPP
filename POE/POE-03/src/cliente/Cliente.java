package cliente;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import comun.AccionNoPermitida;
import comun.BarcoMalPosicionado;
import comun.CoordenadasNoValidas;
import comun.JuegoBarcos;
import comun.TamanioBarcoNoValido;
import lib.ChannelException;
import lib.CommClient;
import lib.Menu;
import lib.ProtocolMessages;
import lib.UnknownOperation;

public class Cliente {

	private static CommClient com;
	private static Menu m;

	private static int numBarcos () {
		try {
			com.sendEvent(new ProtocolMessages("nBarcos"));
			Object reply = com.processReply(com.waitReply());
			System.out.println(reply);
			return (int) reply;
		} catch (ClassNotFoundException e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (UnknownOperation e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (IOException | ChannelException e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (Exception e) {
			System.err.printf("%s: %s\n", e.getClass().getSimpleName(), e.getMessage());
		}
		return 0;
	}

	private static void verTabla(String id)
			throws IOException, ChannelException {
		// crear mensaje a enviar
		ProtocolMessages peticion = new ProtocolMessages(id);
		// enviar mensaje
		com.sendEvent(peticion);
		try {
			// esperar por la respuesta
			ProtocolMessages respuesta = com.waitReply();
			// procesar respuesta o excepción
			System.out.println(com.processReply(respuesta));
		} catch (ClassNotFoundException | UnknownOperation e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (AccionNoPermitida | CoordenadasNoValidas e) {
			System.err.printf("Error: %s\n", e.getMessage());
		} catch (IOException | ChannelException e) {
			throw e;
		} catch (Exception e) {
			System.err.printf("%s: %s\n", e.getClass().getSimpleName(),
					e.getMessage());
		}		
	}

	@SuppressWarnings("unchecked")
	private static Set<Integer> barcosPendientes()
			throws IOException, ChannelException {
		Set<Integer> tamanios = new TreeSet<>();
		// crear mensaje a enviar
		ProtocolMessages peticion = new ProtocolMessages("barcosPorColocar");
		// enviar mensaje
		com.sendEvent(peticion);
		try {
			// esperar por la respuesta
			ProtocolMessages respuesta = com.waitReply();
			// procesar respuesta o excepción
			List<Integer> pendientes = (List<Integer>) com.processReply(respuesta);
			pendientes.forEach(n -> tamanios.add(n));
		} catch (ClassNotFoundException | UnknownOperation e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (AccionNoPermitida e) {
			System.err.printf("Accion no permitida: %s\n", e.getMessage());
		} catch (IOException | ChannelException e) {
			throw e;
		} catch (Exception e) {
			System.err.printf("%s: %s\n", e.getClass().getSimpleName(),
					e.getMessage());
		}

		return tamanios;
	}

	private static void colocarBarcos()
			throws IOException, ChannelException {
		// solicitar los de tamaños de barcos pendientes
		// de colocar en el océano
		Set<Integer> barcos = barcosPendientes();
		int tamanio;
		while (!barcos.isEmpty()) { // quedan barcos por colocar
			// muestra el océano
			verTabla("tableroBarcos");

			// presenta un menú con los tamaños de barcos por colocar
			Menu tamanios = new Menu("\nTamaños de Barcos", "Opción ? ", false);
			if (barcos.size() > 1) {
				barcos.forEach(n -> tamanios.add(String.format("Barco de tamaño %d", n), n));
				tamanio = tamanios.getInteger();
			} else { // sólo queda un tamaño (puede quedar más de un barco por colocar)
				tamanio = barcos.iterator().next();
			}
			// solicitar las coordenadas de los extremos del barco
			System.out.printf("Coordenadas de los extremos del barco de tamaño %d? ", tamanio);
			// crear el mensaje a enviar
			ProtocolMessages peticion = new ProtocolMessages("colocarBarco",
					tamanios.input().nextLine());
			// enviar mensaje
			com.sendEvent(peticion);
			try {
				// esperar por la respuesta
				ProtocolMessages respuesta = com.waitReply();
				// procesar respuesta o excepción
				com.processReply(respuesta);
			} catch (ClassNotFoundException | UnknownOperation e) {
				System.err.printf("Recibido del servidor: %s\n", e.getMessage());
			} catch (CoordenadasNoValidas | BarcoMalPosicionado | TamanioBarcoNoValido e) {
				System.err.printf("Error: %s\n", e.getMessage());
			} catch (AccionNoPermitida e) {
				System.err.printf("Accion no permitida: %s\n", e.getMessage());
			} catch (Exception e) {
				System.err.printf("%s: %s\n", e.getClass().getSimpleName(),
						e.getMessage());
			} finally {
				barcos = barcosPendientes();
			}
		}

		// muestra el océano
		verTabla("tableroBarcos");
	}

	private static void interfazCliente() {
		m = new Menu("\nJuego de Barcos", "Opción ? ");

		m.add("Número de barcos en el océano.", () -> numBarcos());
		m.add("Realizar un tiro al océano del oponente.", () -> tirar());
	}	
	/*
	public static void interfazCliente () {
		m = new Menu ("\nJuego de Barcos", "Opcion Action? ");

		m.add("Numero de barcos en el oceano", () -> numBarcos());
		m.add("Barcos por colocar", () -> barcosPorColocar());
		m.add("Colocar barco", () -> colocarBarco());
		m.add("Ver tablero", () -> tableroBarcos());
		m.add("Iniciar Juego", () -> iniciarJuego());
		m.add("Estado", () -> estado());
	}
	 */

	public static boolean inicioPartida () {
		try {
			com.sendEvent(new ProtocolMessages("iniciarJuego"));
			return (boolean) com.processReply(com.waitReply());
		} catch (IOException | ChannelException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownOperation e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static int miTurno () throws Exception {
		try {
			com.sendEvent(new ProtocolMessages("turno"));
			return (int) com.processReply(com.waitReply());
		} catch (IOException | ChannelException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (UnknownOperation e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void tirar () {
		try {
			com.sendEvent(new ProtocolMessages("tirar", m.input().nextLine()));
			com.processReply(com.waitReply());
		} catch (IOException | ChannelException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownOperation e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main (String[] args) {
		try {
			// establecer la comunicación con el servidor
			// crear el canal de comunicación y establecer la
			// conexión con el servicio por defecto en localhost
			com = new CommClient();
			// activa el registro de mensajes del cliente
			com.activateMessageLog();  // opcional			
		} catch (UnknownHostException e) {
			System.err.printf("Servidor desconocido. %s\n", e.getMessage());
			System.exit(-1);	// salida con error
		} catch (IOException | ChannelException e) {
			System.err.printf("Error: %s\n", e.getMessage());
			System.exit(-1);	// salida con error
		}

		try {
			// colocar los barcos en el océano
			colocarBarcos();

			// si es posible (oponente disponible), comenzar el juego
			while (!inicioPartida()) {
				System.out.println("Esperando por un oponente");
				Thread.sleep(5000);
			}

			// crear la interfaz (menú para lanzar eventos)
			interfazCliente();

			// lanzar eventos mediante la interfaz
			int n;
			do {
				// preguntar al servicio si me corresponde el turno
				n = miTurno();
				while (n == 0 && n != JuegoBarcos.FINAL_JUEGO) { // no tengo el turno
					// mostrar el tablero de tiros
					verTabla("tableroTiros");

					while (n == 0) { // esperando el turno
						Thread.sleep(3000);
						n = miTurno();

						// mostrar el océano
						verTabla("tableroBarcos");
					}

					// mostrar el océano
					verTabla("tableroBarcos");
				}

				// mostrar el tablero de tiros
				verTabla("tableroTiros");
			} while (n != JuegoBarcos.FINAL_JUEGO && m.runSelection());

			if (n != JuegoBarcos.FINAL_JUEGO) { // se abandona el juego
				System.out.printf("\n¡Has abandonado!\n");
			} else { // el juego ha terminado
				System.out.printf("\nPartida finalizada: %s\n",
						numBarcos() == 0 ? "has ganado" : "has perdido");
			}
		} catch (IOException | ChannelException e) {
			System.err.printf("Error: %s\n", e.getMessage());
		} catch (Exception e) { // excepción del servicio
			System.err.printf("Error: %s\n", e.getMessage());
		} finally {
			// cerrar la entrada de la interfaz
			m.close();
			// cerrar el canal de comunicación y
			// desconectar el cliente
			com.disconnect();
		}
	}

}
