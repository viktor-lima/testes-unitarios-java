package br.ce.vkl.servicos;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Date;

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

	@Test
	public void testLocacao() throws Exception {
		// scenery
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 2, 5.0);

		// action
		Locacao locacao = service.alugarFilme(usuario, filme);

		// verification
		error.checkThat(locacao.getValor(), is(5.0));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)),
				is(true));
	}

	@Test(expected = MoveWithoutStockException.class)
	public void testMovieWithoutStock() throws Exception {
		// scenery
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);

		// action
		service.alugarFilme(usuario, filme);
	}

	@Test
	public void User_isEmpty() throws MoveWithoutStockException {
		// scenery
		LocacaoService service = new LocacaoService();
		Filme filme = new Filme("Filme 1", 2, 5.0);

		try {
			service.alugarFilme(null, filme);
			fail();
		} catch (RentException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		}

	}

	@Test
	public void Movie_isEmpty() throws MoveWithoutStockException, RentException {
		// scenery
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");

		exception.expect(RentException.class);
		exception.expectMessage("Filme vazio");
		
		service.alugarFilme(usuario, null);

	}

}
