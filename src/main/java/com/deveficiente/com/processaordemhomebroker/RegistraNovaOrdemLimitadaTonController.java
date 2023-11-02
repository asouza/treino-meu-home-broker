package com.deveficiente.com.processaordemhomebroker;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    RegistraNovaOrdemLimitadaTonController(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
    
    
    @PostMapping("/api/ordens/limitada-ton")
    public void executa(@RequestBody @Valid NovaOrdemLimitadaTONRequest request) {

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

        jmsTemplate.convertAndSend("insere-book-ofertas-limitada-ton", request.toMessage());
    }
}