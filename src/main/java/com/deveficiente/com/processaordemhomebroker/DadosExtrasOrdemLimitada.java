package com.deveficiente.com.processaordemhomebroker;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.util.Assert;

public class DadosExtrasOrdemLimitada {

    private BigDecimal preco;

    public DadosExtrasOrdemLimitada(Map<String,String> dadosExtras) {
        Assert.isTrue(dadosExtras.containsKey("preco"),"Dados extras da ordem limitada devem conter o preco");
        this.preco = new BigDecimal(dadosExtras.get("preco"));
    }

    public BigDecimal getPreco() {
        return preco;
    }
}
