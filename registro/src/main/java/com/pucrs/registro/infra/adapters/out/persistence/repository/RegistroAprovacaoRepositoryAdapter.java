package com.pucrs.registro.infra.adapters.out.persistence.repository;

import com.pucrs.registro.domain.model.RegistroAprovacao;
import com.pucrs.registro.domain.ports.out.RegistroAprovacaoRepository;
import com.pucrs.registro.infra.adapters.out.persistence.entity.RegistroAprovacaoEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RegistroAprovacaoRepositoryAdapter implements RegistroAprovacaoRepository {

    private final RegistroAprovacaoJpaRepository jpa;

    public RegistroAprovacaoRepositoryAdapter(RegistroAprovacaoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public boolean existe(String eventoId, Long planoId) {
        return jpa.existsByEventoIdOrPlanoId(eventoId, planoId);
    }

    @Override
    public void salvar(RegistroAprovacao r) {
        jpa.save(new RegistroAprovacaoEntity(r.getEventoId(), r.getPlanoId(), r.getAprovadoEm(), r.getRecebidoEm(),
                r.getConteudo()));
    }

    @Override
    public List<RegistroAprovacao> listarTodos() {
        return jpa.findAllByOrderByRecebidoEm().stream().map(RegistroAprovacaoRepositoryAdapter::paraDominio).toList();
    }

    @Override
    public Optional<RegistroAprovacao> buscarPorPlano(Long planoId) {
        return jpa.findByPlanoId(planoId).map(RegistroAprovacaoRepositoryAdapter::paraDominio);
    }

    private static RegistroAprovacao paraDominio(RegistroAprovacaoEntity e) {
        return new RegistroAprovacao(e.getEventoId(), e.getPlanoId(), e.getAprovadoEm(), e.getRecebidoEm(),
                e.getConteudo());
    }
}
