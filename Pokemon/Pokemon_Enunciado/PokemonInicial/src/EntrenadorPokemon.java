
public class EntrenadorPokemon {
	private Pokemon p1; // primer pokemon de su equipo
	private Pokemon p2; // segundo pokemon de su equipo
	
	
	public EntrenadorPokemon(){
		p1 = new Pikachu();
		p2 = new Charmander();
	}
	
	public void planDefensivo(){
		p1.defender();
		p2.defender();
	}

	public void plandOfensivo(){
		p1.golpea();
		p2.golpea();
		p1.golpea();
		p2.golpea();
	}
}
