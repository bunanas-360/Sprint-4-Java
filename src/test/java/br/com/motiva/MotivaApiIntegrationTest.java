package br.com.motiva;

import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.repository.EquipeManutencaoRepository;
import br.com.motiva.repository.IntervencaoOperacionalRepository;
import br.com.motiva.repository.RelatorioPrioridadeRepository;
import br.com.motiva.repository.TrechoRodoviaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MotivaApiIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private TrechoRodoviaRepository trechoRepository;
    @Autowired private EquipeManutencaoRepository equipeRepository;
    @Autowired private IntervencaoOperacionalRepository intervencaoRepository;
    @Autowired private RelatorioPrioridadeRepository relatorioRepository;

    @BeforeEach
    void limparDadosDeTeste() {
        // Sem relacionamentos entre estas tabelas, a ordem também deixa o teste explícito.
        relatorioRepository.deleteAll();
        intervencaoRepository.deleteAll();
        equipeRepository.deleteAll();
        trechoRepository.deleteAll();
    }

    @Test
    void crudDeTrechoRespondeComCodigosHttpCorretos() throws Exception {
        MvcResult criacao = mockMvc.perform(post("/api/trechos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"km": 12, "crescimentoVegetacao": 55.0, "tipoTrecho": "umido"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.km").value(12))
                .andReturn();

        long id = extrairId(criacao.getResponse().getContentAsString());
        mockMvc.perform(get("/api/trechos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoTrecho").value("umido"));

        mockMvc.perform(put("/api/trechos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"km": 12, "crescimentoVegetacao": 20.0, "tipoTrecho": "seco"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.crescimentoVegetacao").value(20.0))
                .andExpect(jsonPath("$.tipoTrecho").value("seco"));

        mockMvc.perform(get("/api/trechos")).andExpect(status().isOk());
        mockMvc.perform(delete("/api/trechos/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/trechos/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void rejeitaCrescimentoVegetacaoNegativo() throws Exception {
        mockMvc.perform(post("/api/trechos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"km": 12, "crescimentoVegetacao": -1.0, "tipoTrecho": "umido"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void crudDeEquipeRespondeComCodigosHttpCorretos() throws Exception {
        MvcResult criacao = mockMvc.perform(post("/api/equipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nomeEquipe":"Equipe Norte","responsavel":"Carlos Silva"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        long id = extrairId(criacao.getResponse().getContentAsString());

        mockMvc.perform(get("/api/equipes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeEquipe").value("Equipe Norte"));
        mockMvc.perform(put("/api/equipes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nomeEquipe":"Equipe Sul","responsavel":"Ana Souza"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeEquipe").value("Equipe Sul"));
        mockMvc.perform(get("/api/equipes")).andExpect(status().isOk());
        mockMvc.perform(delete("/api/equipes/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/equipes/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void crudDeIntervencaoRespondeComCodigosHttpCorretos() throws Exception {
        MvcResult criacao = mockMvc.perform(post("/api/intervencoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo":"Rocada Mecanizada","descricao":"Atendimento de trecho critico"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        long id = extrairId(criacao.getResponse().getContentAsString());

        mockMvc.perform(get("/api/intervencoes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Rocada Mecanizada"));
        mockMvc.perform(put("/api/intervencoes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo":"Pulverizacao","descricao":"Controle de vegetacao"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Pulverizacao"));
        mockMvc.perform(get("/api/intervencoes")).andExpect(status().isOk());
        mockMvc.perform(delete("/api/intervencoes/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/intervencoes/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void geraEConsultaRelatorioPersistido() throws Exception {
        trechoRepository.save(new TrechoRodovia(10, 85.0, "umido"));
        trechoRepository.save(new TrechoRodovia(20, 55.0, "seco"));
        trechoRepository.save(new TrechoRodovia(30, 35.0, "umido"));
        trechoRepository.save(new TrechoRodovia(40, 10.0, "seco"));

        mockMvc.perform(post("/api/relatorios"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.qtUrgente").value(1))
                .andExpect(jsonPath("$.qtCritico").value(1))
                .andExpect(jsonPath("$.qtAtencao").value(1))
                .andExpect(jsonPath("$.qtNormal").value(1));

        mockMvc.perform(get("/api/relatorios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/relatorios/periodo")
                        .param("inicio", "2000-01-01T00:00:00")
                        .param("fim", "2099-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void rejeitaPeriodoComInicioDepoisDoFim() throws Exception {
        mockMvc.perform(get("/api/relatorios/periodo")
                        .param("inicio", "2026-10-22T12:00:00")
                        .param("fim", "2026-10-21T12:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    private long extrairId(String json) {
        Matcher matcher = Pattern.compile("\\\"id\\\"\\s*:\\s*(\\d+)").matcher(json);
        if (!matcher.find()) {
            throw new AssertionError("A resposta não contém o campo id: " + json);
        }
        return Long.parseLong(matcher.group(1));
    }
}
