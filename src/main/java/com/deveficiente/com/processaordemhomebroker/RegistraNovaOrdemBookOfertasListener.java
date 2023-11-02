package com.deveficiente.com.processaordemhomebroker;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class RegistraNovaOrdemBookOfertasListener {
    
    @JmsListener(destination = "insere-book-ofertas-limitada-ton", containerFactory = "myFactory")
	public void receiveMessage(NovaOrdemLimitadaTonMessage ordem) {
		System.out.println("Nova ordem para o book de ofertas:"+ ordem);
	}
}
