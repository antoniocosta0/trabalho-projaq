package com.pucrs.planosdevoo.infra.adapters.out.persistence.repository;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.Pouso;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.model.Trecho;
import com.pucrs.planosdevoo.domain.ports.out.PlanoDeVooRepository;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.PlanoDeVooEntity;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.TrechoEmbeddable;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.mapper.PlanoDeVooMapper;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.repository.AeronaveJpaRepository;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.repository.AeroportoJpaRepository;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.repository.AeroviaJpaRepository;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.repository.PilotoJpaRepository;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.repository.PlanoDeVooJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PlanoDeVooRepositoryAdapter implements PlanoDeVooRepository {

    private final PlanoDeVooJpaRepository planos;
    private final PilotoJpaRepository pilotos;
    private final AeronaveJpaRepository aeronaves;
    private final AeroportoJpaRepository aeroportos;
    private final AeroviaJpaRepository aerovias;

    public PlanoDeVooRepositoryAdapter(PlanoDeVooJpaRepository planos, PilotoJpaRepository pilotos,
                                   AeronaveJpaRepository aeronaves, AeroportoJpaRepository aeroportos,
                                   AeroviaJpaRepository aerovias) {
        this.planos = planos;
        this.pilotos = pilotos;
        this.aeronaves = aeronaves;
        this.aeroportos = aeroportos;
        this.aerovias = aerovias;
    }

    @Override
    public PlanoDeVoo salvar(PlanoDeVoo plano) {
        PlanoDeVooEntity entity = new PlanoDeVooEntity(
                plano.getId(),
                pilotos.getReferenceById(plano.getPiloto().getId()),
                aeronaves.getReferenceById(plano.getAeronave().getPrefixo()),
                aeroportos.getReferenceById(plano.getOrigem().getCodigo()),
                aeroportos.getReferenceById(plano.getDestino().getCodigo()),
                plano.getPartida(),
                plano.getSituacao(),
                plano.getTrechos().stream().map(this::trechoParaEntidade).toList());
        PlanoDeVooEntity salvo = planos.save(entity);

        return PlanoDeVoo.reconstituir(salvo.getId(), plano.getPiloto(), plano.getAeronave(),
                plano.getOrigem(), plano.getDestino(), plano.getPartida(), plano.getSituacao(),
                plano.getTrechos());
    }

    private TrechoEmbeddable trechoParaEntidade(Trecho t) {
        Pouso pouso = t.getPouso();
        return new TrechoEmbeddable(t.getOrdem(), aerovias.getReferenceById(t.getAerovia().getId()),
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
