package wc1.base;

// Inteligencia artificial de los orcos
public class IAOrcos {

	/**
	 * Generaci�n de un grupo de ataque de la I.A.
	 * 
	 * @return Un grupo de ataque orco
	 */
	public Unidad[] creaGrupoDeAtaque() {
		// Array de Unidades Orcas
		Unidad[] grupoDeAtaque = new Unidad[10];

		// 4 x infanteria
		for (int x = 0; x < 4; x++)
			grupoDeAtaque[x] = new Grunt();
		// 3 x arqueros
		for (int x = 4; x < 7; x++)
			grupoDeAtaque[x] = new LanzadorDeJabalinas();
		// 2 x jinetes
		grupoDeAtaque[7] = new Incursor();
		grupoDeAtaque[8] = new Incursor();
		// 1 x m�quina de asedio
		grupoDeAtaque[9] = new Catapulta();

		return grupoDeAtaque;
	}
}

// Inteligencia artificial de los humanos (= a la I.A. de los orcos)
class IAHumanos {
	// ...
}
