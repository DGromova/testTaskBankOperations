package com.bank.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String serverUrl;

    @Bean
    public ElasticsearchClient esClient() {
      RestClient restClient = RestClient
              .builder(HttpHost.create(serverUrl))
              .build();

      ElasticsearchTransport transport = new RestClientTransport(
              restClient, new JacksonJsonpMapper()
      );

        log.debug("Initializing an embedded Elasticsearch client");
        return new ElasticsearchClient(transport);
    }


}
