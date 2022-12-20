package com.example.gomoku.controller;

import com.example.gomoku.game.GomokuAI;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.gomoku.game.GomokuRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

@Controller
@CrossOrigin
@RequestMapping("/gomoku")
public class GetController {
    private GomokuRule Gomoku = new GomokuRule();
    GetController(){

    }
    @GetMapping(value = "/set/{id}")
    @ResponseBody
    public HashSet<GomokuRule.Cell> get(@PathVariable Integer id) {
        return Gomoku.setCoordinate(id);
    }

    @RequestMapping(value = "/undo", method = RequestMethod.GET)
    @ResponseBody
    public String undo() {
        return Gomoku.undo();
    }

    @RequestMapping(value = "/type", method = RequestMethod.GET)
    @ResponseBody
    public char getType() {
        return Gomoku.getCurType();
    }

    @RequestMapping(value = "/getAllStep", method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<GomokuRule.Cell> getAllStep() {
        return Gomoku.getAllStep();
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public void reset() {
        Gomoku.reset();
    }

    @RequestMapping(value = "/GomokuAliveFour", method = RequestMethod.GET)
    @ResponseBody
    public HashSet< HashSet<GomokuRule.Cell> > GomokuEstimate() {
        return Gomoku.getAliveFour();
    }


}
