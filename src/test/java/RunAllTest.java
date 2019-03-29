import org.junit.Test;

public class RunAllTest {

	@Test
	public void test() {
		try {
			RunAllExamples.main( new String[] {System.getProperty("java.io.tmpdir") + "/pd4ml-examples/"}  );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
