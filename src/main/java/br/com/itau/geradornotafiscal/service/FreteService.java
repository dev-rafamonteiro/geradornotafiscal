package br.com.itau.geradornotafiscal.service;

import br.com.itau.geradornotafiscal.model.Pedido;

public interface FreteService {
    double calcularFrete(Pedido pedido);
}
