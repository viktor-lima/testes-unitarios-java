package br.ce.vkl.servicos;

import static br.ce.vkl.builders.RentBuilder.umLocacao;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

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
import br.ce.vkl.runners.ParallelRunner;
import br.ce.vkl.utils.DataUtils;


@RunWith(ParallelRunner.class)
public class LocacaoServiceTest {

	@Rule
	public ErrorCollector error = new ErrorCollector();
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@InjectMocks @Spy
	private LocacaoService service;
	@Mock
	private LocacaoDAO dao;
	@Mock
	private SPCService spc;
	@Mock
	private EmailService emailService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void MustRentMovie() throws Exception {

		// scenery

		Usuario user = UserBuilder.oneUser().now();
		List<Filme> filmes = Arrays.asList(MovieBuilder.oneMovie().withValue(5.0).now());

		Mockito.doReturn(DataUtils.obterData(8, 04, 2020)).when(service).getDate();
		
		// action
		Locacao locacao = service.rentMovie(user, filmes);

		// verification
		error.checkThat(locacao.getValor(), is(CoreMatchers.equalTo( 5.0)));
//		error.checkThat(locacao.getDataLocacao(), MatcherOwn.isToday());
//		error.checkThat(locacao.getDataRetorno(), MatcherOwn.isTodayWithDifferenceOfDay(1));
		
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(8, 04, 2020)),is(false));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(10, 04, 2020)),is(false));
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
	public void mustGiveBackMovieInMondayToTheRentInSunday() throws Exception {

		
		Usuario user = UserBuilder.oneUser().now();
		List<Filme> filmes = Arrays.asList(MovieBuilder.oneMovie().now());
		
		Mockito.doReturn(DataUtils.obterData(11, 04, 2020)).when(service).getDate();
		
		Locacao returnLocacao = service.rentMovie(user, filmes);

		assertThat(returnLocacao.getDataRetorno(), MatcherOwn.fallsIn(Calendar.MONDAY));

	}

	@Test
	public void mustNotRentMovieForNegativadedSPC() throws Exception  {
		Usuario user = UserBuilder.oneUser().now();
		List<Filme> filmes = Arrays.asList(MovieBuilder.oneMovie().now());

		Mockito.when(spc.isNegative(Mockito.any(Usuario.class))).thenReturn(true);

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
				umLocacao().comUsuario(user2).agora(), umLocacao().atrasado().comUsuario(user3).agora(),
				umLocacao().atrasado().comUsuario(user3).agora());
		Mockito.when(dao.getPedingLeases()).thenReturn(locacoes);

		service.notifyDelays();

		Mockito.verify(emailService, Mockito.times(3)).notifyDelay(Mockito.any(Usuario.class));
		Mockito.verify(emailService).notifyDelay(user);
		Mockito.verify(emailService, Mockito.atLeastOnce()).notifyDelay(user3);
		Mockito.verify(emailService, Mockito.never()).notifyDelay(user2);
	}

	@Test
	public void shouldHandleErrorCPS() throws Exception {
		//c
		Usuario user = UserBuilder.oneUser().now();
		List<Filme> filmes = Arrays.asList(MovieBuilder.oneMovie().now());
		
		Mockito.when(spc.isNegative(user)).thenThrow(new Exception("Falha catastrófica"));
		//v
		exception.expect(RentException.class);
		exception.expectMessage("Problemas com SPC, tente novamente");	
		//a
		service.rentMovie(user, filmes);
	}
	
	@Test
	public void mustReturnOneRent() {
		//c
			Locacao locacao = RentBuilder.umLocacao().agora();
		
		//a
			service.ExtendRent(locacao, 3);
		//v
			ArgumentCaptor<Locacao> argCaptor = ArgumentCaptor.forClass(Locacao.class);
			Mockito.verify(dao).save(argCaptor.capture());
			Locacao locacaoReturned = argCaptor.getValue();
			
			error.checkThat(locacaoReturned.getValor(), is(12.0));
			error.checkThat(locacaoReturned.getDataLocacao(), is(MatcherOwn.isToday()));
			error.checkThat(locacaoReturned.getDataRetorno(), is(MatcherOwn.isTodayWithDifferenceOfDay(3)));
	}

}
