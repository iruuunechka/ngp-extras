package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint;

/**
 * <p>
 * A class which provides with an example of setting the switch point 
 * of the {@link IntegerFunction}.
 * </p><p>
 * Switch point is a point, which separates arguments 
 * mainly corresponding to the zero differences of the function 
 * from the arguments corresponding to the non-zero differences.
 * </p> 
 * @author Arina Buzdalova
 */
public class SwitchPointExample {
	
	/**
	 * Example of choosing switch point of the {@link IntegerFunction}
	 * @param args aren't used
	 */
	public static void main(String[] args) {
		int lowerDomainBound = 0;
		int upperDomainBound = 400;
		int divider = 10;
		
		IntegerFunction intConvex = Functions.intDownConvex(lowerDomainBound, upperDomainBound);
		IntegerFunction intUpConvex = Functions.intUpConvex(lowerDomainBound, upperDomainBound);
		IntegerFunction intDivX = Functions.intDivX(divider, lowerDomainBound, upperDomainBound);
		
		int upcTolerance = 10;
		int expectedSwitch = 200;
		int upcSwitchPoint = intUpConvex.changeSwitchPoint(expectedSwitch, upcTolerance, 0, 10000);
		
		int cTolerance = 2;
		int cSwitchPoint = intConvex.changeSwitchPoint(upcSwitchPoint, cTolerance, 0, 10);
		
		System.out.println("Downwards convex function switch point: " + cSwitchPoint);
		System.out.println("Upwards convex function switch point: " + upcSwitchPoint);
		
		System.out.println("x" + "  delta(" + intUpConvex + ")  delta(" + intConvex + ")   delta(" + intDivX +")");
		
		for (int x = lowerDomainBound; x < upperDomainBound; x++) {
			System.out.println(
					String.format("%3d", x) + " " + 
					String.format("  %6.1f", intUpConvex.deltaRatio(x, 1.0)) +  					
					String.format("  %4.1f", intConvex.deltaRatio(x, 1.0)) + 
					String.format("  %4.1f", intDivX.deltaRatio(x, 1.0)));
		}
	}
}
