package wc1.base;

public class FAOrcos implements FAbstracta {

    @Override
    public Unidad creaInfanteria() {
        return new Grunt();
    }

    @Override
    public Unidad creaArquero() {
        return new LanzadorDeJabalinas();
    }

    @Override
    public Unidad creaJinete() {
        return new Incursor();
    }

    @Override
    public Unidad creaMA() {
        return new Catapulta();
    }

}
