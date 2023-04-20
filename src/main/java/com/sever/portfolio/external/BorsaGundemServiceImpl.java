package com.sever.portfolio.external;

import com.sever.portfolio.entity.PortfolioItemValue;
import com.sever.portfolio.util.ExceptionUtil;
import com.sever.portfolio.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.SocketException;
import java.net.SocketTimeoutException;

@Slf4j
@Component("BorsaGundemService")
public class BorsaGundemServiceImpl implements BorsaGundemService {

    public static final String URL_BORSAGUNDEM = "https://www.borsagundem.com/piyasa-ekrani/hisse-detay/@companyCode";

    public Document query(String companyCode) {
        String host = URL_BORSAGUNDEM;
        host = host.replaceAll("@companyCode", companyCode);
        Document doc = null;

        int retry = 3;
        while (retry > 0) {
            try {
                doc = Jsoup.connect(host).validateTLSCertificates(false).get();
                break;
            } catch (SocketException | SocketTimeoutException | HttpStatusException e) {
                log.info("{}:{}:{}:{}:{}", companyCode, e.getMessage(), "retrying...", retry, host);
                retry--;
            } catch (Exception e) {
                log.error("{}:{}:{}", companyCode, "Financial is NOT retrieved.", ExceptionUtil.convertStackTraceToString(e));
                return null;
            }
        }
        return doc;
    }

    @Override
    public PortfolioItemValue get(String companyCode) {
        Document document = query(companyCode);

        Elements div = document.select("div[class=hisdtl] ul li[class=c1] span");
        String financialValue = div.get(0).childNodeSize() != 0 ? div.get(0).childNode(1).toString() : "";
        PortfolioItemValue portfolioItemValue = new PortfolioItemValue();
        portfolioItemValue.setCurrentPrice(NumberUtil.valueOfDouble(financialValue));

        div = document.select("div[class=hisdtl] ul li[class=c2] span");
        financialValue = div.get(0).childNodeSize() != 0 ? div.get(0).childNode(0).toString().replace("%", "").trim() : "";
        portfolioItemValue.setDailyPriceChangePercentage(NumberUtil.valueOfDouble(financialValue));

        return portfolioItemValue;
    }
}