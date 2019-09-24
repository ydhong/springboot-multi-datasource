package com.imooc.controller;

import com.imooc.entity.User;
import com.imooc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/add")
    public ResponseEntity<String> add(@RequestBody User user){
        userService.add(user);
        return ResponseEntity.ok("success");
    }

    @RequestMapping("/list")
    public ResponseEntity<List<User>> list(){
        return ResponseEntity.ok(userService.findAll());
    }

}
