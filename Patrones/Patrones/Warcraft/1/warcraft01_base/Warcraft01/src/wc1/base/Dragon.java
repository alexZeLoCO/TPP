package wc1.base;

public class Dragon extends Unidad {

    private DragonLegacy dragon;

    public Dragon() {
        this.dragon = new DragonLegacy();
    }

    public Dragon(DragonLegacy d) {
        this.dragon = d;
    }

    @Override
    public void attack() {
        this.dragon.fly();
        this.dragon.fire();
        this.dragon.land();
    }

    @Override
    public void defend() {
        this.attack();
    }

    @Override
    public void move() {
        this.dragon.fly();
        this.dragon.land();
    }

}
