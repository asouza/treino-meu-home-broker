package com.deveficiente.com.processaordemhomebroker;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.deveficiente.com.processaordemhomebroker.compartilhado.Resultado;

class OrdemLimitadaTest {

	@ParameterizedTest
	@CsvSource({
		"venda,true",
		"compra,false"
	})
	@DisplayName("Deveria verificar se uma ordem é oposta a outra")
	void test1(TipoOferta tipoOferta,boolean resultadoEsperado) {
		BookOfertas bookOfertas = new BookOfertas("vale");

		Cliente cliente = new Cliente("codigo");

		OrdemLimitada origem = OrdemLimitada.novaLimitadaTON(bookOfertas, cliente, 10, "corretora",
				TipoOferta.compra, BigDecimal.TEN);
		
		OrdemLimitada oposta = OrdemLimitada.novaLimitadaTON(bookOfertas, cliente, 10, "corretora",
				tipoOferta, BigDecimal.TEN);
		
		Assertions.assertEquals(resultadoEsperado,origem.isOposta(oposta));
	}
	
	@ParameterizedTest
	@CsvSource({
		"venda,false",
		"compra,true"
	})
	@DisplayName("Deveria verificar se uma ordem é de compra")
	void test2(TipoOferta tipoOferta,boolean resultadoEsperado) {
		BookOfertas bookOfertas = new BookOfertas("vale");
		
		Cliente cliente = new Cliente("codigo");
		
		OrdemLimitada origem = OrdemLimitada.novaLimitadaTON(bookOfertas, cliente, 10, "corretora",
				tipoOferta, BigDecimal.TEN);
		
		Assertions.assertEquals(resultadoEsperado,origem.isCompra());
	}
	
	@Test
	@DisplayName("Deveria verificar se uma ordem tem a quantidade exata a outra")
	void test3() {
		BookOfertas bookOfertas = new BookOfertas("vale");
		
		Cliente cliente = new Cliente("codigo");
		
		OrdemLimitada origem = OrdemLimitada.novaLimitadaTON(bookOfertas, cliente, 10, "corretora",
				TipoOferta.compra, BigDecimal.TEN);
		
		OrdemLimitada oposta = OrdemLimitada.novaLimitadaTON(bookOfertas, cliente, 10, "corretora",
				TipoOferta.venda, BigDecimal.TEN);		
		
		Assertions.assertEquals(true,origem.quantidadeIgual(oposta));
	}
	
	@Test
	@DisplayName("Deveria verificar se uma ordem tem a quantidade diferente da outra")
	void test4() {
		BookOfertas bookOfertas = new BookOfertas("vale");
		
		Cliente cliente = new Cliente("codigo");
		
		OrdemLimitada origem = OrdemLimitada.novaLimitadaTON(bookOfertas, cliente, 10, "corretora",
				TipoOferta.compra, BigDecimal.TEN);
		
		OrdemLimitada oposta = OrdemLimitada.novaLimitadaTON(bookOfertas, cliente, 1, "corretora",
				TipoOferta.venda, BigDecimal.ONE);		
		
		Assertions.assertEquals(false,origem.quantidadeIgual(oposta));
	}
	
	@Test
	@DisplayName("Deveria verificar que uma ordem ainda nao foi executada nenhuma vez")
	void test5() {
		BookOfertas bookOfertas = new BookOfertas("vale");
		
		Cliente cliente = new Cliente("codigo");
		
		OrdemLimitada origem = OrdemLimitada.novaLimitadaTON(bookOfertas, cliente, 10, "corretora",
				TipoOferta.compra, BigDecimal.TEN);
		
		Assertions.assertEquals(false,origem.foiExecutadaComSucesso());
	}
	
	@Test
	@DisplayName("Deveria verificar que uma ordem foi executada com falha")
	void test6() {
		BookOfertas bookOfertas = new BookOfertas("vale");
		
		Cliente cliente = new Cliente("codigo");
		
		OrdemLimitada origem = OrdemLimitada.novaLimitadaTON(bookOfertas, cliente, 10, "corretora",
				TipoOferta.compra, BigDecimal.TEN);
		
		ReflectionTestUtils.setField(origem, "execucoes", List.of(ExecucaoOrdem.falha(origem)));
		
		Assertions.assertEquals(false,origem.foiExecutadaComSucesso());
	}
	
	@Test
	@DisplayName("Deveria verificar que uma ordem foi executada com sucesso")
	void test7() {
		BookOfertas bookOfertas = new BookOfertas("vale");
		
		Cliente cliente = new Cliente("codigo");
		
		OrdemLimitada origem = OrdemLimitada.novaLimitadaTON(bookOfertas, cliente, 10, "corretora",
				TipoOferta.compra, BigDecimal.TEN);
		
		OrdemLimitada oposta = OrdemLimitada.novaLimitadaTON(bookOfertas, cliente, 10, "corretora",
				TipoOferta.venda, BigDecimal.ONE);		
		
		ReflectionTestUtils.setField(origem, "execucoes", List.of(ExecucaoOrdem.sucesso(origem, oposta)));
		
		Assertions.assertEquals(true,origem.foiExecutadaComSucesso());
	}
	
