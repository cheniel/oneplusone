/**
 * TestDriver.java
 * 
 * Runs unit tests for all of the oneplusone classes.
 * 
 * PairingDriver does not have a unit test. It is mostly a command line wrapper
 * for some of the outer functions of the program. So bugs from it's functions
 * are mostly based in appearance and are apparent from tests on the console.
 * 
 * Run unit tests from TestDriver.java
 */

package oneplusoneTest;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestDriver {
  public static void main(String[] args) {
    int failed = 0;

    // list of all the classes to test
    Class<?>[] testClasses = {
    		OrganizationTest.class,
    		TeamTest.class,
    		PersonTest.class,
    		PairingAssignmentTest.class,
    		WeightedCSPTest.class
    };
    
    // test each class
    for (Class<?> testClass : testClasses) {
      Result result = JUnitCore.runClasses(testClass);
      for (Failure failure : result.getFailures()) {
        System.out.println(failure.toString());
        failed++;
      }
    }
    
    // report if there were any errors
    if (failed == 0) {
    	System.out.println("All tests passed.");
    } else {
  		System.out.println(new String(new char[80]).replace("\0", "-"));
    	System.out.println(failed + " tests failed.");
    }
  }
} 