package wc1.base;

public class IAPiratas {

    private PirataFactory factory;

    public IAPiratas(String pirata) {
        if (!pirata.equals("Goblin") && !pirata.equals("Corsario")) {
            throw new IllegalArgumentException("Tipo de pirata no definido");
        }
        switch (pirata) {
            case ("Goblin"):
                this.factory = new PirataFactory();
        }
    }
}
