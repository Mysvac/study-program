package com.mythovac.rpcclient;


import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/")
public class ClientController {

    private final IService service;

    public ClientController(IService service) {
        this.service = service;
    }

    @CrossOrigin(origins = "http://localhost:18080", methods = RequestMethod.GET)
    @GetMapping("/fib")
    public String fib(@RequestParam("n") int n) {
        return service.fib(n);
    }

    @CrossOrigin(origins = "http://localhost:18080", methods = RequestMethod.POST)
    @PostMapping("/sort")
    public String sort(@RequestBody int[] array) {
        return service.sort(array);
    }

}
