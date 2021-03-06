package servidor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import comun.AccionNoPermitida;
import comun.BarcoMalPosicionado;
import comun.CoordenadasNoValidas;
import comun.JuegoBarcos;
import comun.TamanioBarcoNoValido;

public class Servicio implements JuegoBarcos {
	private final static String PATTERN_COORD = "^(\\p{Alpha}\\d{1,2}\\s*){2}$";

	// Información común para todos los OOSs

	// jugadores conectados al servicio y esperando para jugar
	// (se requiere que el jugador haya colocado todos sus barcos)
	private volatile static List<Integer> jugadoresEnEspera = new LinkedList<>();
	// tableros con la disposición de los barcos de los jugadores
	// (como clave se utiliza el identificador del cliente)
	private volatile static Map<Integer, Tablero> oceanoJugadores = new HashMap<>();
	// oponentes en juego (clave y valor, identificador del cliente)
	private volatile static Map<Integer, Integer> oponente = new HashMap<>();
	// indica si es el turno de juego de un jugador
	private volatile static Map<Integer, Boolean> turnoJugador = new HashMap<>();
	// número de barcos no hundidos de cada jugador en el océano
	private volatile static Map<Integer, Integer> barcosEnOceano = new HashMap<>();
	// objeto para exclusión mutua de comprobación de inicio de partida
	private volatile static Object mutex = new Object();
	// objetos de exclusión mutua para el turno
	private volatile static Map<Integer, Object> mutexTurno = new HashMap<>();

	// Información exclusiva de un OOS
	private int idClient; // identificador
	private List<Integer> barcosRestantes; // tamaños de barcos pendientes de colocar en el tablero
	private Tablero oceano; // tablero propio con los barcos
	private Tablero tiros; // tablero contrario, inicialmente en blanco
	private int estado; // estado del juego

	public Servicio(int idClient) {
		this.idClient = idClient;
		this.barcosRestantes = new LinkedList<>(BARCOS);
		this.oceano = new Tablero();
		this.tiros = new Tablero();
		synchronized (oceanoJugadores) {
			oceanoJugadores.put(idClient, this.oceano);
		}
		synchronized (turnoJugador) {
			turnoJugador.put(idClient, false);
		}
		synchronized (barcosEnOceano) {
			barcosEnOceano.put(idClient, 0);
		}
		this.estado = 0; // el jugador ya puede colocar sus barcos en el océano
	}

	@Override
	public String tableroBarcos() {
		return this.oceano.toString();
	}

	@Override
	public String tableroTiros() {
		return this.tiros.toString();
	}

	@Override
	public List<Integer> barcosPorColocar() throws AccionNoPermitida {
		if (this.estado != 0) {
			throw new AccionNoPermitida("barcosPorColocar");
		}

		if (this.barcosRestantes.isEmpty()) { // cambiar de estado
			this.estado = 1;
		}

		return this.barcosRestantes;
	}

	/**
	 * Retorna las coordenadas proporcionadas por la cadena dada.
	 * 
	 * @param str representación como cadena de caracteres de las coordenadas
	 * @return las coordenadas
	 * @throws CoordenadasNoValidas si el formato de la cadena no es correcto
	 *                              o la posición dada desborda el tablero
	 */
	private Pair<Integer, Integer> position(String str)
			throws CoordenadasNoValidas {

		if (str == null || str.isEmpty() || !str.matches("^\\p{Alpha}\\d{1,2}$")) {
			throw new CoordenadasNoValidas(
					"Patrón de coordenadas: ^\\p{Alpha}\\d{1,2}$");
		}

		int row = str.toUpperCase().charAt(0) - (int) 'A';
		int col = str.charAt(1) - (int) '0';

		if (str.length() > 2) {
			col = col * 10 + str.charAt(2) - (int) '0';
		}

		if (row < 0 || row >= JuegoBarcos.DIMENSION ||
				col < 0 || col >= JuegoBarcos.DIMENSION) {
			throw new CoordenadasNoValidas("Tablero desbordado");
		}

		return new Pair<>(row, col);
	}

