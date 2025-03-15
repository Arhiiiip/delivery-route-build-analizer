package itmo.diploma.routebuilder.service;

import itmo.diploma.routebuilder.dto.request.ShippingRequest;
import itmo.diploma.routebuilder.dto.response.ShippingResponse;
import itmo.diploma.routebuilder.service.companyservice.DhlService;
import itmo.diploma.routebuilder.service.companyservice.FedExService;
import itmo.diploma.routebuilder.service.companyservice.SdekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class ShippingService {

    @Autowired
    private DhlService dhlService;

    @Autowired
    private FedExService fedExService;

    @Autowired
    private SdekService sdekService;

    public ShippingResponse findBestOffer(ShippingRequest request) {
        BigDecimal dhlCost = dhlService.getShippingCost(request.getOrigin(), request.getDestination(), request.getWeight(), "dhl_api_key");
        BigDecimal fedExCost = fedExService.getShippingCost(request.getOrigin(), request.getDestination(), request.getWeight(), "fedex_api_key");
        BigDecimal sdekCost = sdekService.getShippingCost(request.getOrigin(), request.getDestination(), request.getWeight(), "sdek_api_key");

        Map<String, BigDecimal> offers = new HashMap<>();
        offers.put("DHL", dhlCost);
        offers.put("FedEx", fedExCost);
        offers.put("СДЭК", sdekCost);

        String bestCarrier = Collections.min(offers.entrySet(), Map.Entry.comparingByValue()).getKey();
        BigDecimal bestCost = offers.get(bestCarrier);

        return new ShippingResponse(bestCarrier, bestCost);
    }
}