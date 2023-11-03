package com.deveficiente.com.processaordemhomebroker;

public class NaoAchouMatchParaOrdemLimitadaException extends RuntimeException {

	public NaoAchouMatchParaOrdemLimitadaException(
			OrdemLimitada ordemLimitada,String message) {
		super(message);
	}

}
