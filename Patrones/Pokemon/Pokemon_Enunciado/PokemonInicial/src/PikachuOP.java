public class PikachuOP extends Pikachu {

    private static PikachuOP pikachu;

    private PikachuOP() {

    }

    public static PikachuOP getPikachu() {
        if (pikachu == null) {
            pikachu = new PikachuOP();
        }
        return pikachu;
    }
}
