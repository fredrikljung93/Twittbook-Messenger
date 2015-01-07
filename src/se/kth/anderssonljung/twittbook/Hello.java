package se.kth.anderssonljung.twittbook;

public class Hello {

	public static void main(String[] args) {
		
		Test A=Test.getInstance();
		Test B=Test.getInstance();
		

	}

	private static class Test {
		static Test instance;
		
		public static Test getInstance(){
			if(instance==null){
				instance=new Test();
			}
			
			return instance;
		}
	}

}