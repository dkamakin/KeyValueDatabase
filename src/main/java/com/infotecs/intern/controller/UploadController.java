package com.infotecs.intern.controller;

import com.infotecs.intern.model.Pair;
import com.infotecs.intern.model.service.PairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@RestController
@RequestMapping("/upload")
public class UploadController {

    private final PairService pairService;

    @Autowired
    public UploadController(PairService pairService) {
        this.pairService = pairService;
    }

    @GetMapping
    public ModelAndView getUploadPage() {
        return new ModelAndView("upload");
    }

    @GetMapping("/dump")
    public Resource dump() {
        return pairService.dump();
    }

    @GetMapping("/list")
    public Iterable<Pair> getAllPairs() {
        return pairService.getPairs();
    }

    @GetMapping("/value/{key}")
    public String getValueByKey(@PathVariable String key) {
        return pairService.getValueByKey(key);
    }

    @PostMapping
    public ModelAndView setValue(@RequestParam String key,
                                 @RequestParam String value,
                                 @RequestParam(defaultValue = "60") Integer ttl) {
        ModelAndView modelAndView = new ModelAndView("redirect:/");

        if (pairService.savePair(key, value, ttl)) {
            modelAndView.setStatus(HttpStatus.OK);
        } else {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }

        return modelAndView;
    }

    @GetMapping("/pair/{key}")
    public String getPairByKey(@PathVariable String key) {
        Optional<Pair> pair = pairService.getPairByKey(key);
        return pair.isPresent() ? pair.get().toString() : "null";
    }

    @DeleteMapping("/{key}")
    public String deleteMapping(@PathVariable String key) {
        return pairService.deleteValueByKey(key);
    }

}
