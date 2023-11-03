package com.deveficiente.com.processaordemhomebroker;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class ExecucaoOrdem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private OrdemLimitada origem;
	@ManyToOne
	private OrdemLimitada match;
	@NotNull
	private StatusExecucao status;
	private LocalDateTime instante = LocalDateTime.now();

	private ExecucaoOrdem(OrdemLimitada origem, OrdemLimitada match) {
		this.origem = origem;
		this.match = match;
		this.status = StatusExecucao.sucesso;
	}

	public ExecucaoOrdem(OrdemLimitada origem) {
		this.origem = origem;
		this.status = StatusExecucao.falha;
	}

	public static ExecucaoOrdem sucesso(OrdemLimitada origem,
			OrdemLimitada match) {
		return new ExecucaoOrdem(origem, match);
	}

	public static ExecucaoOrdem falha(OrdemLimitada origem) {
		return new ExecucaoOrdem(origem);
	}

	public boolean isSucesso() {
		return this.status == StatusExecucao.sucesso;
	}

	public boolean isCompra() {
		return this.origem.isCompra();
	}

	public AtivoCarteiraCliente toAtivo() {
		return this.origem.toAtivo();
	}

	public TipoOferta getTipoOferta() {
		return this.origem.getTipoOferta();
	}

	public int getQuantidade() {
		return this.origem.getQuantidade();
	}

	public Optional<BigDecimal> getPreco() {
		return Optional
				.ofNullable(this.match)
				.map(match -> match.getPreco());
	}

	public String getAtivo() {
		return this.origem.getAtivo();
	}

}
