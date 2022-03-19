package wc1.base;

public class Main {

	public static void creaYLanzaPirata(int tipo) {
		try {
			Pirata p = Pirata.factory(tipo);
			p.move();
			p.move();
			p.attack();
			p.defend();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void m1() {
		Dragon d;
		d = DragonOP.getInstance();
		d.attack();
	}

	public static void m2() {
		Dragon d;
		d = DragonOP.getInstance();
		d.move();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Dragon[] dragons = { new Dragon(), new Dragon(new DragonHielo()), new Dragon(new DragonAcido()) };

		for (Dragon d : dragons) {
			d.attack();
		}

	}

}
