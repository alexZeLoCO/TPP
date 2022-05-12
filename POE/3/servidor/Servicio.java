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

	// Información exclusiva de un OOS
	private int idClient;						// identificador
	private List<Integer> barcosRestantes;		// tamaños de barcos pendientes de colocar en el tablero
	private Tablero oceano;						// tablero propio con los barcos
	private Tablero tiros;						// tablero contrario, inicialmente en blanco
	private int estado;							// estado del juego

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

		Pair<Integer, Integer> p0 = position(str0);
		Pair<Integer, Integer> p1 = position(str1);

		Integer size = this.oceano.colocarBarco(p0, p1, this.barcosRestantes);

		// se ha colocado un barco de tamaño size
		this.barcosRestantes.remove(size);
		barcosEnOceano.put(this.idClient, barcosEnOceano.get(this.idClient) + 1);
	}

	/**
	 * Cambia el estado del OOS del jugador según haya hecho blanco
	 * o no en el último tiro.
	 * @param blanco {@code true} si el tiro ha alcanzado un barco
	 * del oponente
	 */
	private void actualizarEstado(boolean blanco) {
	}

}
