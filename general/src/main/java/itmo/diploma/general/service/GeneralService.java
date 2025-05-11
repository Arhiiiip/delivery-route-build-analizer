package itmo.diploma.general.service;

import itmo.diploma.general.dto.request.SearchRequest;
import itmo.diploma.general.dto.response.ProductResponse;
import java.io.IOException;

public interface GeneralService {
    String getApiKey(String companyName) throws IOException, InterruptedException;
    Long createUser(String login, String apiKey) throws IOException, InterruptedException;
    ProductResponse search(SearchRequest request) throws Exception;
}
