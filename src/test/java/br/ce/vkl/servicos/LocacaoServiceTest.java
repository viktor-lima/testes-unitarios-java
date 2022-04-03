package br.ce.vkl.servicos;


import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import br.ce.vkl.entidades.Filme;
import br.ce.vkl.entidades.Locacao;
import br.ce.vkl.entidades.Usuario;
import br.ce.vkl.servicos.LocacaoService;
import br.ce.vkl.utils.DataUtils;

public class LocacaoServiceTest {
	@Test
	public  void test() {
		//scenery
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 2, 5.0);
		
		//action
		Locacao locacao = service.alugarFilme(usuario, filme);
		
		//verification
		assertTrue(locacao.getValor() == 5.0);
		assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
	}
}
