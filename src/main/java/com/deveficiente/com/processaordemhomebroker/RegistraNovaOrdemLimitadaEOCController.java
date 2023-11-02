package com.deveficiente.com.processaordemhomebroker;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class RegistraNovaOrdemLimitadaEOCController {

    /*
     * Existem combinacoes entre tipo de ordem e validade, pensar numa request só recebendo
     * todas as informações não parece a solução mais simples. Neste momento prefiro ter entradas
     * diferentes para cada combinação, até pq as necessidades de informações podem variar. Por
     * exemplo, se ela é a mercado, não tem preco. Se ela é casada, são duas ordens e 
     * isso também vale para a ordem de stop. 
     */
    
    @PostMapping("/api/ordens/limitada-e-oc")
    public void executa(@RequestBody @Valid NovaOrdemimitadaEOCRequest request) {
        System.out.println("vai registrar nova ordem limitada eoc no book de ofertas: "+request);
    }
}
