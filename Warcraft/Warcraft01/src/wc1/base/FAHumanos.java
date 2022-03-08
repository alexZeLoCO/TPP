package wc1.base;

public class FAHumanos implements FAbstracta {

    @Override
    public Unidad creaInfanteria() {
        return new SoldadoRaso();
    }

    @Override
    public Unidad creaArquero() {
        return new Arquero();
    }

    @Override
    public Unidad creaJinete() {
        return new Caballero();
    }

    @Override
    public Unidad creaMA() {
        return new Catapulta();
    }

}
