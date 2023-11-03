package com.deveficiente.com.processaordemhomebroker;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;

@RestController
public class ControlaCreditoClienteController {

	private ClienteRepository clienteRepository;

	public ControlaCreditoClienteController(
			ClienteRepository clienteRepository) {
		super();
		this.clienteRepository = clienteRepository;
	}

	@PostMapping("/api/clientes/{codigo}/credito")
	@Transactional
	public void adicionaFake(@PathVariable("codigo") String codigo,BigDecimal valor) {
		Cliente cliente = clienteRepository.getByCodigo(codigo);
		cliente.realizaCredito(valor);
	}
	
	@GetMapping("/api/clientes/{codigo}/credito")
	public BigDecimal consultaCredito(@PathVariable("codigo") String codigo) {
		Cliente cliente = clienteRepository.getByCodigo(codigo);
		return cliente.getSaldoAtual();
	}
	
}
