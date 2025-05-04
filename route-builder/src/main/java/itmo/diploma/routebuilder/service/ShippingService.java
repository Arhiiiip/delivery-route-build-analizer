package itmo.diploma.routebuilder.service;

import itmo.diploma.routebuilder.dto.request.ShippingRequest;
import itmo.diploma.routebuilder.dto.response.ShippingResponse;
import itmo.diploma.routebuilder.service.companyservice.DhlService;
import itmo.diploma.routebuilder.service.companyservice.FedExService;
import itmo.diploma.routebuilder.service.companyservice.SdekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShippingService {

    @Autowired
    private DhlService dhlService;

    @Autowired
    private FedExService fedExService;

    @Autowired
    private SdekService sdekService;

    public List<ShippingResponse> findAllOffers(ShippingRequest request) {
        List<ShippingResponse> dhlResponse = dhlService.getShippingCost(request.getCountyFrom(), request.getCountyTo(), request.getWeight(), "dhl_api_key");
        List<ShippingResponse> fedExResponse = fedExService.getShippingCost(request.getCountyFrom(), request.getCountyTo(), request.getWeight(), "fedex_api_key");
        List<ShippingResponse> sdekResponse = sdekService.getShippingCost(request.getCountyFrom(), request.getCountyTo(), request.getWeight(), "sdek_api_key");

        List<ShippingResponse> offers = new ArrayList<>();
        offers.addAll(dhlResponse);
        offers.addAll(fedExResponse);
        offers.addAll(sdekResponse);

        return offers;
    }
}