package br.ce.vkl.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.vkl.servicos.CalculationRentValue;
import br.ce.vkl.servicos.CalculatorTest;
import br.ce.vkl.servicos.LocacaoServiceTest;

@RunWith(Suite.class)
@SuiteClasses({
	CalculatorTest.class,
	CalculationRentValue.class,
	LocacaoServiceTest.class
})
public class SuiteExecution {
	
}
