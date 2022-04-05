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

import br.ce.vkl.entidades.Filme;
import br.ce.vkl.entidades.Locacao;
import br.ce.vkl.entidades.Usuario;
import br.ce.vkl.exceptions.MoveWithoutStockException;
import br.ce.vkl.exceptions.RentException;
import br.ce.vkl.utils.DataUtils;
import junit.framework.Assert;

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
		
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));

		// action
		Locacao locacao = service.rentMovie(usuario, filmes);

		// verification
		error.checkThat(locacao.getValor(), is(5.0));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)),
				is(true));
	}

	@Test(expected = MoveWithoutStockException.class)
	public void mustHappenExceptionToTheRentMovie_withoutStock() throws Exception {
		// scenery

		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0));

		// action
		service.rentMovie(usuario, filmes);
	}

	@Test
	public void CanNotRentMovie_WithoutUser() throws MoveWithoutStockException {
		// scenery

		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));

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

		Usuario usuario = new Usuario("Usuario 1");

		exception.expect(RentException.class);
		exception.expectMessage("Filme vazio");

		service.rentMovie(usuario, null);
	}
	
	@Test
	public void mustGiveBackMovieInMondayToTheRentInSunday() throws MoveWithoutStockException, RentException {
		
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));
		
		Locacao returnLocacao = service.rentMovie(usuario, filmes);
		
		boolean isMonday = DataUtils.verificarDiaSemana(returnLocacao.getDataRetorno(), Calendar.MONDAY);
		Assert.assertTrue(isMonday);
		
	}

}
