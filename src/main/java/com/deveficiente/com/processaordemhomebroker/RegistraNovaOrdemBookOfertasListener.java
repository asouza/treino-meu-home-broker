package com.deveficiente.com.processaordemhomebroker;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.deveficiente.com.processaordemhomebroker.compartilhado.Log5WBuilder;

import jakarta.transaction.Transactional;

@Component
public class RegistraNovaOrdemBookOfertasListener {

	private BookOfertasRepository bookOfertasRepository;
	private static final Logger log = LoggerFactory
	.getLogger(RegistraNovaOrdemBookOfertasListener.class);

	
    @JmsListener(destination = "insere-book-ofertas-limitada-ton", containerFactory = "myFactory")
	@Transactional
	public void receiveMessage(NovaOrdemLimitadaTonMessage ordem) {
		/*
		 * Carrego o book de ofertas para o ativo
		 * Registro a ordem
		 * Mando agora para processar a ordem seguindo o tipo e considerando a validade
		 */

		 Optional<BookOfertas> possivelBook = bookOfertasRepository.findByAtivo(ordem.getAtivo());
		 Assert.isTrue(possivelBook.isPresent(),"Não existe book de ofertas para o ativo "+ordem.getAtivo());

		Log5WBuilder
			.metodo()
			.oQueEstaAcontecendo("Salvando nova ordem")
			.adicionaInformacao("ativo", ordem.getAtivo())
			.adicionaInformacao("valor", ordem.getPreco().toString())
			.info(log);

		Ordem novaOrdemAdicionada = possivelBook.get().adiciona(ordem :: toModel);
		DadosExtrasOrdemLimitada dadosExtrasOrdemLimitada = (DadosExtrasOrdemLimitada)novaOrdemAdicionada.getDadosExtrasTipoOrdem();

		Log5WBuilder
			.metodo()
			.oQueEstaAcontecendo("Nova ordem salva")
			.adicionaInformacao("ativo", novaOrdemAdicionada.getAtivo())
			.adicionaInformacao("ativo", dadosExtrasOrdemLimitada.getPreco().toString())
			.adicionaInformacao("instante", novaOrdemAdicionada.getInstante().toString())
			.info(log);	
			
		

	}
}
