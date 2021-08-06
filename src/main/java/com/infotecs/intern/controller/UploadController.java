package com.infotecs.intern.controller;

import com.infotecs.intern.model.Pair;
import com.infotecs.intern.model.service.PairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/upload")
public class UploadController {

    private final PairService pairService;

    @Autowired
    public UploadController(PairService pairService) {
        this.pairService = pairService;
    }

    @GetMapping("/{key}")
    public String getValueByKey(@PathVariable String key) {
        return pairService.getValueByKey(key);
    }

    @PostMapping
    public void setValue(@RequestParam String key,
                         @RequestParam String value,
                         @RequestParam(defaultValue = "60") Integer ttl) {
        pairService.savePair(key, value, ttl);
    }

    @GetMapping
    public String getPairByKey(@RequestParam String key) {
        Optional<Pair> pair = pairService.getPairByKey(key);
        return pair.isPresent() ? pair.get().toString() : "null";
    }

    @DeleteMapping
    public void deleteMapping(@RequestParam String key) {
        pairService.deleteValueByKey(key);
    }

}
