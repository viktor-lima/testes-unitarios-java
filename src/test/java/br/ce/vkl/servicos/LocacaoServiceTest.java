package br.ce.vkl.servicos;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
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
	public void MustPay75PercentInMovie() throws MoveWithoutStockException, RentException {
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
				new Filme("Filme 3", 2, 4.0));

		Locacao result = service.rentMovie(usuario, filmes);

		assertThat(result.getValor(), is(11.0));

	}

	@Test
	public void MustPay50PercentInMovie() throws MoveWithoutStockException, RentException {
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
				new Filme("Filme 3", 2, 4.0), new Filme("Filme 3", 2, 4.0));

		Locacao result = service.rentMovie(usuario, filmes);

		assertThat(result.getValor(), is(13.0));

	}

	@Test
	public void MustPay25PercentInMovie() throws MoveWithoutStockException, RentException {
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
				new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0), new Filme("Filme 5", 2, 4.0)

		);

		Locacao result = service.rentMovie(usuario, filmes);

		assertThat(result.getValor(), is(14.0));

	}

	@Test
	public void MustPay100PercentInMovie() throws MoveWithoutStockException, RentException {
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
				new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0), new Filme("Filme 5", 2, 4.0),
				new Filme("Filme 6", 2, 4.0)

		);

		Locacao result = service.rentMovie(usuario, filmes);

		assertThat(result.getValor(), is(14.0));

	}

}
