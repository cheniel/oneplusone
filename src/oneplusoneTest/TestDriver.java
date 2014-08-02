package oneplusoneTest;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestDriver {
  public static void main(String[] args) {
    int failed = 0;

    Class<?>[] testClasses = {
    		OrganizationTest.class,
    		TeamTest.class,
    		PersonTest.class
    };
    
    for (Class<?> testClass : testClasses) {
      Result result = JUnitCore.runClasses(testClass);
      for (Failure failure : result.getFailures()) {
        System.out.println(failure.toString());
        failed++;
      }
    }
    
		System.out.println(new String(new char[80]).replace("\0", "-"));
    if (failed == 0) {
    	System.out.println("All tests passed.");
    } else {
    	System.out.println(failed + " tests failed.");
    }
  }
} 