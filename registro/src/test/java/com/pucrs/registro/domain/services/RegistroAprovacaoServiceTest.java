package com.pucrs.registro.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pucrs.registro.domain.model.RegistroAprovacao;
import com.pucrs.registro.domain.ports.out.RegistroAprovacaoRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class RegistroAprovacaoServiceTest {

    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 18, 12, 0);

    private final List<RegistroAprovacao> gravados = new ArrayList<>();
    private final RegistroAprovacaoService servico = new RegistroAprovacaoService(new RegistroAprovacaoRepository() {
        public boolean existe(String eventoId, Long planoId) {
            return gravados.stream().anyMatch(r -> r.getEventoId().equals(eventoId) || r.getPlanoId().equals(planoId));
        }
        public void salvar(RegistroAprovacao registro) { gravados.add(registro); }
        public List<RegistroAprovacao> listarTodos() { return gravados; }
        public Optional<RegistroAprovacao> buscarPorPlano(Long planoId) {
            return gravados.stream().filter(r -> r.getPlanoId().equals(planoId)).findFirst();
        }
    });

    @Test
    void mesmaAprovacaoRecebidaDuasVezesGeraUmRegistro() {
        assertTrue(servico.registrar(registro("evento-1", 7L)));
        assertFalse(servico.registrar(registro("evento-1", 7L)));

        assertEquals(1, servico.listar().size());
    }

    @Test
    void planoJaRegistradoComOutroEventoNaoDuplica() {
        servico.registrar(registro("evento-1", 7L));

        assertFalse(servico.registrar(registro("evento-2", 7L)));
        assertEquals("evento-1", servico.buscarPorPlano(7L).getEventoId());
    }

    private static RegistroAprovacao registro(String eventoId, Long planoId) {
        return new RegistroAprovacao(eventoId, planoId, AGORA, AGORA, "{\"eventoId\":\"" + eventoId + "\"}");
    }
}
