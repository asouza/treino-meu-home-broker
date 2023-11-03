package com.deveficiente.com.processaordemhomebroker.compartilhado;

import org.springframework.util.Assert;

public class Resultado<TipoProblema extends RuntimeException, TipoSucesso> {

    private boolean sucesso;
    private TipoProblema problema;

    private Resultado(boolean sucesso) {
        this.sucesso = sucesso;
    }

    private Resultado(TipoProblema problema) {
        this.problema = problema;
    }

    public static Resultado<RuntimeException, Void> sucessoSemInfoAdicional() {
        return new Resultado(true);
    }

    public static <TipoProblema extends RuntimeException> Resultado<TipoProblema, Void> falhaCom(TipoProblema problema) {
        return new Resultado<TipoProblema,Void>(problema);
    }

	public boolean temErro() {
		return !this.sucesso;
	}

	public RuntimeException getProblema() {
		Assert.isTrue(!sucesso, "Só pode buscar o problema se tiver erro");
		return this.problema;
	}

	public boolean isSucesso() {
		return this.sucesso;
	}


}
