
public abstract class Pokemon {

	public static Pokemon newPokemon(String pokemon) throws Exception {
		switch (pokemon) {
			case ("Pikachu"):
				return new Pikachu();
			case ("Charmander"):
				return new Charmander();
			case ("Angemon"):
				return new DigimonAdapted(new Angemon());
			case ("Agumon"):
				return new DigimonAdapted(new Agumon());
		}
		throw new Exception(pokemon + " no existe");
	}

	public void golpea() {
		// golpea a alg�n pokemon enemigo a tiro
	}

	public void retirate() {
		// vuelve a su pokeball
	}

	public void defender() {
		// el pokemon se concentra para recibir menos da�o en el siguiente ataque
	}
}
