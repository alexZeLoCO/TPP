package cliente;

import java.io.IOException;

import lib.ChannelException;
import lib.Menu;

public class EjemploMenu {

	private static Menu m = new Menu("\nMenú de prueba", "Opción? ");

	private static void prueba1() {
		System.out.print("Valor entero: ");
		System.out.printf("Ejecutando prueba1(): %d\n", m.input().nextInt());
	}
	
	private static void prueba2() {
		System.out.println("Ejecutando prueba2()");
	}
	
	private static void prueba3() {
		System.out.println("Ejecutando prueba3()");
	}
	
	public static void main(String[] args) {

		m.add("Prueba1", () -> prueba1());
		m.add("Prueba2", () -> prueba2());
		m.add("Prueba3", () -> prueba3());
		
		try {
			do {
				
			} while (m.runSelection());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ChannelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Menu mo = new Menu("\nMenú de objetos", "Opción? ");
		
		mo.add("Carácter alfabético", 'a');
		mo.add("Valor numérico", 20);
		mo.add("String", "Una cadena");
				
		Object obj = mo.getObject();
		while (obj != null) {
			System.out.println(obj.toString());
			obj = mo.getObject();
		}			
	}

}
