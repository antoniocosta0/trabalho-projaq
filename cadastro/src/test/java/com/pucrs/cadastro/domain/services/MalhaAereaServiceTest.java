package com.pucrs.cadastro.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pucrs.cadastro.domain.exceptions.RecursoNaoEncontradoException;
import com.pucrs.cadastro.domain.exceptions.RegistroDuplicadoException;
import com.pucrs.cadastro.domain.model.Aeroporto;
import com.pucrs.cadastro.domain.model.Aerovia;
import com.pucrs.cadastro.domain.ports.out.AeroportoRepository;
import com.pucrs.cadastro.domain.ports.out.AeroviaRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MalhaAereaServiceTest {

    private MalhaAereaService malhaAerea;

    @BeforeEach
    void preparar() {
        Map<String, Aeroporto> aeroportos = new LinkedHashMap<>();
        Map<String, Aerovia> aerovias = new LinkedHashMap<>();
        malhaAerea = new MalhaAereaService(new AeroportoRepository() {
            public List<Aeroporto> listarTodos() { return new ArrayList<>(aeroportos.values()); }
            public Optional<Aeroporto> buscarPorCodigo(String codigo) {
                return Optional.ofNullable(aeroportos.get(codigo));
            }
            public Aeroporto salvar(Aeroporto a) { aeroportos.put(a.getCodigo(), a); return a; }
        }, new AeroviaRepository() {
            public List<Aerovia> listarTodas() { return new ArrayList<>(aerovias.values()); }
            public Optional<Aerovia> buscarPorId(String id) { return Optional.ofNullable(aerovias.get(id)); }
            public Aerovia salvar(Aerovia a) { aerovias.put(a.getId(), a); return a; }
        });
        malhaAerea.cadastrarAeroporto("POA", "Porto Alegre", 3);
        malhaAerea.cadastrarAeroporto("FLN", "Florianópolis", 2);
        malhaAerea.cadastrarAerovia("R1", "POA", "FLN", 363);
    }

    @Test
    void aeroportoNasceComAsVagasNumeradas() {
        assertEquals(List.of(1, 2, 3), malhaAerea.buscarAeroporto("POA").getVagas());
    }

    @Test
    void aeroviaExigeAeroportosCadastrados() {
        assertThrows(RecursoNaoEncontradoException.class, () -> malhaAerea.cadastrarAerovia("R9", "POA", "XXX", 100));
    }

    @Test
    void idRepetidoNaoSubstituiORegistro() {
        assertThrows(RegistroDuplicadoException.class, () -> malhaAerea.cadastrarAerovia("R1", "FLN", "POA", 1));
        assertThrows(RegistroDuplicadoException.class, () -> malhaAerea.cadastrarAeroporto("POA", "Outro", 9));
        assertEquals(363, malhaAerea.buscarAerovia("R1").getDistanciaKm());
    }

    @Test
    void filtraAeroviasPorOrigemEDestino() {
        malhaAerea.cadastrarAerovia("R8", "FLN", "POA", 363);

        assertEquals("R8", malhaAerea.listarAerovias("FLN", null).get(0).getId());
        assertEquals(1, malhaAerea.listarAerovias(null, "FLN").size());
    }
}
