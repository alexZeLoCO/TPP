package wc1.base;

public class OrcosFactory implements EntityFactory {

    @Override
    public Unidad newInfanteria() {
        return new Grunt();
    }

    @Override
    public Unidad newArquero() {
        return new LanzadorDeJabalinas();
    }

    @Override
    public Unidad newJinete() {
        return new Incursor();
    }

    @Override
    public Unidad newMA() {
        return new Catapulta();
    }

    @Override
    public Unidad newMago() {
        return new Hechicero();
    }
}
