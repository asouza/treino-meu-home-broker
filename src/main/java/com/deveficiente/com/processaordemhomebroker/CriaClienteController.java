package com.deveficiente.com.processaordemhomebroker;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;

@RestController
public class CriaClienteController {
	
	private ClienteRepository clienteRepository;	

	public CriaClienteController(ClienteRepository clienteRepository) {
		super();
		this.clienteRepository = clienteRepository;
	}


	@PostMapping("/api/clientes")
	@Transactional
	public void executa(String codigo) {
		Cliente novoCliente = new Cliente(codigo);
		clienteRepository.save(novoCliente);
	}
}
