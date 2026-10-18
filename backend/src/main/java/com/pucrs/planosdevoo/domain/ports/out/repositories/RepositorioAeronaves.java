package com.pucrs.planosdevoo.domain.ports.out.repositories;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.DadosAeronave;
import java.util.List;
import java.util.Optional;

public interface RepositorioAeronaves {
    List<Aeronave> listarTodas();
    Optional<Aeronave> buscarPorPrefixo(String prefixo);

    // Grava os dados do cadastro, e não a Aeronave: são eles que a tabela única
    // guarda, e mapear da subclasse de volta pediria um segundo switch por categoria.
    Aeronave salvar(DadosAeronave dados);
}
