package com.jtalics.pairstrader;

public interface Outputable {
	void println(String string);

	void print(String string);

	void println();

	void clear();
	
	public class Default implements Outputable {

		@Override
		public void println(String string) {
			System.out.println(string);
		}

		@Override
		public void print(String string) {
			System.out.println(string);
		}

		@Override
		public void println() {
			System.out.println();
		}

		@Override
		public void clear() {
			// ignore
		}
		
	}
}
