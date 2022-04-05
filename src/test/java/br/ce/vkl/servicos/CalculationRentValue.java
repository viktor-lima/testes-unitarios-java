package br.ce.vkl.servicos;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import br.ce.vkl.builders.MovieBuilder;
import br.ce.vkl.entidades.Filme;
import br.ce.vkl.entidades.Locacao;
import br.ce.vkl.entidades.Usuario;
import br.ce.vkl.exceptions.MoveWithoutStockException;
import br.ce.vkl.exceptions.RentException;

@RunWith(Parameterized.class)
public class CalculationRentValue {

	private LocacaoService service;

	@Parameter
	public List<Filme> filmes;
	@Parameter(value = 1)
	public Double rentValue;
	@Parameter(value = 2)
	public String scenery;

	@Before
	public void setup() {
		service = new LocacaoService();
	}

	private static Filme filme1 = MovieBuilder.oneMovie().now();
	private static Filme filme2 = MovieBuilder.oneMovie().now();
	private static Filme filme3 = MovieBuilder.oneMovie().now();
	private static Filme filme4 = MovieBuilder.oneMovie().now();
	private static Filme filme5 = MovieBuilder.oneMovie().now();
	private static Filme filme6 = MovieBuilder.oneMovie().now();

	@Parameters(name = "{2}")
	public static Collection<Object[]> getParameters() {
		return Arrays.asList(new Object[][] { 
				{ Arrays.asList(filme1, filme2, filme3), 11.0, "3 movies: 25%" },
				{ Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "4 movies: 50%"  },
				{ Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "5 movies: 75%"  },
				{ Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "6 movies: 100%"  } 
			});
	}

	@Test
	public void MustCalculationRentValueWithDiscount() throws MoveWithoutStockException, RentException {
		Usuario usuario = new Usuario("Usuario 1");

		Locacao result = service.rentMovie(usuario, filmes);

		assertThat(result.getValor(), is(rentValue));

	}

}
