package br.ce.vkl.builders;

import br.ce.vkl.entidades.Usuario;

public class UserBuilder {

	private Usuario user;
	
	private UserBuilder() {}
	
	public static UserBuilder oneUser() {
		UserBuilder builder = new UserBuilder();
		builder.user = new Usuario();
		builder.user.setNome("Usuario 1");
		return builder;
	}
	
	public UserBuilder withName(String name) {
		user.setNome(name);
		return this;
	}
	
	public Usuario now() {
		return user;
	}
	
}
