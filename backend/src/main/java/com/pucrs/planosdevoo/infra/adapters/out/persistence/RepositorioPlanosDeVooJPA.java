package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.Pouso;
import com.pucrs.planosdevoo.domain.model.Trecho;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPlanosDeVoo;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioPlanosDeVooJPA implements RepositorioPlanosDeVoo {

    private final PlanoDeVooSpringDataRepository planos;
    private final PilotoSpringDataRepository pilotos;
    private final AeronaveSpringDataRepository aeronaves;
    private final AeroportoSpringDataRepository aeroportos;
    private final AeroviaSpringDataRepository aerovias;

    public RepositorioPlanosDeVooJPA(PlanoDeVooSpringDataRepository planos, PilotoSpringDataRepository pilotos,
                                   AeronaveSpringDataRepository aeronaves, AeroportoSpringDataRepository aeroportos,
                                   AeroviaSpringDataRepository aerovias) {
        this.planos = planos;
        this.pilotos = pilotos;
        this.aeronaves = aeronaves;
        this.aeroportos = aeroportos;
        this.aerovias = aerovias;
    }

    @Override
    public PlanoDeVoo salvar(PlanoDeVoo plano) {
        PlanoDeVooJpaEntity entity = new PlanoDeVooJpaEntity(
                plano.getId(),
                pilotos.getReferenceById(plano.getPiloto().getId()),
                aeronaves.getReferenceById(plano.getAeronave().getPrefixo()),
                aeroportos.getReferenceById(plano.getOrigem().getCodigo()),
                aeroportos.getReferenceById(plano.getDestino().getCodigo()),
                plano.getPartida(),
                plano.getSituacao(),
                plano.getTrechos().stream().map(this::trechoParaEntidade).toList());
        PlanoDeVooJpaEntity salvo = planos.save(entity);

        // O domínio já tem piloto, aeronave e aeroportos completos;
        // do banco só falta o identificador gerado.
        return PlanoDeVoo.reconstituir(salvo.getId(), plano.getPiloto(), plano.getAeronave(),
                plano.getOrigem(), plano.getDestino(), plano.getPartida(), plano.getSituacao(),
                plano.getTrechos());
    }

    private TrechoJpaEmbeddable trechoParaEntidade(Trecho t) {
        Pouso pouso = t.getPouso();
        return new TrechoJpaEmbeddable(t.getOrdem(), aerovias.getReferenceById(t.getAerovia().getId()),
                t.getAltitude(), t.getHorarioEntrada(),
                pouso == null ? null : pouso.getNumeroVaga(),
                pouso == null ? null : pouso.getPermanenciaMinutos());
    }

    @Override
    public Optional<PlanoDeVoo> buscarPorId(Long id) {
        return planos.findById(id).map(PlanoDeVooMapper::paraDominio);
    }

    @Override
    public List<PlanoDeVoo> listarTodos() {
        return planos.findAll().stream().map(PlanoDeVooMapper::paraDominio).toList();
    }

    @Override
    public List<PlanoDeVoo> listarPorSituacao(SituacaoPlano situacao) {
        return planos.findBySituacao(situacao).stream().map(PlanoDeVooMapper::paraDominio).toList();
    }
}
