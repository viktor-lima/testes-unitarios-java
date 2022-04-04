package br.ce.vkl.servicos;

import br.ce.vkl.exceptions.CanNotDividingByZeroException;

public class Calculator {
	
	public int sum(int a, int b) {
		return a+b;
	}

	public int sub(int a, int b) {
		return a-b;
	}

	public int split(int a, int b) throws CanNotDividingByZeroException {
		if(b == 0) 
			throw new CanNotDividingByZeroException();
		return a/b;
	}
}
