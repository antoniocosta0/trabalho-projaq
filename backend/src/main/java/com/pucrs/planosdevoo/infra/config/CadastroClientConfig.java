package com.pucrs.planosdevoo.infra.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class CadastroClientConfig {

    // o balanceamento fica só neste cliente: o RestClient.Builder global é usado pelo próprio Eureka
    @Bean
    public RestClient cadastroRestClient(LoadBalancerClient balanceador,
                                         @Value("${cadastro.url:http://cadastro}") String url,
                                         @Value("${cadastro.timeout.conexao:2s}") Duration conexao,
                                         @Value("${cadastro.timeout.leitura:3s}") Duration leitura) {
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(conexao);
        fabrica.setReadTimeout(leitura);
        return RestClient.builder()
                .baseUrl(url)
                .requestFactory(fabrica)
                .requestInterceptor(new LoadBalancerInterceptor(balanceador))
                .build();
    }
}
