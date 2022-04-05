package br.ce.vkl.matchers;

public class MatcherOwn {

	public static DayWeekMatcher fallsIn(Integer dayWeek) {
		return new DayWeekMatcher(dayWeek);
	}

	public static DateWithDifferenceDaysMatchers isTodayWithDifferenceOfDay(Integer qtdDays) {
		return new DateWithDifferenceDaysMatchers(qtdDays);
	}

	public static DateWithDifferenceDaysMatchers isToday() {
		return new DateWithDifferenceDaysMatchers(0);
	}

}
