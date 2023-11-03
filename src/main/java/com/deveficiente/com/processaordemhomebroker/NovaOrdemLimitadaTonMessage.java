package com.deveficiente.com.processaordemhomebroker;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class NovaOrdemLimitadaTonMessage implements NovaOrdemLimitadaMessage {

	@NotBlank
	private String ativo;
	@Min(1)
	private int quantidade;
	@NotBlank
	private String codigoCorretora;
	@NotNull
	private TipoOferta tipoOferta;
	@Positive
	private BigDecimal preco;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	NovaOrdemLimitadaTonMessage(@NotBlank @JsonProperty("ativo") String ativo,
			@Min(1) @JsonProperty("quantidade") int quantidade,
			@NotBlank @JsonProperty("codigoCorretora") String codigoCorretora,
			@NotNull @JsonProperty("tipoOferta") TipoOferta tipoOferta,
			@Positive @JsonProperty("preco") BigDecimal preco) {
		this.ativo = ativo;
		this.quantidade = quantidade;
		this.codigoCorretora = codigoCorretora;
		this.tipoOferta = tipoOferta;
		this.preco = preco;
	}

	public String getAtivo() {
		return ativo;
	}

	@Override
	public String toString() {
		return "NovaOrdemLimitadaTonMessage [ativo=" + ativo + ", quantidade="
				+ quantidade + ", codigoCorretora=" + codigoCorretora
				+ ", tipoOrdem=" + tipoOferta + ", preco=" + preco + "]";
	}

	public OrdemLimitada toModel(BookOfertas bookOfertas,Cliente cliente) {
		// aqui tem que usar um builder para criar a ordem considerando a
		// especificidade
		// Ordem.novaLimitadaTON(bookOfertas, quantidade, codigoCorretora,
		// tipoOrdem, preco)
		// precisa enriquecer a ordem com o tipo dela(mercado,limitada etc,),
		// validadade e infos especificas.
		// Se for limitada -> tem preco
		return OrdemLimitada.novaLimitadaTON(bookOfertas,cliente, quantidade,
				codigoCorretora, tipoOferta, preco);
	}

	public BigDecimal getPreco() {
		return preco;
	}

	@Override
	public int getQuantidade() {
		return quantidade;
	}

	@Override
	public String codigoCorretora() {
		return codigoCorretora;
	}

	@Override
	public TipoOferta getTipoOferta() {
		return tipoOferta;
	}

	public Map<String, String> toMap() {
		return Map.of("ativo", ativo, "quantidade", String.valueOf(quantidade),
				"preco", preco.toString(), "codigoCorretora", codigoCorretora,
				"tipoOferta", tipoOferta.name());
	}

}
