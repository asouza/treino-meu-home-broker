package com.deveficiente.com.processaordemhomebroker;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.deveficiente.com.processaordemhomebroker.compartilhado.Log5WBuilder;
import com.deveficiente.com.processaordemhomebroker.compartilhado.Resultado;

import jakarta.validation.Valid;

@RestController
public class RegistraNovaOrdemLimitadaTonController {

    /*
     * Existem combinacoes entre tipo de ordem e validade, pensar numa request só recebendo
     * todas as informações não parece a solução mais simples. Neste momento prefiro ter entradas
     * diferentes para cada combinação, até pq as necessidades de informações podem variar. Por
     * exemplo, se ela é a mercado, não tem preco. Se ela é casada, são duas ordens e 
     * isso também vale para a ordem de stop. 
     */

    private JmsTemplate jmsTemplate;
    private ClienteRepository clienteRepository;
    
	private static final Logger log = LoggerFactory
			.getLogger(RegistraNovaOrdemLimitadaTonController.class);


    RegistraNovaOrdemLimitadaTonController(JmsTemplate jmsTemplate,ClienteRepository clienteRepository) {
        this.jmsTemplate = jmsTemplate;
        this.clienteRepository = clienteRepository;
    }
    
    
    @PostMapping("/api/ordens/{codigoCliente}/limitada-ton")
    public void executa(@PathVariable("codigoCliente") String codigoCliente,@RequestBody @Valid NovaOrdemLimitadaTONRequest request) {

        /*
         * Ideia 1:
         * Aqui eu transformo numa mensagem para o book de ofertas enriquecendo com o 
         * tipo e validade da ordem. Dessa forma o listener da fila pode decidir 
         * como realizar o próximo passo. 
         * 
         * Acho que o ponto fraco é que o enriquecimento tem ir na forma de metadado
         */

         /*
          * Ideia 2:
          * Repasso para uma fila especifica considerando a combinacao de tipo e validade. 
          *
          * Aqui eu tenho a vantagem de usar a linguagem para definir melhor o contrato,
          * o que deve facilitar quaisquer alterações no futuro. 
          * 
          */

        /*
         * - carregar o cliente
         * - verificar se for para comprar, verificar se tem saldo
         * - verificar se for para vender, verificar se tem ativo na quantidade
         */

         //aqui depois é para trocar por autenticado e tal..
         Cliente clienteSolicitante = clienteRepository.getByCodigo(codigoCliente);
         Assert.notNull(clienteSolicitante, "Não tem cliente com este o código "+codigoCliente);

         Resultado<RuntimeException,Void> resultado = clienteSolicitante.podeRealizarOperacao(request);

         if(resultado.temErro()){
            throw resultado.getProblema();
         }
         
        //preciso adicionar o tipo da validade aqui, mas o meu guideline recomenda nao alterar estado de quem você não criou
         
        HashMap<String, String> mensagem = new HashMap<>(request.toMessage().toMap());
        mensagem.put("tipoValidade", TipoValidade.TON.name());
        mensagem.put("codigoCliente", codigoCliente);
        
        Log5WBuilder
        	.metodo()
        	.oQueEstaAcontecendo("Enviando nova ordem limitada para o book")
        	.adicionaInformacao("mensagem", mensagem.toString())
        	.info(log);
        
        jmsTemplate.convertAndSend("insere-book-ofertas-limitada", mensagem);
        
        Log5WBuilder
    	.metodo()
    	.oQueEstaAcontecendo("Ordem enviada para o book de ofertas")
    	.adicionaInformacao("mensagem", mensagem.toString())
    	.info(log);        
    }
}
