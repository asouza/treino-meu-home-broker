package com.deveficiente.com.processaordemhomebroker;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.deveficiente.com.processaordemhomebroker.compartilhado.ExecutaTransacao;
import com.deveficiente.com.processaordemhomebroker.compartilhado.Log5WBuilder;

import jakarta.transaction.Transactional;

@Component
public class RegistraNovaOrdemLimitadaBookOfertasListener {

	private BookOfertasRepository bookOfertasRepository;
	private ClienteRepository clienteRepository;
	private JmsTemplate jmsTemplate;
	private ExecutaTransacao executaTransacao;

	private static final Logger log = LoggerFactory
			.getLogger(RegistraNovaOrdemLimitadaBookOfertasListener.class);

	public RegistraNovaOrdemLimitadaBookOfertasListener(
			BookOfertasRepository bookOfertasRepository,
			ClienteRepository clienteRepository, JmsTemplate jmsTemplate,
			ExecutaTransacao executaTransacao) {
		this.bookOfertasRepository = bookOfertasRepository;
		this.clienteRepository = clienteRepository;
		this.jmsTemplate = jmsTemplate;
		this.executaTransacao = executaTransacao;
	}

	@JmsListener(destination = "insere-book-ofertas-limitada", containerFactory = "myFactory")
	public void receiveMessage(Map<String, String> mensagem) {

		Assert.isTrue(mensagem.containsKey("tipoValidade"),
				"O tipo de validade é obrigatório para processar uma ordem limitada");

		TipoValidade tipoValidade = TipoValidade
				.converte(mensagem.get("tipoValidade"));
		
		Assert.isTrue(mensagem.containsKey("codigoCliente"), "O código do cliente é obrigatório para processar uma ordem");
			

		// aqui eu entendi que precisava de polimorfismo para lidar com os
		// outros tipos de validade.
		// ao mesmo tempo o número de validade é fixo, poderia ser quatro ifs
		NovaOrdemLimitadaMessage novaOrdemLimitadaMessage = tipoValidade
				.criaNovaLimitadaMessage(mensagem);

		/*
		 * Carrego o book de ofertas para o ativo Registro a ordem Mando agora
		 * para processar a ordem seguindo o tipo e considerando a validade
		 */

		Log5WBuilder.metodo().oQueEstaAcontecendo("Salvando nova ordem")
				.adicionaInformacao("ativo",
						novaOrdemLimitadaMessage.getAtivo())
				.adicionaInformacao("preco",
						novaOrdemLimitadaMessage.getPreco().toString())
				.info(log);
		
		

		OrdemLimitada novaOrdemAdicionada = executaTransacao.comRetorno(() -> {
			
			Optional<BookOfertas> possivelBook = bookOfertasRepository
					.findByAtivo(novaOrdemLimitadaMessage.getAtivo());
			Assert.isTrue(possivelBook.isPresent(),
					"Não existe book de ofertas para o ativo "
							+ novaOrdemLimitadaMessage.getAtivo());
			
			Cliente cliente = clienteRepository.getByCodigo(mensagem.get("codigoCliente"));
			
			 return possivelBook.get()
					.adiciona(bookOfertas -> {
						return novaOrdemLimitadaMessage.toModel(bookOfertas,cliente);
					});
			
		});

		Log5WBuilder.metodo().oQueEstaAcontecendo("Nova ordem salva")
				.adicionaInformacao("ativo", novaOrdemAdicionada.getAtivo())
				.adicionaInformacao("preco",
						novaOrdemAdicionada.getPreco().toString())
				.adicionaInformacao("instante",
						novaOrdemAdicionada.getInstante().toString())
				.info(log);

		/*
		 * Aqui eu tenho uma dúvida, qual a restrição para processar a ordem.
		 * Até acabar de processar uma ordem inserida no book pode chegar uma
		 * outra que altere o estado do book. Isso é aceitável ou precisa de um
		 * snapshot do book? Apesar que o snapshot não parece escalável
		 */
		
		Log5WBuilder.metodo()
		.oQueEstaAcontecendo("Envinado mensagem para executar a ordem colocada no book")
		.adicionaInformacao("codigoOrdem", novaOrdemAdicionada.getCodigo().toString())
		.info(log);		

		this.jmsTemplate.convertAndSend("processa-ordem-limitada-ton",
				Map.of("codigoOrdem", novaOrdemAdicionada.getCodigo().toString()));
		
		Log5WBuilder.metodo()
		.oQueEstaAcontecendo("Ordem enviada para ser executada")
		.adicionaInformacao("codigoOrdem", novaOrdemAdicionada.getCodigo().toString())
		.info(log);		

	}
}
