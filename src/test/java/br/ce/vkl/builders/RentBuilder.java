package br.ce.vkl.builders;

import java.util.Arrays;
import java.util.Date;

import br.ce.vkl.entidades.Filme;
import br.ce.vkl.entidades.Locacao;
import br.ce.vkl.entidades.Usuario;
import br.ce.vkl.utils.DataUtils;

public class RentBuilder {

	private Locacao elemento;
	private RentBuilder(){}

	public static RentBuilder umLocacao() {
		RentBuilder builder = new RentBuilder();
		inicializarDadosPadroes(builder);
		return builder;
	}

	public static void inicializarDadosPadroes(RentBuilder builder) {
		builder.elemento = new Locacao();
		Locacao elemento = builder.elemento;

		
		elemento.setUsuario(UserBuilder.oneUser().now());
		elemento.setFilme(Arrays.asList(MovieBuilder.oneMovie().now()));
		elemento.setDataLocacao(new Date());
		elemento.setDataRetorno(DataUtils.obterDataComDiferencaDias(1));
		elemento.setValor(4.0);
	}

	public RentBuilder comUsuario(Usuario param) {
		elemento.setUsuario(param);
		return this;
	}

	public RentBuilder comListaFilme(Filme... params) {
		elemento.setFilme(Arrays.asList(params));
		return this;
	}

	public RentBuilder comDataLocacao(Date param) {
		elemento.setDataLocacao(param);
		return this;
	}

	public RentBuilder comDataRetorno(Date param) {
		elemento.setDataRetorno(param);
		return this;
	}

	public RentBuilder comValor(Double param) {
		elemento.setValor(param);
		return this;
	}

	public Locacao agora() {
		return elemento;
	}
	
	
}
