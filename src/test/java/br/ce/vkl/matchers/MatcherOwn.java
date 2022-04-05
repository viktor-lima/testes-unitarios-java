package br.ce.vkl.matchers;

public class MatcherOwn {
	
	public static DayWeekMatcher fallsIn(Integer dayWeek) {
		return new DayWeekMatcher(dayWeek);
	}

}
