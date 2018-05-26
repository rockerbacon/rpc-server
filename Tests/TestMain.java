package Tests;

public class TestMain {

	public static void test (String tag, Object obtained, Object expected) {
		System.out.print(tag+": ");
		if (obtained.equals(expected)) {
			System.out.println("PASSED");
		} else {
			System.out.println("FAILED: obtained = " + obtained.toString() + " ; expected = " + expected.toString());
		}	
	}

	public static void main (String args[]) {
		TestServer ts = new TestServer(30007);
		TestClient tc = new TestClient(30007);
		TestClient.Test1Return r = new TestClient.Test1Return();
		int	a = 5,
			b = 6;
			
		String s = "uppercase", sr;
		
		r = tc.test1(a, b);
		TestMain.test("Test 1 sum", r.sum, a+b);
		TestMain.test("Test 1 mult", r.mult, a*b);
		
		sr = tc.test2(s);
		TestMain.test("Test 2", sr, s.toUpperCase());
		
		ts.close();
		tc.close();
	}	
	
}
