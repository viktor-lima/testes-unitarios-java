package br.ce.vkl.servicos;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.vkl.builders.MovieBuilder;
import br.ce.vkl.builders.UserBuilder;
import br.ce.vkl.entidades.Filme;
import br.ce.vkl.entidades.Locacao;
import br.ce.vkl.entidades.Usuario;
import br.ce.vkl.exceptions.MoveWithoutStockException;
import br.ce.vkl.exceptions.RentException;
import br.ce.vkl.matchers.MatcherOwn;
import br.ce.vkl.utils.DataUtils;

public class LocacaoServiceTest {

	@Rule
	public ErrorCollector error = new ErrorCollector();
	@Rule
	public ExpectedException exception = ExpectedException.none();

	private LocacaoService service;

	@Before
	public void setup() {
		service = new LocacaoService();
	}

	@Test
	public void MustRentMovie() throws Exception {
		
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// scenery
		
		Usuario usuario = UserBuilder.oneUser().now();
		List<Filme> filmes = Arrays.asList(MovieBuilder.oneMovie().withValue(5.0).now());

		// action
		Locacao locacao = service.rentMovie(usuario, filmes);

		// verification
		error.checkThat(locacao.getValor(), is(5.0));
		error.checkThat(locacao.getDataLocacao(), MatcherOwn.isToday());
		error.checkThat(locacao.getDataRetorno(), MatcherOwn.isTodayWithDifferenceOfDay(1));
	}

	@Test(expected = MoveWithoutStockException.class)
	public void mustHappenExceptionToTheRentMovie_withoutStock() throws Exception {
		// scenery

		Usuario usuario = UserBuilder.oneUser().now();
		List<Filme> filmes = Arrays.asList(MovieBuilder.oneMoviewithoutStock().now());

		// action
		service.rentMovie(usuario, filmes);
	}

	@Test
	public void CanNotRentMovie_WithoutUser() throws MoveWithoutStockException {
		// scenery

		List<Filme> filmes = Arrays.asList(MovieBuilder.oneMovie().now());

		try {
			service.rentMovie(null, filmes);
			fail();
		} catch (RentException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		}

	}

	@Test
	public void CanNotRentMovie_withoutMovie() throws MoveWithoutStockException, RentException {
		// scenery

		Usuario usuario = UserBuilder.oneUser().now();

		exception.expect(RentException.class);
		exception.expectMessage("Filme vazio");

		service.rentMovie(usuario, null);
	}
	
	@Test
	public void mustGiveBackMovieInMondayToTheRentInSunday() throws MoveWithoutStockException, RentException {
		
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		Usuario usuario = UserBuilder.oneUser().now();
		List<Filme> filmes = Arrays.asList(MovieBuilder.oneMovie().now());
		
		Locacao returnLocacao = service.rentMovie(usuario, filmes);
		

		assertThat(returnLocacao.getDataRetorno(), MatcherOwn.fallsIn(Calendar.MONDAY));
		
	}

}
