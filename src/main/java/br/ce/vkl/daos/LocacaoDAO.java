package br.ce.vkl.daos;

import java.util.List;

import br.ce.vkl.entidades.Locacao;

public interface LocacaoDAO {
	
	void save(Locacao locacao);

	List<Locacao> getPedingLeases();
	
}
