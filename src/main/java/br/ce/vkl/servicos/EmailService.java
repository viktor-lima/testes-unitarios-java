package br.ce.vkl.servicos;

import br.ce.vkl.entidades.Usuario;

public interface EmailService {
	
	void notifyDelay(Usuario user);
	
}
