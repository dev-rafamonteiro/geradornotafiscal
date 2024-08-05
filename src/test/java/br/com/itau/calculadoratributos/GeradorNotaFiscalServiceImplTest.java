package br.com.itau.calculadoratributos;

import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.service.*;
import br.com.itau.geradornotafiscal.service.impl.GeradorNotaFiscalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GeradorNotaFiscalServiceImplTest {


    @Mock
    private CalculadoraAliquotaProduto calculadoraAliquotaProduto;

    @Mock
    private FreteService freteService;

    @Mock
    private EstoqueService estoqueService;

    @Mock
    private RegistroService registroService;

    @Mock
    private EntregaService entregaService;

    @Mock
    private FinanceiroService financeiroService;

    @InjectMocks
    private GeradorNotaFiscalServiceImpl geradorNotaFiscalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void gerarNotaFiscal() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setDestinatario(new Destinatario());
        pedido.setItens(Arrays.asList(new Item()));
        pedido.setValorFrete(100.0);

        List<ItemNotaFiscal> itemNotaFiscalList = Arrays.asList(new ItemNotaFiscal());
        when(calculadoraAliquotaProduto.calcularAliquota(pedido)).thenReturn(itemNotaFiscalList);
        when(freteService.calcularFrete(pedido)).thenReturn(108.0);

        double valorTotalItens = itemNotaFiscalList.stream()
                .mapToDouble(item -> item.getQuantidade() * item.getValorUnitario() + item.getValorTributoItem())
                .sum();

        // Act
        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido);

        // Assert
        assertEquals(valorTotalItens, notaFiscal.getValorTotalItens());
        assertEquals(108.0, notaFiscal.getValorFrete());
        verify(estoqueService, times(1)).enviarNotaFiscalParaBaixaEstoque(notaFiscal);
        verify(registroService, times(1)).registrarNotaFiscal(notaFiscal);
        verify(entregaService, times(1)).agendarEntrega(notaFiscal);
        verify(financeiroService, times(1)).enviarNotaFiscalParaContasReceber(notaFiscal);
    }

}