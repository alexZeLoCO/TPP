public class DigimonAdapted extends Pokemon {

    Digimon adapted;

    public DigimonAdapted(Digimon adapted) {
        this.adapted = adapted;
    }

    @Override
    public void golpea() {
        this.adapted.hit();
    }

    @Override
    public void retirate() {
        this.adapted.run();
    }

    @Override
    public void defender() {
        this.adapted.block();
    }

}
