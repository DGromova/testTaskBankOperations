package com.bank.service;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.bank.models.UserDocument;
import com.bank.models.UserSearchParameters;

public interface UserElasticsearchService {
    SearchResponse<UserDocument> findUsers(UserSearchParameters userSearchParameters, int page, int size, String sortField, String direction);
}
