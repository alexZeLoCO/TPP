package servidor;

import comun.ISaludador;

/**
 * Clase del objeto de operaciones de servicio (OOS).
 *
 */
public class SaludadorOOS implements ISaludador {
	// área de datos
	private int estado;		// estado de control
	private String str;		// estado de datos
	
	private final static String SALUDO_DEFECTO = "¡Hola. Soy el saludador!";
	
	/**
	 * Crea el objeto de servicio con el saludo por defecto.
	 */
	public SaludadorOOS() {
		this(SALUDO_DEFECTO);
	}
	
	/**
	 * Crea el objeto de servicio con el saludo que se especifica.
	 * @param str el saludo del objeto
	 */
	public SaludadorOOS(String str) {
		this.str = (str == null || str.isEmpty()) ? SALUDO_DEFECTO : str;
		this.estado = 0;
	}
	
	@Override
	public String saluda() {
		return str;
	}

}
