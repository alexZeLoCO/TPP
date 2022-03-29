package wc1.base;

public class NoMuertosFactory implements EntityFactory {

    @Override
    public Unidad newInfanteria() {
        return new Esqueleto();
    }

    @Override
    public Unidad newArquero() {
        return new LanceroZombi();
    }

    @Override
    public Unidad newJinete() {
        return new JineteSinCabeza();
    }

    @Override
    public Unidad newMA() {
        return new CannonDeLaPlaga();
    }

    @Override
    public Unidad newMago() {
        return new Nigromante();
    }

}
