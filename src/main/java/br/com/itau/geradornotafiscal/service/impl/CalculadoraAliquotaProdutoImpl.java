package br.com.itau.geradornotafiscal.service.impl;


import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.service.CalculadoraAliquotaProduto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalculadoraAliquotaProdutoImpl implements CalculadoraAliquotaProduto {

    @Override
    public List<ItemNotaFiscal> calcularAliquota(Pedido pedido) {
        Destinatario destinatario = pedido.getDestinatario();
        TipoPessoa tipoPessoa = destinatario.getTipoPessoa();
        double valorTotalItens = pedido.getValorTotalItens();
        double aliquota;

        if (tipoPessoa == TipoPessoa.FISICA) {
            aliquota = calcularAliquotaFisica(valorTotalItens);
        } else {
            RegimeTributacaoPJ regimeTributacao = destinatario.getRegimeTributacao();
            aliquota = calcularAliquotaJuridica(valorTotalItens, regimeTributacao);
        }

        return pedido.getItens().stream()
                .map(item -> new ItemNotaFiscal(item.getIdItem(), item.getDescricao(), item.getValorUnitario(), item.getQuantidade(), aliquota))
                .collect(Collectors.toList());
    }

    private double calcularAliquotaFisica(double valorTotalItens) {
        if (valorTotalItens < 500) {
            return 0;
        } else if (valorTotalItens <= 2000) {
            return 0.12;
        } else if (valorTotalItens <= 3500) {
            return 0.15;
        } else {
            return 0.17;
        }
    }

    private double calcularAliquotaJuridica(double valorTotalItens, RegimeTributacaoPJ regimeTributacao) {
        switch (regimeTributacao) {
            case SIMPLES_NACIONAL:
                return calcularAliquotaSimplesNacional(valorTotalItens);
            case LUCRO_REAL:
                return calcularAliquotaLucroReal(valorTotalItens);
            case LUCRO_PRESUMIDO:
                return calcularAliquotaLucroPresumido(valorTotalItens);
            default:
                throw new IllegalArgumentException("Regime de tributação não reconhecido");
        }
    }

    private double calcularAliquotaSimplesNacional(double valorTotalItens) {
        if (valorTotalItens < 1000) {
            return 0.03;
        } else if (valorTotalItens <= 2000) {
            return 0.07;
        } else if (valorTotalItens <= 5000) {
            return 0.13;
        } else {
            return 0.19;
        }
    }

    private double calcularAliquotaLucroReal(double valorTotalItens) {
        if (valorTotalItens < 1000) {
            return 0.03;
        } else if (valorTotalItens <= 2000) {
            return 0.09;
        } else if (valorTotalItens <= 5000) {
            return 0.15;
        } else {
            return 0.20;
        }
    }

    private double calcularAliquotaLucroPresumido(double valorTotalItens) {
        if (valorTotalItens < 1000) {
            return 0.03;
        } else if (valorTotalItens <= 2000) {
            return 0.09;
        } else if (valorTotalItens <= 5000) {
            return 0.16;
        } else {
            return 0.20;
        }
    }
}
