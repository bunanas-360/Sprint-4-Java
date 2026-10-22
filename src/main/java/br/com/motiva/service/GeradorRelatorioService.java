package br.com.motiva.service;

import br.com.motiva.dto.RelatorioResponse;
import br.com.motiva.exception.ResourceNotFoundException;
import br.com.motiva.model.RelatorioPrioridade;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.repository.RelatorioPrioridadeRepository;
import br.com.motiva.repository.TrechoRodoviaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GeradorRelatorioService {
    // Mantidos os limites de classificação definidos no GeradorRelatorio da Sprint 3.
    private static final double LIMITE_URGENTE = 80.0;
    private static final double LIMITE_CRITICO = 50.0;
    private static final double LIMITE_ATENCAO = 30.0;
    private static final int LIMITE_RESUMO = 2000;

    private final RelatorioPrioridadeRepository relatorioRepository;
    private final TrechoRodoviaRepository trechoRepository;

    public GeradorRelatorioService(RelatorioPrioridadeRepository relatorioRepository,
                                  TrechoRodoviaRepository trechoRepository) {
        this.relatorioRepository = relatorioRepository;
        this.trechoRepository = trechoRepository;
    }

    @Transactional
    public RelatorioResponse gerarRelatorio() {
        List<TrechoRodovia> trechos = trechoRepository.findAllByOrderByKmAsc();
        int urgente = 0;
        int critico = 0;
        int atencao = 0;
        int normal = 0;
        StringBuilder resumo = new StringBuilder();

        for (TrechoRodovia trecho : trechos) {
            double crescimento = trecho.getCrescimentoVegetacao().doubleValue();
            String nivel;
            String intervencao;
            if (crescimento >= LIMITE_URGENTE) {
                urgente++;
                nivel = "URGENTE";
                intervencao = "Roçada Mecanizada";
            } else if (crescimento >= LIMITE_CRITICO) {
                critico++;
                nivel = "CRÍTICO";
                intervencao = "Roçada Manual";
            } else if (crescimento >= LIMITE_ATENCAO) {
                atencao++;
                nivel = "ATENÇÃO";
                intervencao = "Monitoramento";
            } else {
                normal++;
                nivel = "NORMAL";
                intervencao = "Sem necessidade imediata";
            }
            acrescentarResumo(resumo, String.format(
                    "KM %d: %s (vegetação %.2f%%; intervenção: %s); ",
                    trecho.getKm(), nivel, crescimento, intervencao));
        }

        if (trechos.isEmpty()) {
            acrescentarResumo(resumo, "Nenhum trecho cadastrado; relatório gerado sem itens.");
        }

        RelatorioPrioridade salvo = relatorioRepository.save(
                new RelatorioPrioridade(urgente, critico, atencao, normal, resumo.toString()));
        return paraResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<RelatorioResponse> listarHistorico() {
        return relatorioRepository.findAllByOrderByDataGeracaoDesc()
                .stream().map(this::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public RelatorioResponse buscarPorId(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("O ID deve ser positivo.");
        return relatorioRepository.findById(id).map(this::paraResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Relatório não encontrado: id=" + id));
    }

    @Transactional(readOnly = true)
    public List<RelatorioResponse> consultarPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Os parâmetros inicio e fim são obrigatórios.");
        }
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("A data inicio não pode ser posterior a fim.");
        }
        return relatorioRepository.findByDataGeracaoBetweenOrderByDataGeracaoDesc(inicio, fim)
                .stream().map(this::paraResponse).toList();
    }

    private void acrescentarResumo(StringBuilder resumo, String item) {
        if (resumo.length() + item.length() <= LIMITE_RESUMO) {
            resumo.append(item);
            return;
        }
        String sufixo = "... resumo truncado (limite da coluna RESUMO).";
        if (!resumo.toString().endsWith(sufixo)) {
            int limite = LIMITE_RESUMO - sufixo.length();
            if (resumo.length() > limite) resumo.setLength(limite);
            resumo.append(sufixo);
        }
    }

    private RelatorioResponse paraResponse(RelatorioPrioridade relatorio) {
        return new RelatorioResponse(relatorio.getId(), relatorio.getDataGeracao(),
                relatorio.getQtUrgente(), relatorio.getQtCritico(), relatorio.getQtAtencao(),
                relatorio.getQtNormal(), relatorio.getResumo());
    }
}
