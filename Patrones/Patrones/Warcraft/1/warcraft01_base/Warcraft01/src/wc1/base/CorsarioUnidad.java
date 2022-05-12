package wc1.base;

public class CorsarioUnidad extends Unidad {

    private CorsarioIngles ci;

    public CorsarioUnidad() {
        this.ci = new CorsarioIngles();
    }

    @Override
    public void attack() {
        this.ci.shoot();
    }

    @Override
    public void defend() {
        this.ci.block();
    }

    @Override
    public void move() {
        this.ci.advance();
    }

}
