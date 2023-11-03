package com.deveficiente.com.processaordemhomebroker;

public class AtivoInsuficienteException extends RuntimeException {

	private String codigoAtivo;
	private int quantidade;

	public AtivoInsuficienteException(String codigoAtivo, int quantidade) {
		this.codigoAtivo = codigoAtivo;
		this.quantidade = quantidade;
		// TODO Auto-generated constructor stub
	}

}
