package br.ce.vkl.builders;

import br.ce.vkl.entidades.Filme;

public class MovieBuilder {

	private Filme filme;
	
	private MovieBuilder() {}
	
	public static MovieBuilder oneMovie() {
		MovieBuilder builder = new MovieBuilder();
		builder.filme = new Filme();
		builder.filme.setEstoque(2);
		builder.filme.setNome("Filme 1");
		builder.filme.setPrecoLocacao(4.0);
		return builder;
	}
	
	public static MovieBuilder oneMoviewithoutStock() {
		MovieBuilder builder = new MovieBuilder();
		builder.filme = new Filme();
		builder.filme.setEstoque(0);
		builder.filme.setNome("Filme 1");
		builder.filme.setPrecoLocacao(4.0);
		return builder;
	}
	
	
	public MovieBuilder withoutStock() {
		filme.setEstoque(0);
		return this;
	}
	
	public MovieBuilder withValue(Double value) {
		filme.setPrecoLocacao(value);
		return this;
	}
	
	public Filme now() {
		return filme;
	}
	
}
