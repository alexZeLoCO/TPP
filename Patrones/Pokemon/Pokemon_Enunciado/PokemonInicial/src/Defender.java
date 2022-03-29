public class Defender implements iCommand {

    Pokemon p;

    public Defender(Pokemon p) {
        this.p = p;
    }

    @Override
    public void ejecutar() {
        this.p.defender();
    }

}
