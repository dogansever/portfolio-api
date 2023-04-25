package com.sever.portfolio.external;

import com.sever.portfolio.entity.CompanyValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BorsaGundemServiceImplTest {

    @InjectMocks
    BorsaGundemServiceImpl borsaGundemService;

    @Test
    void getCompanyValue() {
        CompanyValue companyValue = borsaGundemService.getCompanyValue("INVEO");

        Assertions.assertNotNull(companyValue.getCurrentPrice());
        Assertions.assertNotNull(companyValue.getDailyPriceChangePercentage());
    }
}