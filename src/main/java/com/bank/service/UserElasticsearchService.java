package com.bank.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.bank.dto.FilterParams;
import com.bank.mapper.UserMapper;
import com.bank.models.UserDocument;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class UserElasticsearchService {

    private final UserMapper userMapper;

    @Value("${spring.elasticsearch.uris}")
    private final String serverUrl;

    RestClient restClient = RestClient
            .builder(HttpHost.create(serverUrl))
            .build();

    ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper());

    ElasticsearchClient esClient = new ElasticsearchClient(transport);

    @SneakyThrows
    public SearchResponse<UserDocument> findUsers(FilterParams filterParams, int page, int size, String sortField, String direction) {

        Query byBirthdate;
        Query byFullName;
        Query byPhone;
        Query byEmail;

        if (filterParams.getBirthdate() != null) {
            LocalDate birthdate = userMapper.convertDateFormat(filterParams.getBirthdate());
            byBirthdate = DateRangeQuery.of(r -> r
                            .field("birthdate")
                            .gt(String.valueOf(birthdate))
            )._toRangeQuery()._toQuery();
        } else {
            byBirthdate = null;
        }

        if (filterParams.getFullName() != null) {
            String fullName = filterParams.getFullName();
            byFullName = WildcardQuery.of(w -> w
                    .field("fullName")
                    .value(fullName + "*")
                    .caseInsensitive(true)
            )._toQuery();
        } else {
            byFullName = null;
        }

        if (filterParams.getPhone() != null) {
            String phone = userMapper.convertPhoneFormat(filterParams.getPhone());
            byPhone = TermQuery.of(t -> t
                    .field("phone")
                    .value(phone)
            )._toQuery();
        } else {
            byPhone = null;
        }

        if (filterParams.getEmail() != null) {
            String email = filterParams.getEmail().toLowerCase();
            byEmail = TermQuery.of(t -> t
                    .field("email")
                    .value(email)
            )._toQuery();
        } else {
            byEmail = null;
        }

        SearchResponse<UserDocument> response = esClient.search(s -> s
                        .index("users")
                        .query(q -> q
                                .bool(b -> b
                                        .must(filterParams.getBirthdate() != null ? byBirthdate : Query.of(q2 -> q2.matchAll(m -> m)))
                                        .must(filterParams.getFullName() != null ? byFullName : Query.of(q2 -> q2.matchAll(m -> m)))
                                        .must(filterParams.getPhone() != null ? byPhone : Query.of(q2 -> q2.matchAll(m -> m)))
                                        .must(filterParams.getEmail() != null ? byEmail : Query.of(q2 -> q2.matchAll(m -> m)))
                                )
                        ).size(size)
                        .from(page)
                        .sort(sort -> sort
                                .field(field -> field
                                        .field(sortField)
                                        .order(direction.equalsIgnoreCase("asc") ?
                                                SortOrder.Asc : SortOrder.Desc)
                                )
                        )
                , UserDocument.class
        );
        esClient.close();

        return response;
    }

}
