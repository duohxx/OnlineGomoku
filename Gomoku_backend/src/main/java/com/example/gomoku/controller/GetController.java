package com.example.gomoku.controller;

import com.example.gomoku.game.GomokuAI;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Indexed;
import org.springframework.web.bind.annotation.*;
import com.example.gomoku.game.GomokuRule;

import java.util.ArrayList;
import java.util.HashMap;
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
    public HashSet<GomokuRule.Coordinate> get(@PathVariable Integer id) {
        return Gomoku.setCoordinate(id);
    }

    @RequestMapping(value = "/undo", method = RequestMethod.GET)
    @ResponseBody
    public HashSet<GomokuRule.Cell> undo() {
        return Gomoku.undo();
    }

    @RequestMapping(value = "/type", method = RequestMethod.GET)
    @ResponseBody
    public char getType() {
        return Gomoku.getCurType();
    }

    @RequestMapping(value = "/getAllStep", method = RequestMethod.GET)
    @ResponseBody
    public HashSet<GomokuRule.Cell> getAllStep() {
        return Gomoku.getAllStep();
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public void reset() {
        Gomoku.reset();
    }

    @GetMapping(value = "/getInstance/{instance}")
    @ResponseBody
    public HashSet< HashSet<GomokuRule.Coordinate> > getInstance(@PathVariable String instance) {
        System.out.println(instance);
        return Gomoku.getArrayByInstance(instance);
    }

    @GetMapping(value = "/getScore")
    @ResponseBody
    public Integer getScore( ) {
        Integer score = Gomoku.gerScore();
        System.out.println(score);
        return score;
    }

    @GetMapping(value = "/AIplay")
    @ResponseBody
    public GomokuRule.Coordinate AIplay( ) {
        GomokuRule.Coordinate coordinate = Gomoku.ALPlay();
        System.out.println("AI think the best move is: " + coordinate);
        return coordinate;
    }
}