	@Override
	public void colocarBarco(String str)
			throws CoordenadasNoValidas, BarcoMalPosicionado, TamanioBarcoNoValido, AccionNoPermitida {
		if (this.estado != 0) {
			throw new AccionNoPermitida("colocarBarco");
		}

		// comprobar que la cadena es válida
		if (str == null || str.isEmpty() || !str.matches(PATTERN_COORD)) {
			throw new IllegalArgumentException(
					String.format("Patrón de coordenadas: %s", PATTERN_COORD));
		}

		Pattern pattern = Pattern.compile("\\p{Alpha}\\d{1,2}");
		Matcher matcher = pattern.matcher(
				str.subSequence(0, str.length()));
		matcher.find();
		String str0 = matcher.group();
		matcher.find();
		String str1 = matcher.group();

		// coordenadas de los extremos del barco
		Pair<Integer, Integer> p0 = position(str0);
		Pair<Integer, Integer> p1 = position(str1);

		Integer size = this.oceano.colocarBarco(p0, p1, this.barcosRestantes);

		// se ha colocado un barco de tamaño size
		this.barcosRestantes.remove(size);
		barcosEnOceano.put(this.idClient, barcosEnOceano.get(this.idClient) + 1);
	}

	@Override
	public boolean iniciarJuego() throws AccionNoPermitida {
		if (this.estado != 1) {
			throw new AccionNoPermitida("iniciarJuego");
		}
		synchronized (mutex) {
			if (oponente.get(this.idClient) != null) {
				// el jugador tiene oponente, pero no el turno inicial
				this.estado = 2;
			} else {
				// el jugador no tiene oponente, si es posible asignarle
				// uno que esté en espera
				if (jugadoresEnEspera.isEmpty()) {
					// esperar por un oponente
					jugadoresEnEspera.add(this.idClient);
				} else {
					if (jugadoresEnEspera.contains(idClient)) {
						if (jugadoresEnEspera.size() == 1) {
							// seguir esperando por un oponente
							return false;
						}

						// se puede formar una pareja de juego
						jugadoresEnEspera.remove((Integer) idClient);
					}

					// una vez asegurado que este jugador no está
					// en la lista de espera, se elige como oponente
					// al primero en espera
					int idOponente = jugadoresEnEspera.remove(0);
					oponente.put(this.idClient, idOponente);
					oponente.put(idOponente, this.idClient);
					// objeto de exclusión mutua para el turno de
					// ambos oponentes
					mutexTurno.put(this.idClient, new Object());
					mutexTurno.put(idOponente, mutexTurno.get(this.idClient));
					// el jugador que hace que se forme la pareja
					// de juego tiene el turno
					turnoJugador.put(this.idClient, true);
					this.estado = 2;
				}
			}
		}

		return this.estado == 2;
	}

	@Override
	public int turno() throws AccionNoPermitida {
		if (this.estado < 2) {
			throw new AccionNoPermitida("turno");
		}

		// asegurarse de que el oponente sigue conectado
		int idOponente = oponente.get(this.idClient);
		if (oponente.get(idOponente) == null) {
			// el oponente se ha desconectado
			this.estado = FINAL_JUEGO;
			System.out.println(this.estado);
			return this.estado;
		}

		// condición de finalización del juego
		if (barcosEnOceano.get(this.idClient) == 0 ||
				barcosEnOceano.get(idOponente) == 0) {
			this.estado = FINAL_JUEGO;
			return this.estado;
		}

		if (this.estado == 2) {
			synchronized (mutexTurno.get(this.idClient)) {
				if (turnoJugador.get(this.idClient)) {
					this.estado = 3;
				} else { // no tiene el turno
					return 0;
				}
			}
		}

		// tiene el turno
		return 1;
	}

	/**
	 * Cambia el estado del OOS del jugador según haya hecho blanco
	 * o no en el último tiro.
	 * 
	 * @param blanco {@code true} si el tiro ha alcanzado un barco
	 *               del oponente
	 */
	private void actualizarEstado(boolean blanco) {
		if (blanco && this.estado != 5) { // mantiene el turno
			this.estado++;
		} else { // cambia el turno
			this.estado = 2;
			synchronized (mutexTurno.get(this.idClient)) {
				turnoJugador.put(this.idClient, false);
				turnoJugador.put(oponente.get(this.idClient), true);
			}
		}
	}

	@Override
	public String coordenadasTiro(String tiro)
			throws AccionNoPermitida, CoordenadasNoValidas {
		if (this.estado < 2 || this.estado == FINAL_JUEGO) {
			throw new AccionNoPermitida("coordenadasTiro");
		}
		if (this.estado == 2) {
			throw new AccionNoPermitida("No es tú turno");
		}

		if (this.estado == 5) {
			this.nuke(tiro);
			return "Nuke";
		}

		// coordenadas del tiro
		Pair<Integer, Integer> p = position(tiro);
		int row = p.first();
		int col = p.second();

		return tiro(row, col);
	}

