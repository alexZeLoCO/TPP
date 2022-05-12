package wc1.base;

public class Unidad {

	protected int hitPoints = 0;
	protected int armor = 0;
	protected int attackDamage = 0;
	protected int range = 0;

	// acciones
	void attack() {
	}

	void defend() {
	}

	void move() {
	}

	@Deprecated
	public static Unidad factoria(String tipo) throws Exception {
		switch (tipo) {
			case ("Grunt"):
				return new Grunt();
			case ("Soldado"):
				return new SoldadoRaso();
			// And the rest...
		}
		throw new Exception("Non-existent unit");
	}

	@Deprecated
	public static Unidad factoria(String tipo, String bando) throws Exception {
		switch (tipo) {
			case ("Infanteria"):
				if (bando.equals("Orcos")) {
					return new Grunt();
				} else {
					return new SoldadoRaso();
				}
				// And the rest...
		}
		throw new Exception("Non-existent unit");
	}
}
