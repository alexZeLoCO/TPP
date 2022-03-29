package wc1.base;

public class HumanosFactory implements EntityFactory {

    @Override
    public Unidad newInfanteria() {
        return new SoldadoRaso();
    }

    @Override
    public Unidad newArquero() {
        return new Arquero();
    }

    @Override
    public Unidad newJinete() {
        return new Caballero();
    }

    @Override
    public Unidad newMA() {
        return new Catapulta();
    }

    @Override
    public Unidad newMago() {
        return new Conjurador();
    }

}
