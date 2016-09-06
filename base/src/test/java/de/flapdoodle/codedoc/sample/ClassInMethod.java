package de.flapdoodle.codedoc.sample;

public class ClassInMethod {

	public void someMethod() {
		class Embedded {
			
			public Embedded(String foo) {
				
			}
			
			public void methodInEmbedded() {
				
			}
		};
	}
	
	class Anon {

		public Anon(boolean bar) {
			
		}
		
		public void methodInAnon(int number) {
			
		}
	}
}
