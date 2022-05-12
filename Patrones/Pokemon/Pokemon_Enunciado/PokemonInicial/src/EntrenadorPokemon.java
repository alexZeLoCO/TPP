
public class EntrenadorPokemon {
	private Pokemon p1; // primer pokemon de su equipo
	private Pokemon p2; // segundo pokemon de su equipo

	public EntrenadorPokemon(String p1, String p2) throws Exception {
		this.p1 = Pokemon.newPokemon(p1);
		this.p2 = Pokemon.newPokemon(p2);
	}

	/*
	 * public EntrenadorPokemon() throws Exception {
	 * this("Pikachu", "Charmander");
	 * }
	 */

	public EntrenadorPokemon() throws Exception {
		this("Angemon", "Agumon");
	}

	public void planDefensivo() {
		iCommand[] defender = { new Defender(p1), new Defender(p2) };
		for (iCommand c : defender) {
			c.ejecutar();
		}
	}

	public void planOfensivo() {
		iCommand[] ofensivo = { new Atacar(p1), new Atacar(p2), new Atacar(p1), new Atacar(p2) };
		for (iCommand c : ofensivo) {
			c.ejecutar();
		}
	}
}
