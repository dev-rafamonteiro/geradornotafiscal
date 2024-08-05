package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.service.EstoqueService;
import org.springframework.stereotype.Service;

@Service
public class EstoqueServiceImpl implements EstoqueService {
    @Override
    public void enviarNotaFiscalParaBaixaEstoque(NotaFiscal notaFiscal) {
        // Implementação da lógica de baixa de estoque
    }
}
