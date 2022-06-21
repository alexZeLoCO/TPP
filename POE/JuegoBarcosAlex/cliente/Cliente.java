package cliente;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;
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

	/**
	 * Canal de comunicación del cliente. La comunicación con el
	 * servidor se establece al crear este objeto.
	 */
	private static CommClient com; // canal de comunicación del cliente

	private static Scanner scanner = new Scanner(System.in);

	private static int numBarcos()
			throws IOException, ChannelException {
		int n = 0;
		// crear mensaje a enviar
		ProtocolMessages peticion = new ProtocolMessages("numBarcosEnOceano");
		// enviar mensaje
		com.sendEvent(peticion);
		try {
			// esperar por la respuesta
			ProtocolMessages respuesta = com.waitReply();
			// procesar respuesta o excepción
			n = (int) com.processReply(respuesta);
		} catch (ClassNotFoundException | UnknownOperation e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (IOException | ChannelException e) {
			throw e;
		} catch (Exception e) {
			System.err.printf("%s: %s\n", e.getClass().getSimpleName(),
					e.getMessage());
		}

		return n;
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

	private static void tirar()
			throws IOException, ChannelException {
		// solicitar coordenadas del tiro
		System.out.print("Coordenadas del tiro? ");
		// crear mensaje a enviar
		ProtocolMessages peticion = new ProtocolMessages("coordenadasTiro", scanner.nextLine());
		// enviar mensaje
		com.sendEvent(peticion);
		try {
			// esperar por la respuesta
			ProtocolMessages respuesta = com.waitReply();
			// procesar respuesta o excepción
			System.out.println(com.processReply(respuesta));
		} catch (ClassNotFoundException | UnknownOperation e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (IOException | ChannelException e) {
			throw e;
		} catch (Exception e) {
			System.err.printf("%s: %s\n", e.getClass().getSimpleName(),
					e.getMessage());
		}
	}

	private static int miTurno()
			throws IOException, ChannelException {
		int n = 0;
		// crear mensaje a enviar
		ProtocolMessages peticion = new ProtocolMessages("turno");
		// enviar mensaje
		com.sendEvent(peticion);
		try {
			// esperar por la respuesta
			ProtocolMessages respuesta = com.waitReply();
			// procesar respuesta o excepción
			n = (int) com.processReply(respuesta);
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

		return n;
	}

	private static boolean inicioPartida()
			throws IOException, ChannelException {
		boolean comenzar = false;
		// crear mensaje a enviar
		ProtocolMessages peticion = new ProtocolMessages("iniciarJuego");
		// enviar mensaje
		com.sendEvent(peticion);
		try {
			// esperar por la respuesta
			ProtocolMessages respuesta = com.waitReply();
			// procesar respuesta o excepción
			comenzar = (boolean) com.processReply(respuesta);
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

		return comenzar;
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

	private static void sonar()
			throws IOException, ChannelException {
		// solicitar coordenadas del tiro
		System.out.print("Coordenadas del sonar? ");
		// crear mensaje a enviar
		ProtocolMessages peticion = new ProtocolMessages("sonar", scanner.nextLine());
		// enviar mensaje
		com.sendEvent(peticion);
		try {
			// esperar por la respuesta
			ProtocolMessages respuesta = com.waitReply();
			// procesar respuesta o excepción
			System.out.println(com.processReply(respuesta));
		} catch (ClassNotFoundException | UnknownOperation e) {
			System.err.printf("Recibido del servidor: %s\n", e.getMessage());
		} catch (IOException | ChannelException e) {
			throw e;
		} catch (Exception e) {
			System.err.printf("%s: %s\n", e.getClass().getSimpleName(),
					e.getMessage());
		}
	}

	public static void main(String[] args) {

		try {
			// establecer la comunicación con el servidor
			// crear el canal de comunicación y establecer la
			// conexión con el servicio por defecto en localhost
			com = new CommClient();
			// activa el registro de mensajes del cliente
			com.activateMessageLog(); // opcional
		} catch (UnknownHostException e) {
			System.err.printf("Servidor desconocido. %s\n", e.getMessage());
			System.exit(-1); // salida con error
		} catch (IOException | ChannelException e) {
			System.err.printf("Error: %s\n", e.getMessage());
			System.exit(-1); // salida con error
		}

		try {
			// colocar los barcos en el océano
			colocarBarcos();

			// si es posible (oponente disponible), comenzar el juego
			while (!inicioPartida()) {
				System.out.println("Esperando por un oponente");
				Thread.sleep(5000);
			}

			int n = miTurno();
			Menu menu = new Menu("Opcion", "~> ");
			menu.add("Tiro", () -> tirar());
			menu.add("Sonar", () -> sonar());
			while (n != JuegoBarcos.FINAL_JUEGO) {

				while (n == 0) { // esperando el turno
					// mostrar el océano
					verTabla("tableroBarcos");
					Thread.sleep(3000);
					n = miTurno();
					// mostrar el océano
					verTabla("tableroBarcos");
					Thread.sleep(500);
				}

				boolean primerTiro = true;
				while (n == 1) { // turno de juego
					// mostrar el tablero de tiros
					verTabla("tableroTiros");
					// disparar al océano del oponente
					if (primerTiro) {
						menu.runSelection();
					} else {
						tirar();
					}
					// mostrar el tablero de tiros
					verTabla("tableroTiros");
					Thread.sleep(500);
					n = miTurno();
					primerTiro = false;
				}

			}

			// mostrar el océano
			verTabla("tableroBarcos");

			if (n != JuegoBarcos.FINAL_JUEGO) { // se abandona el juego
				System.out.printf("\n¡Has abandonado!\n");
			} else { // el juego ha terminado
				System.out.printf("\nPartida finalizada: %s\n",
						numBarcos() == 0 ? "has perdido" : "has ganado");
			}
		} catch (IOException | ChannelException e) {
			System.err.printf("Error: %s\n", e.getMessage());
		} catch (Exception e) { // excepción del servicio
			System.err.printf("Error: %s\n", e.getMessage());
		} finally {
			// cerrar la entrada
			scanner.close();
			// cerrar el canal de comunicación y
			// desconectar el cliente
			com.disconnect();
		}

	} // main

}
