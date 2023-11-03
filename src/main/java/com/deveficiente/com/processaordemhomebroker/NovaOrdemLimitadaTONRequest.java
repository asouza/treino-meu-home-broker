package com.deveficiente.com.processaordemhomebroker;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

//configurar jackson para ler os atributos privados na serialiazacao
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class NovaOrdemLimitadaTONRequest {

    @NotBlank
    private String ativo;
    @Min(1)
    private int quantidade;
    @NotBlank
    private String codigoCorretora;
    @NotNull
    private TipoOferta tipoOrdem;
    @Positive
    private BigDecimal preco;

    /*
     * Aqui eu poderia ter deixado com setters, mas aí uma classe de http request estaria
     * sendo influenciada pela minha configuracao equivocada do processo de mensageria. Eu quase
     * tirei o construtor e coloquei setters por conta de uma InvalidTypeException(ou algo assim)
     * que tava rolando. Acho que tem a ver com uma configuracao do converter de jackson para message
     * qeu eu não manjo direito. 
     * 
     * A configuracao feita não habilita o jackson a ler os nomes dos parametros direto via
     * reflection. Eu teria que meter uns JsonProperty para cada param aqui... 
     */

    NovaOrdemLimitadaTONRequest(@NotBlank String ativo, @Min(1) int quantidade, @NotBlank String codigoCorretora,
            @NotNull TipoOferta tipoOrdem, @Positive BigDecimal preco) {
        this.ativo = ativo;
        this.quantidade = quantidade;
        this.codigoCorretora = codigoCorretora;
        this.tipoOrdem = tipoOrdem;
        this.preco = preco;
    }

    @Override
    public String toString() {
        return "NovaOrdemimitadaEOCRequest [ativo=" + ativo + ", quantidade=" + quantidade + ", codigoCorretora="
                + codigoCorretora + ", tipoOrdem=" + tipoOrdem + ", preco=" + preco + "]";
    }

    public NovaOrdemLimitadaTonMessage toMessage() {
        return new NovaOrdemLimitadaTonMessage(ativo, quantidade, codigoCorretora, tipoOrdem, preco);
    }

    

}
