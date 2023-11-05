package com.deveficiente.com.processaordemhomebroker;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.deveficiente.com.processaordemhomebroker.compartilhado.Log5WBuilder;
import com.deveficiente.com.processaordemhomebroker.compartilhado.Resultado;

import jakarta.transaction.Transactional;

@Component
public class ProcessaOrdemLimitadaTonListener {

	private OrdemLimitadaRepository ordemLimitadaRepository;
	private AcessoMelhoresOfertas acessoMelhoresOfertas;

	private static final Logger log = LoggerFactory
			.getLogger(ProcessaOrdemLimitadaTonListener.class);

	public ProcessaOrdemLimitadaTonListener(
			OrdemLimitadaRepository ordemLimitadaRepository,
			AcessoMelhoresOfertas acessoMelhoresOfertas) {
		super();
		this.ordemLimitadaRepository = ordemLimitadaRepository;
		this.acessoMelhoresOfertas = acessoMelhoresOfertas;
	}

	@JmsListener(destination = "processa-ordem-limitada-ton", containerFactory = "myFactory")
	@Transactional
	public void receiveMessage(Map<String, String> mensagem) {
		Assert.isTrue(mensagem.containsKey("codigoOrdem"),
				"Precisa do codigo da ordem para processar");

		String codigoOrdem = mensagem.get("codigoOrdem");

		OrdemLimitada ordemParaSerProcessada = this.ordemLimitadaRepository
				.getByCodigo(UUID.fromString(codigoOrdem));

		Assert.notNull(ordemParaSerProcessada,
				"Não deveria chegar um código de ordem inexistente aqui "
						+ codigoOrdem);

		Log5WBuilder.metodo().oQueEstaAcontecendo("Vai executar a ordem")
				.adicionaInformacao("codigoOrdem", codigoOrdem).info(log);

		Resultado<RuntimeException, Void> resultado = ordemParaSerProcessada
				.executa(acessoMelhoresOfertas);

		Log5WBuilder.metodo().oQueEstaAcontecendo("Executou a ordem")
				.adicionaInformacao("codigoOrdem", codigoOrdem)
				.adicionaInformacao("sucesso ?", resultado.isSucesso() + "")
				.info(log);

	}
}
