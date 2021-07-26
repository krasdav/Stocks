package org.dav.equitylookup.dto;

import lombok.Data;
import org.dav.equitylookup.model.Stock;
import org.dav.equitylookup.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserDTO {

    private Long id;
    @NotBlank
    @Size(min = 4, max =20)
    private String userName;
    private BigDecimal portfolio = new BigDecimal("0");
    private List<Stock> stocks = new ArrayList<>();

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.userName = user.getUsername();
        this.portfolio = BigDecimal.valueOf(user.getPortfolio().doubleValue());
        this.stocks.addAll(user.getStocks());
    }

    public String getUsername() {
        return userName;
    }
}
