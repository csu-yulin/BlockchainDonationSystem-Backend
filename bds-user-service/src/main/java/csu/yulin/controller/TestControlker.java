package csu.yulin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lp
 * @create 2024-12-19-20:23
 */
@RestController
@RequestMapping("/test")
public class TestControlker {
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
