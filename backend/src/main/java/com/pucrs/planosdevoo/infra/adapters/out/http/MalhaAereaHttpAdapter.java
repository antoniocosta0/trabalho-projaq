package com.pucrs.planosdevoo.infra.adapters.out.http;

import com.pucrs.planosdevoo.domain.exceptions.MalhaAereaIndisponivelException;
import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.ports.out.MalhaAereaPort;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class MalhaAereaHttpAdapter implements MalhaAereaPort {

    private static final Logger log = LoggerFactory.getLogger(MalhaAereaHttpAdapter.class);

    private final RestClient cadastro;

    public MalhaAereaHttpAdapter(RestClient cadastroRestClient) {
        this.cadastro = cadastroRestClient;
    }

    @Override
    public Optional<Aeroporto> buscarAeroporto(String codigo) {
        return buscar("/aeroportos/{codigo}/vagas", VagasResposta.class, codigo)
                .map(v -> new Aeroporto(v.aeroporto(), v.vagas()));
    }

    @Override
    public Optional<Aerovia> buscarAerovia(String id) {
        return buscar("/aerovias/{id}", AeroviaResposta.class, id)
                .map(a -> new Aerovia(a.id(), a.origem(), a.destino(), a.distanciaKm()));
    }

    // 404 é recurso inexistente; qualquer outra falha é o cadastro indisponível
    private <T> Optional<T> buscar(String caminho, Class<T> tipo, String id) {
        try {
            return Optional.ofNullable(cadastro.get().uri(caminho, id).retrieve().body(tipo));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException | IllegalStateException e) {
            log.warn("Falha ao consultar o cadastro em {}: {}", caminho, e.getMessage());
            throw new MalhaAereaIndisponivelException("O cadastro da rede aérea está indisponível no momento");
        }
    }

    record VagasResposta(String aeroporto, List<Integer> vagas) { }

    record AeroviaResposta(String id, String origem, String destino, double distanciaKm) { }
}