	public void nuke(String tiro) throws AccionNoPermitida, CoordenadasNoValidas {
		if (this.estado < 2 || this.estado == FINAL_JUEGO) {
			throw new AccionNoPermitida("coordenadasTiro");
		}
		if (this.estado == 2) {
			throw new AccionNoPermitida("No es tú turno");
		}
		if (this.estado != 5) {
			throw new AccionNoPermitida("No se puede hacer aún");
		}

		Pair<Integer, Integer> pos = position(tiro);
		Celda c = oceanoJugadores.get(oponente.get(this.idClient)).tabla[pos.first()][pos.second()];
		Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> p = c.extremos();
		if (p.first().first() != p.second().first()) {
			for (int i = p.first().first(); i <= p.second().first(); i++) {
				this.tiro(i, p.first().second());
			}
		} else {
			for (int i = p.first().second(); i <= p.second().second(); i++) {
				this.tiro(p.first().first(), i);
			}
		}
	}

	private String tiro(int row, int col) {
		Celda celda = oceanoJugadores.get(oponente.get(this.idClient)).tiro(row, col);
		// registrar el tiro en la tabla de tiros
		if (!celda.esBarco()) { // agua
			this.tiros.tabla[row][col] = Celda.AGUA_TORPEDEADA;
			actualizarEstado(false);
			if (celda == Celda.AGUA) {
				return "Agua";
			}

			if (celda == Celda.SEPARADOR) {
				return "Agua: tiro desperdiciado, separación entre barcos";
			}

			if (celda == Celda.AGUA_TORPEDEADA) {
				return "Agua: tiro repetido";
			}
		}

		if (celda == Barco.HUNDIDO) { // se ha alcanzado una celda de un barco ya hundido
			actualizarEstado(true);
			return "Hundido: tiro repetido";
		}

		if (celda == Barco.TOCADO) { // barco tocado
			this.tiros.tabla[row][col] = celda;
			actualizarEstado(true);
			return "Tocado";
		}

		// barco hundido, regirstrar éste en la tabla de tiros
		this.tiros.registrarBarco((Barco) celda);
		int idOponente = oponente.get(this.idClient);
		barcosEnOceano.put(idOponente, barcosEnOceano.get(idOponente) - 1);
		actualizarEstado(true);
		return "Hundido";

	}

	public String sonar(String tiro) throws AccionNoPermitida, CoordenadasNoValidas {
		if (this.estado < 2) {
			throw new AccionNoPermitida("sonar");
		}
		if (this.estado == 2) {
			throw new AccionNoPermitida("No es tu turno");
		}
		Pair<Integer, Integer> p = position(tiro);
		int row, col;
		for (int i = -1; i < 2; i++) {
			row = p.first() + i;
			for (int j = -1; j < 2; j++) {
				col = p.second() + j;
				Celda celda = oceanoJugadores.get(oponente.get(this.idClient)).tiro(row, col);
				if (!celda.esBarco()) {
					this.tiros.tabla[row][col] = Celda.AGUA_TORPEDEADA;
				}
				if (celda == Barco.TOCADO) {
					this.tiros.tabla[row][col] = Barco.BARCO;
					((Barco) celda).ch = Barco.BARCO.ch;
				}
			}
		}
		return "sonar";
	}

	@Override
	public int numBarcosEnOceano() {
		return barcosEnOceano.get(this.idClient);
	}

	@Override
	public void close() {
		if (this.estado < 2) { // desconexión sin haber comenzado la partida
			synchronized (jugadoresEnEspera) {
				jugadoresEnEspera.remove((Integer) this.idClient);
			}
		}

		int idOponente = oponente.get(this.idClient);
		if (oponente.get(idOponente) != null) { // el oponente sigue conectado
			synchronized (mutexTurno.get(this.idClient)) {
				turnoJugador.put(oponente.get(idOponente), true);
			}
		} else { // el oponente está desconectado
			// eliminar las información compartida de ambos contrincantes
			synchronized (oceanoJugadores) {
				oceanoJugadores.remove(this.idClient);
				oceanoJugadores.remove(idOponente);
			}
			synchronized (barcosEnOceano) {
				barcosEnOceano.remove(this.idClient);
				barcosEnOceano.remove(idOponente);
			}
			synchronized (turnoJugador) {
				turnoJugador.remove(this.idClient);
				turnoJugador.remove(idOponente);
			}
			synchronized (mutexTurno) {
				mutexTurno.remove(this.idClient);
				mutexTurno.remove(idOponente);
			}
		}

		// este jugador ya no necesita saber quien es su oponente
		synchronized (oponente) {
			oponente.remove(this.idClient);
		}

		JuegoBarcos.super.close();
	}

}
