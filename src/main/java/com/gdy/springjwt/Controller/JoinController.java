package com.gdy.springjwt.Controller;

import com.gdy.springjwt.DTO.JoinDTO;
import com.gdy.springjwt.Service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody //api형태로 받기때문에 ResponseBody를 사용
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;



    @PostMapping("/join")
    public String join(JoinDTO joinDTO){
        joinService.joinProcess(joinDTO);
        return "ok";
    }
}
