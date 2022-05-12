package wc1.base;

public class Pirata extends Unidad {

    public static Pirata factory(int tipo) throws Exception {
        switch (tipo) {
            case 1:
                return new CorsarioUnidad();
            case 2:
                return new GoblinPirataUnidad();
            default:
                throw new Exception("Pirata no existente");
        }
    }
}
