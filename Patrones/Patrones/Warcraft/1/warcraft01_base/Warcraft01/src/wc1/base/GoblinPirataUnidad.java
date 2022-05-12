package wc1.base;

public class GoblinPirataUnidad extends Pirata {

    private GoblinPirata gp;

    public GoblinPirataUnidad() {
        this.gp = new GoblinPirata();
    }

    @Override
    public void attack() {
        this.gp.abordar();
    }

    @Override
    public void defend() {
        this.gp.esquivar();
    }

    @Override
    public void move() {
        this.gp.navega();
    }

}
