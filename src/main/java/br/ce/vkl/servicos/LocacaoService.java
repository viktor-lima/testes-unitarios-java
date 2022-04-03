package br.ce.vkl.servicos;

import static br.ce.vkl.utils.DataUtils.adicionarDias;

import java.util.Date;

import br.ce.vkl.entidades.Filme;
import br.ce.vkl.entidades.Locacao;
import br.ce.vkl.entidades.Usuario;
import br.ce.vkl.exceptions.MoveWithoutStockException;
import br.ce.vkl.exceptions.RentException;

public class LocacaoService {
	
	public Locacao alugarFilme(Usuario usuario, Filme filme) throws MoveWithoutStockException, RentException  {
		
		if(usuario == null)
			throw new RentException("Usuario vazio");
		
		if(filme == null)
			throw new RentException("Filme vazio");
		
		if(filme.getEstoque() == 0) 
			throw new MoveWithoutStockException();
		
		
		Locacao locacao = new Locacao();
		locacao.setFilme(filme);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		locacao.setValor(filme.getPrecoLocacao());

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		//TODO adicionar método para salvar
		
		return locacao;
	}
	
	
}