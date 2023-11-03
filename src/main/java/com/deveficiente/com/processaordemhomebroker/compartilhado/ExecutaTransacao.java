package com.deveficiente.com.processaordemhomebroker.compartilhado;

import org.springframework.stereotype.Component;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Supplier;
import jakarta.transaction.Transactional;

@Component
public class ExecutaTransacao {

	@Transactional
	public <T> T comRetorno(Supplier<T> supplier) {
		return supplier.get();
	}
}
