package cliente;

import java.io.IOException;

import lib.ChannelException;
import lib.Menu;

public class MenuBarcos {

	private static Menu m = new Menu("\nMenú de Hundir la Flota", "Seleccionar una");
	
	private static void test () {
		System.out.println("Test!");
	}

	private static void main (String[] args) {
		m.add("test", () -> test());
		
		try {
			do {
				
			} while (m.runSelection());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ChannelException e) {
			e.printStackTrace();
		}
		
		Object obj = m.getObject();
		while (obj != null) {
			System.out.println(obj.toString());
			obj = m.getObject();
		}
	}
}
