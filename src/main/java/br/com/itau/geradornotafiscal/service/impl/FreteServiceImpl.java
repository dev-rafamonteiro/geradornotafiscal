package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.service.FreteService;
import org.springframework.stereotype.Service;

@Service
public class FreteServiceImpl implements FreteService {

    @Override
    public double calcularFrete(Pedido pedido) {
        Destinatario destinatario = pedido.getDestinatario();
        Regiao regiao = destinatario.getEnderecos().stream()
                .filter(endereco -> endereco.getFinalidade() == Finalidade.ENTREGA || endereco.getFinalidade() == Finalidade.COBRANCA_ENTREGA)
                .map(Endereco::getRegiao)
                .findFirst()
                .orElse(null);

        double valorFrete = pedido.getValorFrete();
        return calcularFretePorRegiao(valorFrete, regiao);
    }

    private double calcularFretePorRegiao(double valorFrete, Regiao regiao) {
        if (regiao == Regiao.NORTE) {
            return valorFrete * 1.08;
        } else if (regiao == Regiao.NORDESTE) {
            return valorFrete * 1.085;
        } else if (regiao == Regiao.CENTRO_OESTE) {
            return valorFrete * 1.07;
        } else if (regiao == Regiao.SUDESTE) {
            return valorFrete * 1.048;
        } else if (regiao == Regiao.SUL) {
            return valorFrete * 1.06;
        } else {
            throw new IllegalArgumentException("Região não reconhecida");
        }
    }
}
