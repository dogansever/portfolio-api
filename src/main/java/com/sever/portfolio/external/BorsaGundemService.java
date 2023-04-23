package com.sever.portfolio.external;

import com.sever.portfolio.entity.PortfolioItemValue;

public interface BorsaGundemService {
    PortfolioItemValue getCompanyValue(String companyCode);
}
