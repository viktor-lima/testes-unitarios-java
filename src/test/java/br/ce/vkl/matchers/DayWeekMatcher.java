package br.ce.vkl.matchers;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.vkl.utils.DataUtils;

public class DayWeekMatcher  extends TypeSafeMatcher<Date>{
	
	private Integer dayWeek;

	public DayWeekMatcher(Integer dayWeek) {
		this.dayWeek = dayWeek;
	}
	
	public void describeTo(Description desc) {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.DAY_OF_WEEK, this.dayWeek);
		String dateExtendo = date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt","BR"));
		desc.appendText(dateExtendo);
	}

	@Override
	protected boolean matchesSafely(Date date) {
		// TODO Auto-generated method stub
		return DataUtils.verificarDiaSemana(date, dayWeek);
	}

}
