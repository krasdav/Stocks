package org.dav.equitylookup.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Share {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDateTime timeBought;
    private String ticker;
    private String company;
    @ManyToOne(fetch = FetchType.LAZY)
    private Portfolio portfolio;
    private BigDecimal boughtPrice;

    public Share() {
    }

    public Share(BigDecimal boughtPrice, Stock stock, User user) {
        this.ticker = stock.getTicker();
        this.company = stock.getCompany();
        this.timeBought = LocalDateTime.now();
        this.boughtPrice = boughtPrice;
        this.portfolio = user.getPortfolio();
    }
}
