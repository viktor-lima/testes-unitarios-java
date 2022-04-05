package br.ce.vkl.matchers;

public class MatcherOwn {

	public static DayWeekMatcher fallsIn(Integer dayWeek) {
		return new DayWeekMatcher(dayWeek);
	}

	public static DateDifferenceDaysMatchers isTodayWithDifferenceOfDay(Integer qtdDays) {
		return new DateDifferenceDaysMatchers(qtdDays);
	}

	public static DateDifferenceDaysMatchers isToday() {
		return new DateDifferenceDaysMatchers(0);
	}

}
