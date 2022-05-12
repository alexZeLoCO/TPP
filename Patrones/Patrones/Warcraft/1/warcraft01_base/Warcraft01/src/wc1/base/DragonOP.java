package wc1.base;

public class DragonOP extends Dragon {

    private static Dragon dragon = null;

    private DragonOP() {

    }

    public static Dragon getInstance() {
        if (dragon == null) {
            dragon = new Dragon();
        }
        return dragon;
    }

}
