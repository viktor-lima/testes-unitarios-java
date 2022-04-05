package br.ce.vkl.matchers;

import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.vkl.utils.DataUtils;

public class DateDifferenceDaysMatchers extends TypeSafeMatcher<Date>{
	
	private Integer qtdDays;

	public DateDifferenceDaysMatchers(Integer qtdDays) {
		this.qtdDays = qtdDays;
	}
	
	public void describeTo(Description arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean matchesSafely(Date date) {
		return DataUtils.isMesmaData(date, DataUtils.obterDataComDiferencaDias(qtdDays));
	}

}
