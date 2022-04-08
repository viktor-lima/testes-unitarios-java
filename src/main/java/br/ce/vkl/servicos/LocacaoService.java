package br.ce.vkl.servicos;

import static br.ce.vkl.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.vkl.daos.LocacaoDAO;
import br.ce.vkl.entidades.Filme;
import br.ce.vkl.entidades.Locacao;
import br.ce.vkl.entidades.Usuario;
import br.ce.vkl.exceptions.MoveWithoutStockException;
import br.ce.vkl.exceptions.RentException;
import br.ce.vkl.exceptions.SPCService;
import br.ce.vkl.utils.DataUtils;

public class LocacaoService {
	
	private LocacaoDAO dao;
	private SPCService spcService;
	private EmailService emailService;

	public Locacao rentMovie(Usuario usuario, List<Filme> filmes) throws MoveWithoutStockException, RentException {

		if (usuario == null)
			throw new RentException("Usuario vazio");

		if (filmes == null)
			throw new RentException("Filme vazio");

		for (Filme filme : filmes) {
			if (filme.getEstoque() == 0)
				throw new MoveWithoutStockException();
		}
		boolean negative;
		try {
			negative =  spcService.isNegative(usuario);
		}
		catch(Exception e) {
			throw new RentException("Problemas com SPC, tente novamente");
		}
		if(negative)
			throw new RentException("Usuário Negativado");			

		Locacao locacao = new Locacao();
		locacao.setFilme(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		Double totalValue = 0d;
		for (int i = 0; i < filmes.size(); i++) {
			Filme filme = filmes.get(i);
			Double valueMovie = filme.getPrecoLocacao();
			switch (i) {
			case 2:
				valueMovie = valueMovie * 0.75;
				break;
			case 3:
				valueMovie = valueMovie * 0.50;
				break;
			case 4:
				valueMovie = valueMovie * 0.25;
				break;
			case 5:
				valueMovie = valueMovie * 0;
				break;
			}
			totalValue += valueMovie;
		}
		locacao.setValor(totalValue);

		// Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if(DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY))
			dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		dao.save(locacao);

		return locacao;
	}
	
	public void notifyDelays() {
		List<Locacao> locacoes = dao.getPedingLeases();
		for(Locacao locacao: locacoes) {
			if(locacao.getDataRetorno().before(new Date()))
				emailService.notifyDelay(locacao.getUsuario());
		}
	}
	
	public void ExtendRent(Locacao locacao, int days) {
		Locacao newLocacao = new Locacao();
		newLocacao.setUsuario(locacao.getUsuario());
		newLocacao.setFilme(locacao.getFilme());
		newLocacao.setDataLocacao(new Date());
		newLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(days));
		newLocacao.setValor(locacao.getValor() * days);
		dao.save(newLocacao);
	}


}