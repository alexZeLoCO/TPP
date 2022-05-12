package servidor;

import java.util.ArrayList;
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
	private volatile static List<Integer> jugadoresEnEspera = new LinkedList<Integer>();
	// tableros con la disposición de los barcos de los jugadores
	// (como clave se utiliza el identificador del cliente)
	private volatile static Map<Integer, Tablero> oceanoJugadores = new HashMap<Integer, Tablero>();
	// oponentes en juego (clave y valor, identificador del cliente)
	private volatile static Map<Integer, Integer> oponente = new HashMap<Integer, Integer>();
	// indica si es el turno de juego de un jugador
	private volatile static Map<Integer, Boolean> turnoJugador = new HashMap<Integer, Boolean>();
	// número de barcos no hundidos de cada jugador en el océano
	private volatile static Map<Integer, Integer> barcosEnOceano = new HashMap<Integer, Integer>();

	// Información exclusiva de un OOS
	private int idClient;						// identificador
	private List<Integer> barcosRestantes;		// tamaños de barcos pendientes de colocar en el tablero
	private Tablero oceano;						// tablero propio con los barcos
	private Tablero tiros;						// tablero contrario, inicialmente en blanco
	private int estado;							// estado del juego

	/**
	 * Crea un nuevo servicio
	 */
	public Servicio (int id) {
		// non-static
		this.idClient = id;
		this.barcosRestantes = new ArrayList<Integer> (BARCOS);
		this.oceano = new Tablero ();
		this.tiros  = new Tablero ();
		this.estado = 0;
		// static
		oceanoJugadores.put(this.idClient, this.oceano);
		barcosEnOceano.put(this.idClient, 0);
		turnoJugador.put(this.idClient, false);
	}

	public String datos () {
		return String.format("%s\n%s\n%d", oponente.toString(), turnoJugador.toString(), this.estado);
	}

	public String tableroBarcos () {
		return this.oceano.toString();
	}
	
	public String tableroTiros () {
		return this.tiros.toString();
	}

	public List<Integer> barcosPorColocar () throws AccionNoPermitida {
		if (this.estado > 1) {
			throw new AccionNoPermitida("barcosPorColocar");
		}
		return this.barcosRestantes;
	}

	public int turno () throws AccionNoPermitida {
		if (this.estado < 2) {
			throw new AccionNoPermitida("turno");
		}
		if (barcosEnOceano.get(this.idClient) == 0 || barcosEnOceano.get(oponente.get(this.idClient)) == 0) {
			return FINAL_JUEGO;
		}
		if (this.estado == FINAL_JUEGO) {
			return this.estado;
		}
		if (this.estado == 2) {
			// synchronized (nextTurno.get(this.idClient)) {
			if (turnoJugador.get(this.idClient)) {
				this.estado = 3;
			} else {
				return 0;
			}
			// }
		}
		return 1;
	}

	/**
	 * Retorna las coordenadas proporcionadas por la cadena dada.
	 * @param str representación como cadena de caracteres de las coordenadas
	 * @return las coordenadas
	 * @throws CoordenadasNoValidas si el formato de la cadena no es correcto
	 * o la posición dada desborda el tablero
	 */
	private Pair<Integer, Integer> position(String str)
			throws CoordenadasNoValidas {

		if (str == null || str.isEmpty() || !str.matches("^\\p{Alpha}\\d{1,2}$")) {
			throw new CoordenadasNoValidas(
					"Patrón de coordenadas: ^\\p{Alpha}\\d{1,2}$");
		}

		int row = str.toUpperCase().charAt(0) - (int)'A';
		int col = str.charAt(1) - (int)'0';

		if (str.length() > 2) {
			col = col * 10 + str.charAt(2) - (int)'0';
		}

		if (row < 0 || row > DIMENSION ||
				col < 0 || col > DIMENSION) {
			throw new CoordenadasNoValidas("Tablero desbordado");
		}

		return new Pair<>(row, col);
	}

	@Override
	public String coordenadasTiro (String tiro) throws AccionNoPermitida, CoordenadasNoValidas {
		if (this.estado < 2 || this.estado == FINAL_JUEGO || !turnoJugador.get(this.idClient)) {
			throw new AccionNoPermitida(String.format("coordenadasTiro | estado: %d", this.estado));
		}
		Pair<Integer, Integer> pos = position(tiro);
		if (pos.first() < 0 || pos.first() >= DIMENSION || pos.second() < 0 || pos.second() >= DIMENSION) {
			throw new CoordenadasNoValidas(String.format("coordenadasTiro | coords: (%d, %d)", pos.first(), pos.second()));
		}
		Celda objetivo = oceanoJugadores.get(oponente.get(this.idClient)).tiro(pos.first(), pos.second());
		if (objetivo == (Celda.AGUA) || objetivo == (Celda.AGUA_TORPEDEADA)) {
			// Agua
			this.tiros.tabla[pos.first()][pos.second()] = Celda.AGUA_TORPEDEADA;
			this.actualizarEstado(false);
			return "AGUA";
		}
		if (objetivo == (Barco.TOCADO)) {
			// Tocado
			this.tiros.tabla[pos.first()][pos.second()] = Barco.TOCADO;
			this.actualizarEstado(true);
			return "TOCADO";
		}
		// Hundido
		// System.out.println((objetivo instanceof Barco) ? objetivo.toString() : ((Barco) objetivo).toString());
		this.tiros.registrarBarco((Barco) objetivo);
		this.actualizarEstado(true);
		barcosEnOceano.put(this.idClient, barcosEnOceano.get(this.idClient) - 1);
		System.out.println(barcosEnOceano.get(oponente.get(this.idClient)).toString());
		return "HUNDIDO";
	} // coordenadasTiro

	@Override
	public void colocarBarco(String str)
			throws CoordenadasNoValidas, BarcoMalPosicionado, TamanioBarcoNoValido, AccionNoPermitida {
		if (this.estado != 0) {
			throw new AccionNoPermitida("colocarBarco");
		}

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

		Integer size = this.oceano.colocarBarco(position(str0), position(str1), this.barcosRestantes);

		// se ha colocado un barco de tamaño size
		this.barcosRestantes.remove(size);
		barcosEnOceano.put(this.idClient, barcosEnOceano.get(this.idClient) + 1);
		if (this.barcosRestantes.isEmpty()) {
			this.estado = 1;
		}
	}

	/**
	 * Cambia el estado del OOS del jugador según haya hecho blanco
	 * o no en el último tiro.
	 * @param blanco {@code true} si el tiro ha alcanzado un barco
	 * del oponente
	 */
	private void actualizarEstado(boolean blanco) {
		if (barcosEnOceano.get(this.idClient) == 0) {
			this.estado = FINAL_JUEGO;
		}
		if (blanco && this.estado != 5) {
			this.estado++;
		} else {
			turnoJugador.put(this.idClient, false);
			turnoJugador.put(oponente.get(this.idClient), true);
			this.estado = 2;
		}
	}

	public int numBarcosEnOceano () {
		return barcosEnOceano.get(this.idClient);
	}

	/*
	// Original foo
	@Override
	public boolean iniciarJuego () throws AccionNoPermitida {
		if (this.estado != 1) {
			throw new AccionNoPermitida("iniciarJuego");
		}
		if (oponente.get(idClient) != null) {
			this.estado = 2;
		} else {
			if (jugadoresEnEspera.isEmpty()) {
				jugadoresEnEspera.add(this.idClient);	// Lista vacia, jugador tiene que esperar
			} else {
				if (jugadoresEnEspera.contains(this.idClient)) {
					if (jugadoresEnEspera.size() == 1) {
						return false;	// Jugador solo en la lista
					}
					jugadoresEnEspera.remove((Integer)this.idClient);	// Jugador en lista con otro jugador
				}
				int idOponente = jugadoresEnEspera.remove(0);
				oponente.put(idOponente, this.idClient);
				oponente.put(this.idClient, idOponente);
				turnoJugador.put(this.idClient, true);
				this.estado = 2;
			}
		}
		return this.estado == 2;
	}
	*/

	@Override
	public boolean iniciarJuego () throws AccionNoPermitida {
		if (this.estado != 1) {
			throw new AccionNoPermitida("iniciarJuego");
		}
		if (oponente.get(idClient) != null) {
			this.estado = 2;
			return true;
		}
		if (jugadoresEnEspera.isEmpty()) {
			jugadoresEnEspera.add(this.idClient);	// Lista vacia, jugador tiene que esperar
			return this.estado == 2;
		}
		if (jugadoresEnEspera.contains(this.idClient)) {
			if (jugadoresEnEspera.size() == 1) {
				return false;	// Jugador solo en la lista
			}
			jugadoresEnEspera.remove((Integer)this.idClient);	// Jugador en lista con otro jugador
		}
		int idOponente = jugadoresEnEspera.remove(0);
		oponente.put(idOponente, this.idClient);
		oponente.put(this.idClient, idOponente);
		turnoJugador.put(this.idClient, true);
		turnoJugador.put(idOponente, false);
		this.estado = 3;
		return true;
	}
	
	@Override
	public void close () {
		if (this.estado < 2) {
			jugadoresEnEspera.remove(this.idClient);
		}
		int idOponente = oponente.get(this.idClient);
		if (oponente.get(idOponente) != null) {
			turnoJugador.put(oponente.get(idOponente), true);
		} else {
		oceanoJugadores.remove(this.idClient);
		oceanoJugadores.remove(idOponente);
		barcosEnOceano.remove(this.idClient);
		barcosEnOceano.remove(idOponente);
		turnoJugador.remove(this.idClient);
		turnoJugador.remove(idOponente);
		}
		oponente.remove(this.idClient);
		JuegoBarcos.super.close();
	}
}
