package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.service.FinanceiroService;
import org.springframework.stereotype.Service;

@Service
public class FinanceiroServiceImpl implements FinanceiroService {
    @Override
    public void enviarNotaFiscalParaContasReceber(NotaFiscal notaFiscal) {
        // Implementação da lógica de envio para contas a receber
    }
}
