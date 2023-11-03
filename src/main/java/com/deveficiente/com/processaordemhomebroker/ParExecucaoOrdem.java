package com.deveficiente.com.processaordemhomebroker;

import org.springframework.util.Assert;

public class ParExecucaoOrdem {

	private ExecucaoOrdem origemExecucao;
	private ExecucaoOrdem matchExecucao;

	public ParExecucaoOrdem(ExecucaoOrdem origemExecucao,
			ExecucaoOrdem matchExecucao) {

		Assert.isTrue(origemExecucao.isSucesso(),
				"Só pode criar um par de execucoes quando tem sucesso");

		this.origemExecucao = origemExecucao;
		this.matchExecucao = matchExecucao;
		// TODO Auto-generated constructor stub
	}

	public void atualizaClientes() {
		origemExecucao.atualizaCliente();
		matchExecucao.atualizaCliente();
	}
	
	public ExecucaoOrdem getOrigemExecucao() {
		return origemExecucao;
	}
	
	public ExecucaoOrdem getMatchExecucao() {
		return matchExecucao;
	}

}
