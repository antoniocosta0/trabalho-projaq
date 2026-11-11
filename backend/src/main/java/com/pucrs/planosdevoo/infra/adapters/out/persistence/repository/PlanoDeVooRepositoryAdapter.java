package com.pucrs.planosdevoo.infra.adapters.out.persistence.repository;

import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.Pouso;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.model.Trecho;
import com.pucrs.planosdevoo.domain.ports.out.PlanoDeVooRepository;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.PlanoDeVooEntity;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.TrechoEmbeddable;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.mapper.PlanoDeVooMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PlanoDeVooRepositoryAdapter implements PlanoDeVooRepository {

    private final PlanoDeVooJpaRepository planos;
    private final PilotoJpaRepository pilotos;
    private final AeronaveJpaRepository aeronaves;

    public PlanoDeVooRepositoryAdapter(PlanoDeVooJpaRepository planos, PilotoJpaRepository pilotos,
                                       AeronaveJpaRepository aeronaves) {
        this.planos = planos;
        this.pilotos = pilotos;
        this.aeronaves = aeronaves;
    }

    @Override
    public PlanoDeVoo salvar(PlanoDeVoo plano) {
        PlanoDeVooEntity entity = new PlanoDeVooEntity(
                plano.getId(),
                plano.getCriador(),
                pilotos.getReferenceById(plano.getPiloto().getId()),
                aeronaves.getReferenceById(plano.getAeronave().getPrefixo()),
                plano.getOrigem(),
                plano.getDestino(),
                plano.getPartida(),
                plano.getSituacao(),
                plano.getTrechos().stream().map(this::trechoParaEntidade).toList());
        PlanoDeVooEntity salvo = planos.save(entity);

        return PlanoDeVoo.reconstituir(salvo.getId(), plano.getCriador(), plano.getPiloto(), plano.getAeronave(),
                plano.getOrigem(), plano.getDestino(), plano.getPartida(), plano.getSituacao(),
                plano.getTrechos());
    }

    private TrechoEmbeddable trechoParaEntidade(Trecho t) {
        Pouso pouso = t.getPouso();
        Aerovia aerovia = t.getAerovia();
        return new TrechoEmbeddable(t.getOrdem(), aerovia.getId(), aerovia.getOrigem(), aerovia.getDestino(),
                aerovia.getDistanciaKm(), t.getAltitude(), t.getHorarioEntrada(),
                pouso == null ? null : pouso.getNumeroVaga(),
                pouso == null ? null : pouso.getPermanenciaMinutos());
    }

    @Override
    public Optional<PlanoDeVoo> buscarPorId(Long id) {
        return planos.findById(id).map(PlanoDeVooMapper::paraDominio);
    }

    @Override
    public List<PlanoDeVoo> listarPorCriador(String criador) {
        return planos.findByCriador(criador).stream().map(PlanoDeVooMapper::paraDominio).toList();
    }

    @Override
    public List<PlanoDeVoo> listarPorCriadorESituacao(String criador, SituacaoPlano situacao) {
        return planos.findByCriadorAndSituacao(criador, situacao).stream().map(PlanoDeVooMapper::paraDominio).toList();
    }
}
