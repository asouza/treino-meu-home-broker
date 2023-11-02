package com.deveficiente.com.processaordemhomebroker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProcessaOrdemHomeBrokerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcessaOrdemHomeBrokerApplication.class, args);
	}

}

/**
 * - a entidade pode realiazar uma ordem de compra ou venda
 * - uma ordem tem o instante, o tipo (compra ou venda), corretora, quantidade e preço. 
 * - uma ordem pode ser a mercado(busca o melhor match), limitada(paga o melhor preço considerando o limite estabelecido).
 * - Ordem casada é quando a entidade emite uma ordem de venda de uma ativo x por um valor e uma ordem de compra de um ativo y por outro valor. A operaçaão só se completa se as duas funcionarem. 
 * - Ordem de stop acontece para vendas e compras. Para venda é quando você define um limite de preço para vender, se chegar naquilo ou abaixo você quer fazera a melhor venda que puder. A ordem de stop de compra(avançada pra mim) é quando você quer comprar uma ação que está entrando num momento ascendente. Se ela vencer aquele preço você entende que é legal de entrar. 
 * - Toda ordem tem uma validade. Pode ser para o dia, até uma determinada data, até o investidor cancelar, validade de tudo ou nada e "execute ou cancele" que eu entendi que é faça o melhor que puder(compra ou venda parcial)
 * - Toda ordem emitida vai para o book de ofertas (essa é uma operação que me parece síncrona). O book de ofertas é ordenado pelas melhores ofertas. Maior preço de compra e menor preço de venda. 
 * - Se duas ordens são exatamente iguais, o momento de emissão é o critério de desempate.
 * - Depois que a ordem está no book de ofertas, acontece a tentativa da transação. 
 */
