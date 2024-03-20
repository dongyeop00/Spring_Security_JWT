package com.gdy.springjwt.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody //웹 서버로 동작하지 않고 api서버로 동작하기 때문에 특정한 객체 데이터를 응답하기에 @ResponseBody를 선언
public class MainController {

    @GetMapping("/")
    public String main(){
        return "main Contrller";
    }
}
