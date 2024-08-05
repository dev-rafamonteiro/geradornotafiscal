package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GeradorNotaFiscalServiceImpl implements GeradorNotaFiscalService {

    private final CalculadoraAliquotaProduto calculadoraAliquotaProduto;
    private final FreteService freteService;
    private final EstoqueService estoqueService;
    private final RegistroService registroService;
    private final EntregaService entregaService;
    private final FinanceiroService financeiroService;

    @Autowired
    public GeradorNotaFiscalServiceImpl(
            CalculadoraAliquotaProduto calculadoraAliquotaProduto,
            FreteService freteService,
            EstoqueService estoqueService,
            RegistroService registroService,
            EntregaService entregaService,
            FinanceiroService financeiroService) {
        this.calculadoraAliquotaProduto = calculadoraAliquotaProduto;
        this.freteService = freteService;
        this.estoqueService = estoqueService;
        this.registroService = registroService;
        this.entregaService = entregaService;
        this.financeiroService = financeiroService;
    }

    @Override
    public NotaFiscal gerarNotaFiscal(Pedido pedido) {
        // Calcular a alíquota e os itens da nota fiscal
        List<ItemNotaFiscal> itemNotaFiscalList = calculadoraAliquotaProduto.calcularAliquota(pedido);

        // Calcular o valor do frete com percentual
        double valorFreteComPercentual = freteService.calcularFrete(pedido);

        // Calcular o valor total dos itens da nota fiscal
        double valorTotalItens = itemNotaFiscalList.stream()
                .mapToDouble(item -> item.getQuantidade() * item.getValorUnitario() + item.getValorTributoItem())
                .sum();

        // Criar o objeto NotaFiscal
        String idNotaFiscal = UUID.randomUUID().toString();
        NotaFiscal notaFiscal = NotaFiscal.builder()
                .idNotaFiscal(idNotaFiscal)
                .data(LocalDateTime.now())
                .valorTotalItens(valorTotalItens)
                .valorFrete(valorFreteComPercentual)
                .itens(itemNotaFiscalList)
                .destinatario(pedido.getDestinatario())
                .build();

        // Realizar operações com a nota fiscal
        estoqueService.enviarNotaFiscalParaBaixaEstoque(notaFiscal);
        registroService.registrarNotaFiscal(notaFiscal);
        entregaService.agendarEntrega(notaFiscal);
        financeiroService.enviarNotaFiscalParaContasReceber(notaFiscal);

        return notaFiscal;
    }
}
