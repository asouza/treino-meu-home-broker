package com.deveficiente.com.processaordemhomebroker;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;

@Repository
public class AcessoMelhoresOfertas {

	private EntityManager entityManager;

	public AcessoMelhoresOfertas(EntityManager entityManager) {
		super();
		this.entityManager = entityManager;
	}

	public Optional<OrdemLimitada> buscaMelhorOferta(OrdemLimitada alvo) {
		
		//outra forma de fazer, se ficasse mais dinâmico, era usar as specifications
		
		//se a oferta é de compra preciso achar valores iguais ou menores.
		//se a oferta de venda preciso achar valores iguais ou maiores
		String operadorComparacaoPreco = alvo.isCompra() ? "<=" : ">=";
		
		String jpql = "select distinct(ol) from OrdemLimitada ol where"
				+ "	(select count(1) from ExecucaoOrdem eo where eo.status = :statusExecucao) >= 0"
				+ "	and ol.tipoOferta = :tipoOferta "
				+ " and ol.preco "+operadorComparacaoPreco+" :preco "
				+ "	and ol.quantidade = :quantidade "
				+ " and ol.bookOfertas.id = :bookOfertasId"
				+ "	order by ol.preco,ol.instante asc";
		
		List<OrdemLimitada> possivesOfertas = entityManager
			.createQuery(jpql,OrdemLimitada.class)
			.setParameter("statusExecucao", StatusExecucao.falha)
			.setParameter("tipoOferta", alvo.getTipoOfertaOposta())
			.setParameter("preco", alvo.getPreco())
			.setParameter("quantidade", alvo.getQuantidade())
			.setParameter("bookOfertasId", alvo.getBookOfertas().getId())
			.getResultList();
		
		return possivesOfertas.stream().findFirst();
	}
}
