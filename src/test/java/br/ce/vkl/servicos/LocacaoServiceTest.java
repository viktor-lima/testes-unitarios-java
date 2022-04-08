package br.ce.vkl.servicos;

import static br.ce.vkl.builders.RentBuilder.umLocacao;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import br.ce.vkl.builders.MovieBuilder;
import br.ce.vkl.builders.RentBuilder;
import br.ce.vkl.builders.UserBuilder;
import br.ce.vkl.daos.LocacaoDAO;
import br.ce.vkl.entidades.Filme;
import br.ce.vkl.entidades.Locacao;
import br.ce.vkl.entidades.Usuario;
import br.ce.vkl.exceptions.MoveWithoutStockException;
import br.ce.vkl.exceptions.RentException;
import br.ce.vkl.exceptions.SPCService;
import br.ce.vkl.matchers.MatcherOwn;
import br.ce.vkl.utils.DataUtils;
import buildermaster.BuilderMaster;

public class LocacaoServiceTest {

	@Rule
	public ErrorCollector error = new ErrorCollector();
	@Rule
	public ExpectedException exception = ExpectedException.none();

	private LocacaoService service;
	private LocacaoDAO dao;
	private SPCService spc;
	private EmailService emailService;

	@Before
	public void setup() {
		service = new LocacaoService();
		dao = Mockito.mock(LocacaoDAO.class);
		service.setLocacaoDAO(dao);
		spc = Mockito.mock(SPCService.class);
		service.setSPCService(spc);
		emailService = Mockito.mock(EmailService.class);
		service.setEmailService(emailService);
	}

	@Test
	public void MustRentMovie() throws Exception {
		
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// scenery
		
		Usuario user = UserBuilder.oneUser().now();
		List<Filme> filmes = Arrays.asList(MovieBuilder.oneMovie().withValue(5.0).now());

		// action
		Locacao locacao = service.rentMovie(user, filmes);

		// verification
		error.checkThat(locacao.getValor(), is(5.0));
		error.checkThat(locacao.getDataLocacao(), MatcherOwn.isToday());
		error.checkThat(locacao.getDataRetorno(), MatcherOwn.isTodayWithDifferenceOfDay(1));
	}

	@Test(expected = MoveWithoutStockException.class)
	public void mustHappenExceptionToTheRentMovie_withoutStock() throws Exception {
		// scenery

		Usuario user = UserBuilder.oneUser().now();
		List<Filme> filmes = Arrays.asList(MovieBuilder.oneMoviewithoutStock().now());

		// action
		service.rentMovie(user, filmes);
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

		Usuario user = UserBuilder.oneUser().now();

		exception.expect(RentException.class);
		exception.expectMessage("Filme vazio");

		service.rentMovie(user, null);
	}
	
	@Test
	public void mustGiveBackMovieInMondayToTheRentInSunday() throws MoveWithoutStockException, RentException {
		
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		Usuario user = UserBuilder.oneUser().now();
		List<Filme> filmes = Arrays.asList(MovieBuilder.oneMovie().now());
		
		Locacao returnLocacao = service.rentMovie(user, filmes);
		

		assertThat(returnLocacao.getDataRetorno(), MatcherOwn.fallsIn(Calendar.MONDAY));
		
	}
	
	@Test
	public void mustNotRentMovieForNegativadedSPC() throws MoveWithoutStockException  {
		Usuario user = UserBuilder.oneUser().now();
		List<Filme> filmes = Arrays.asList(MovieBuilder.oneMovie().now());
		
		Mockito.when(spc.isNegative(user)).thenReturn(true);
		
		
		try {
			service.rentMovie(user, filmes);
			Assert.fail();
		} catch (RentException e) {
			Assert.assertThat(e.getMessage(), is("Usuário Negativado"));
		}	
		
		Mockito.verify(spc).isNegative(user);
	}

	@Test
	public void mustSendEmailForLateRentals() {
		Usuario user = UserBuilder.oneUser().now();
		Usuario user2 = UserBuilder.oneUser().withName("Usuario em dia").now();
		Usuario user3 = UserBuilder.oneUser().withName("Outro atrasado").now();
		List<Locacao> locacoes = Arrays.asList(
				umLocacao().atrasado().comUsuario(user).agora(),
				umLocacao().comUsuario(user2).agora(),
				umLocacao().atrasado().comUsuario(user3).agora()
				);
		Mockito.when(dao.getPedingLeases()).thenReturn(locacoes);
		
		service.notifyDelays();
		
		Mockito.verify(emailService).notifyDelay(user);
		Mockito.verify(emailService).notifyDelay(user3);
		Mockito.verify(emailService, Mockito.never()).notifyDelay(user2);
	}

	
}












