package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
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

    public RepositorioPlanosDeVooJPA(PlanoDeVooSpringDataRepository planos, PilotoSpringDataRepository pilotos,
                                   AeronaveSpringDataRepository aeronaves, AeroportoSpringDataRepository aeroportos) {
        this.planos = planos;
        this.pilotos = pilotos;
        this.aeronaves = aeronaves;
        this.aeroportos = aeroportos;
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
                plano.getSituacao());
        PlanoDeVooJpaEntity salvo = planos.save(entity);

        // O domínio já tem piloto, aeronave e aeroportos completos;
        // do banco só falta o identificador gerado.
        return PlanoDeVoo.reconstituir(salvo.getId(), plano.getPiloto(), plano.getAeronave(),
                plano.getOrigem(), plano.getDestino(), plano.getPartida(), plano.getSituacao());
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
