package wc1.base;

public class IAComun {

    private EntityFactory eFactory;

    public IAComun(String entity) {
        if (!entity.equals("orcos") && !entity.equals("humanos") && !entity.equals("noMuertos")) {
            throw new IllegalArgumentException("Unidentified entities");
        }
        switch (entity) {
            case ("orcos"):
                this.eFactory = new OrcosFactory();
            case ("humanos"):
                this.eFactory = new HumanosFactory();
            case ("noMuertos"):
                this.eFactory = new NoMuertosFactory();
        }
    }

    /**
     * Generaci�n de un grupo de ataque de la I.A.
     * 
     * @return Un grupo de ataque
     */
    public Unidad[] creaGrupoDeAtaque() {
        // Array de Unidades Orcas
        Unidad[] grupoDeAtaque = new Unidad[13];

        // 4 x infanteria
        for (int x = 0; x < 4; x++)
            grupoDeAtaque[x] = this.eFactory.newInfanteria();
        // 3 x arqueros
        for (int x = 4; x < 7; x++)
            grupoDeAtaque[x] = this.eFactory.newArquero();
        // 2 x jinetes
        grupoDeAtaque[7] = this.eFactory.newJinete();
        grupoDeAtaque[8] = this.eFactory.newJinete();
        // 1 x m�quina de asedio
        grupoDeAtaque[9] = this.eFactory.newMA();

        for (int x = 10; x < 13; x++) {
            grupoDeAtaque[x] = this.eFactory.newMago();
        }

        return grupoDeAtaque;
    }

}
