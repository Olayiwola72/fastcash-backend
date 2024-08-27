package com.fastcash.moneytransfer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FallbackController {

    // This will forward any request that doesn't contain a file extension and is not in the /assets/ & /swagger-ui/ folder to index.html
	@RequestMapping(value = "/{path:^(?!assets|swagger-ui|.*\\..*).*$}/**")
    public String fallback() {
        return "forward:/index.html";
    }
    
}