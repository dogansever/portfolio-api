package com.sever.portfolio.external;

import com.sever.portfolio.entity.CompanyValue;

public interface BorsaGundemService {
    CompanyValue getCompanyValue(String companyCode);
}
