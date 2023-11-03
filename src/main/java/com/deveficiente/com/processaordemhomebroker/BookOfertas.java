package com.deveficiente.com.processaordemhomebroker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

@Entity
public class BookOfertas {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToMany(mappedBy = "bookOfertas", cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	private List<OrdemLimitada> ordensCompra = new ArrayList<>();
	private String ativo;

	@Deprecated
	public BookOfertas() {
	}

	public BookOfertas(String ativo) {
		this.ativo = ativo;
	}

	public OrdemLimitada adiciona(
			Function<BookOfertas, OrdemLimitada> criaOrdem) {
		/*
		 * Outra maneira era receber os dados da ordem e instanciar a ordem aqui
		 * passando o book
		 */
		OrdemLimitada ordem = criaOrdem.apply(this);
		Assert.isTrue(ordem.pertenceAAtivo(this.ativo),
				"No book de ofertas do ativo " + ativo
						+ " só entra ordem do mesmo ativo");

		this.ordensCompra.add(ordem);

		return ordem;
	}

	public String getAtivo() {
		return ativo;
	}

	public List<OrdemLimitada> getOrdensPorInstante() {
		return ordensCompra.stream().sorted(
				(o1, o2) -> o1.getInstante().compareTo(o2.getInstante()))
				.collect(Collectors.toList());
	}

	public boolean pertenceAAtivo(@NotBlank String ativo) {
		return this.ativo.equals(ativo);
	}

	public Optional<OrdemLimitada> buscaMelhorOfertaLimitada(
			OrdemLimitada ordemLimitada) {

		return this.ordensCompra.stream()
				.filter(ordem -> !ordem.foiExecutadaComSucesso())
				.filter(ordem -> ordem.isOposta(ordemLimitada))
				.filter(ordem -> ordem.precoDentroDoLimite(ordemLimitada))
				.sorted(ordemLimitada.funcaoOrdenaPorMelhorPreco())
				.findFirst();

	}

}
