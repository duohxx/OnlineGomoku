package com.example.gomoku.game;

import java.util.HashSet;

public class VirtualBroad {
    private final HashSet<GomokuRule.Cell> grid = new  HashSet<GomokuRule.Cell>();

    private HashSet< HashSet<GomokuRule.Coordinate> > FiveList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > AliveFourList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > BlockedFourList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > AliveThreeList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > BlockedThreeList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > AliveTwoList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > BlockedTwoList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > OneList = new HashSet< HashSet<GomokuRule.Coordinate> >();
}
