package org.dav.equitylookup.controller;

import lombok.RequiredArgsConstructor;
import org.dav.equitylookup.model.Portfolio;
import org.dav.equitylookup.model.User;
import org.dav.equitylookup.model.dto.StockDTO;
import org.dav.equitylookup.model.form.CoinForm;
import org.dav.equitylookup.model.form.StockForm;
import org.dav.equitylookup.model.form.UserRegistrationForm;
import org.dav.equitylookup.service.CryptoService;
import org.dav.equitylookup.service.PortfolioService;
import org.dav.equitylookup.service.StockService;
import org.dav.equitylookup.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final UserService userService;

    private final ModelMapper modelMapper;

    private final PortfolioService portfolioService;

    private final StockService stockService;

    private final PasswordEncoder passwordEncoder;

    private final CryptoService cryptoService;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/index")
    public String frontPage(Model model) throws IOException {
        model.addAttribute("coin", new CoinForm());
        model.addAttribute("stock", new StockForm());
        return "index";
    }

    @PostMapping("/crypto")
    public String getCryptoPrice(@ModelAttribute("coin") CoinForm coinForm, RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("coinPrice", cryptoService.getCoinPrice(coinForm.getSymbol() + "USDT"));
        redirectAttrs.addFlashAttribute("coinSymbol", coinForm.getSymbol());
        return "redirect:/index";
    }

    @PostMapping("/stock")
    public String getStockPrice(@ModelAttribute("stock") StockForm stockForm, RedirectAttributes redirectAttrs) throws IOException {
        redirectAttrs.addFlashAttribute("stockPrice", stockService.getStock(stockForm.getTicker()).getCurrentPrice());
        redirectAttrs.addFlashAttribute("stockTicker", stockForm.getTicker());
        return "redirect:/index";
    }

    @GetMapping("/registration")
    public String saveUser(Model model) {
        model.addAttribute("user", new UserRegistrationForm());
        return "registration-form";
    }

    @PostMapping("/registration")
    public String saveUser(@Valid @ModelAttribute("user") UserRegistrationForm userRegistrationForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration-form";
        } else if (userService.getUserByUsername(userRegistrationForm.getUsername()) != null &&
                userService.findByEmail(userRegistrationForm.getEmail()) != null) {
            bindingResult.rejectValue("username", "user.username", "User with this email and username already exists");
            return "registration-form";
        }
        User user = new User(userRegistrationForm.getUsername());
        user.setPassword(passwordEncoder.encode(userRegistrationForm.getPassword()));
        user.setRole("ROLE_USER");
        user.setEmail(userRegistrationForm.getEmail());
        userService.saveUser(user);
        Portfolio portfolio = new Portfolio(userRegistrationForm.getPortfolioName(), user);
        portfolioService.savePortfolio(portfolio);
        user.setPortfolio(portfolio);

        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        return "registration-success";
    }

    @GetMapping("/user")
    public String welcomePage(Model model, Principal user) {
        model.addAttribute("username", user.getName());
        return "user-welcome";
    }
}
