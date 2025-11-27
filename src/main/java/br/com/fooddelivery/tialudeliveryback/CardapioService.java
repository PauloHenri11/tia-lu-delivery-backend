package com.tialu.api.service;

import com.tialu.api.entity.Cardapio;
import com.tialu.api.repository.CardapioRepository;
import com.tialu.api.exception.BusinessRuleException; // Exceção customizada
import com.tialu.api.exception.ResourceNotFoundException; // Exceção customizada (gera 404)
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardapioService {

    private final CardapioRepository cardapioRepository;

    public CardapioService(CardapioRepository cardapioRepository) {
        this.cardapioRepository = cardapioRepository;
    }

    @Transactional
    public void excluirCardapio(Long cardapioId, Long merchantId) {
        // CA-002: Valida se o cardápio existe E pertence ao merchant logado
        Cardapio cardapio = cardapioRepository.findByIdAndMerchantId(cardapioId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Cardápio não encontrado ou acesso não autorizado.")); // Atende CA-007

        // CA-005: Regra de negócio - Impedir exclusão se for o único
        long totalCardapios = cardapioRepository.countByMerchantId(merchantId);
        if (totalCardapios <= 1) {
            throw new BusinessRuleException("Não é permitido excluir o único cardápio ativo do estabelecimento.");
        }

        // Se passou pelas validações, deleta.
        // O banco cuidará dos filhos graças à configuração da Pessoa 3 (Cascade)
        cardapioRepository.delete(cardapio);
    }
}