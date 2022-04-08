package br.ce.vkl.matchers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.vkl.utils.DataUtils;

public class DateWithDifferenceDaysMatchers extends TypeSafeMatcher<Date>{
	
	private Integer qtdDays;

	public DateWithDifferenceDaysMatchers(Integer qtdDays) {
		this.qtdDays = qtdDays;
	}
	
	public void describeTo(Description desc) {
		Date expetedDate = DataUtils.obterDataComDiferencaDias(qtdDays);
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		desc.appendText(format.format(expetedDate));
	}

	@Override
	protected boolean matchesSafely(Date date) {
		return DataUtils.isMesmaData(date, DataUtils.obterDataComDiferencaDias(qtdDays));
	}

}
