package pl.konradoldakowski.libraryapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "fine")
public class FineProperties {
    private BigDecimal dailyFee;
    private BigDecimal dailyInterestRate;

    public BigDecimal getDailyFee() {
        return dailyFee;
    }

    public void setDailyFee(BigDecimal dailyFee) {
        this.dailyFee = dailyFee;
    }

    public BigDecimal getDailyInterestRate() {
        return dailyInterestRate;
    }

    public void setDailyInterestRate(BigDecimal dailyInterestRate) {
        this.dailyInterestRate = dailyInterestRate;
    }
}
