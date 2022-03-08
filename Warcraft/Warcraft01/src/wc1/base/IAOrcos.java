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

	public Unidad[] creaGrupoDeAtaque(String especie) {
		FAbstracta fa;
		Unidad[] grupoDeAtaque = new Unidad[10];
		if (especie.equals("Orcos")) {
			fa = new FAOrcos();
		} else {
			fa = new FAHumanos();
		}

		// 4 x infanteria
		for (int x = 0; x < 4; x++)
			grupoDeAtaque[x] = fa.creaInfanteria();
		// 3 x arqueros
		for (int x = 4; x < 7; x++)
			grupoDeAtaque[x] = fa.creaArquero();
		// 2 x jinetes
		grupoDeAtaque[7] = fa.creaJinete();
		grupoDeAtaque[8] = fa.creaJinete();
		// 1 x m�quina de asedio
		grupoDeAtaque[9] = fa.creaMA();

		return grupoDeAtaque;
	}
}

// Inteligencia artificial de los humanos (= a la I.A. de los orcos)
class IAHumanos {
	// ...
}
