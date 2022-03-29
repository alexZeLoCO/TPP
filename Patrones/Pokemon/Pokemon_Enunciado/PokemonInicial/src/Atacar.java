public class Atacar implements iCommand {

    Pokemon p;

    public Atacar(Pokemon p) {
        this.p = p;
    }

    @Override
    public void ejecutar() {
        this.p.golpea();
    }

}