	@Test
	@DisplayName("Deveria executar uma ordem com falha")
	void test8() {
		BookOfertas bookOfertas = new BookOfertas("vale");
		
		Cliente clienteComprador = new Cliente("codigo");		
		clienteComprador.realizaCredito(BigDecimal.TEN);
		
		OrdemLimitada origem = OrdemLimitada.novaLimitadaTON(bookOfertas, clienteComprador, 10, "corretora",
				TipoOferta.compra, BigDecimal.TEN);
		
//		OrdemLimitada oposta = OrdemLimitada.novaLimitadaTON(bookOfertas, clienteComprador, 10, "corretora",
//				TipoOferta.venda, BigDecimal.ONE);
		
		AcessoMelhoresOfertas melhoresOfertas = Mockito.mock(AcessoMelhoresOfertas.class);
		Mockito.when(melhoresOfertas.buscaMelhorOferta(origem)).thenReturn(Optional.empty());
		
		
		Resultado<RuntimeException, Void> resultado = origem.executa(melhoresOfertas);
		
		@SuppressWarnings("unchecked")
		List<ExecucaoOrdem> execucoes = (List<ExecucaoOrdem>) ReflectionTestUtils.getField(origem, "execucoes");
		
		
		Assertions.assertEquals(false,resultado.isSucesso());
		Assertions.assertEquals(1, execucoes.size());
		Assertions.assertEquals(false, execucoes.get(0).isSucesso());
		Assertions.assertEquals(true, execucoes.get(0).isCompra());
		Assertions.assertTrue(resultado.getProblema() instanceof NaoAchouMatchParaOrdemLimitadaException);
	}
	
	@Test
	@DisplayName("Deveria executar uma ordem com sucesso")
	void test9() {
		BookOfertas bookOfertas = new BookOfertas("vale");
		
		Cliente clienteComprador = new Cliente("codigo");		
		clienteComprador.realizaCredito(new BigDecimal("100"));
		
		//prepara o vendedor com tudo que precisa
		Cliente clienteVendedor = new Cliente("codigo");		
		clienteVendedor.realizaCredito(BigDecimal.TEN);
		Carteira carteiraVendedor = (Carteira) ReflectionTestUtils.getField(clienteVendedor,"carteira");
		List<AtivoCarteiraCliente> ativosVendedor = (List<AtivoCarteiraCliente>) ReflectionTestUtils.getField(carteiraVendedor, "ativos");
		ativosVendedor.add(new AtivoCarteiraCliente("vale", 10));
		
		OrdemLimitada origem = OrdemLimitada.novaLimitadaTON(bookOfertas, clienteComprador, 10, "corretora",
				TipoOferta.compra, BigDecimal.TEN);
		
		OrdemLimitada oposta = OrdemLimitada.novaLimitadaTON(bookOfertas, clienteVendedor, 10, "corretora",
				TipoOferta.venda, BigDecimal.TEN);
		
		
		AcessoMelhoresOfertas melhoresOfertas = Mockito.mock(AcessoMelhoresOfertas.class);
		Mockito.when(melhoresOfertas.buscaMelhorOferta(origem)).thenReturn(Optional.of(oposta));
		
		
		Resultado<RuntimeException, Void> resultado = origem.executa(melhoresOfertas);
		
		@SuppressWarnings("unchecked")
		List<ExecucaoOrdem> execucoesOrigem = (List<ExecucaoOrdem>) ReflectionTestUtils.getField(origem, "execucoes");
		@SuppressWarnings("unchecked")
		List<ExecucaoOrdem> execucoesMatch = (List<ExecucaoOrdem>) ReflectionTestUtils.getField(oposta, "execucoes");
		
		
		Assertions.assertEquals(true,resultado.isSucesso());
		Assertions.assertEquals(1, execucoesOrigem.size());
		Assertions.assertEquals(true, execucoesOrigem.get(0).isSucesso());
		Assertions.assertEquals(true, execucoesOrigem.get(0).isCompra());
		Assertions.assertEquals(1, execucoesMatch.size());
		Assertions.assertEquals(true, execucoesMatch.get(0).isSucesso());
		Assertions.assertEquals(false, execucoesMatch.get(0).isCompra());
		Assertions.assertEquals(BigDecimal.ZERO, clienteComprador.getSaldoAtual());
		Assertions.assertEquals(new BigDecimal("110"), clienteVendedor.getSaldoAtual().setScale(0));
	}

}
