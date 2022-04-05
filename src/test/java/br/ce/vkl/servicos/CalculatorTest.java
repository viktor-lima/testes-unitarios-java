package br.ce.vkl.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.ce.vkl.exceptions.CanNotDividingByZeroException;

public class CalculatorTest {
	
	private Calculator calc;
	
	@Before
	public void setup() {
		calc = new Calculator();
	}

	@Test
	public void MustSumTwoValues() {
		//scenery
			int a = 5;
			int b = 3;
			
		//action
			int resultSum = calc.sum(a,b);
		//verification
			Assert.assertEquals(8, resultSum);
	}
	
	@Test
	public void mustSubtractTwoValues() {
		//scenery
		int a = 8;
		int b = 5;
		
		//action
		int reusltSub = calc.sub(a,b);
		//verification
		Assert.assertEquals(3, reusltSub);
	}
	
	@Test
	public void mustSplitTwoValues() throws CanNotDividingByZeroException {
		//scenery
		int a = 6;
		int b = 3;
	
		//action
		int reusltSub = calc.split(a,b);
		//verification
		Assert.assertEquals(2, reusltSub);
	}
	
	@Test(expected = CanNotDividingByZeroException.class)
	public void mustHappenExceptionWhenDividingByZero() throws CanNotDividingByZeroException {
		//scenery
		int a = 6;
		int b = 0;
		
		//action
		calc.split(a,b);
	}
	
	
}
