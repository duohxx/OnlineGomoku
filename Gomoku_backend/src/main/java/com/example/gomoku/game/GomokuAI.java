package com.example.gomoku.game;

import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

// 1. traverse all cell that close the existing pieces
// 2. get each cell's score
//
/*
 1. check alive four --****-- score: 10000
 2. check opponent alive four score: 5000
 3. check how to make: four-and-four, three-and-three and three-and-four  score: 1000
 4. check how to block opponent's: four-and-four, three-and-three and three-and-four  score: 500
 5. check how to make alive three, broken three or broken four  score: 100
 6. check how to block opponent's: alive three, broken three or broken four  score: 80
 7. make alive two or broken three score: (diagonal two: 40
 */
public class GomokuAI {
    private final  HashSet<GomokuRule.Cell> grid = new  HashSet<GomokuRule.Cell>();
    private Integer num;
    private char type;
    private HashMap<GomokuRule.Coordinate, Integer> nextStepsScores = new HashMap<GomokuRule.Coordinate, Integer>();
    // find instances and amount
    private HashSet< HashSet<GomokuRule.Coordinate> > FiveList = new HashSet< HashSet<GomokuRule.Coordinate> >();

    private HashSet< HashSet<GomokuRule.Coordinate> > AliveFourList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > BlockedFourList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > AliveThreeList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > BlockedThreeList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > AliveTwoList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > BlockedTwoList = new HashSet< HashSet<GomokuRule.Coordinate> >();



    public enum Instance {
        Five, Four, Three, Two, One,
        BlockedFour, BlockedThree, BlockedTwo
    }
    GomokuAI(HashSet<GomokuRule.Cell> Grid){
        this.grid.addAll( Grid );
        this.num = this.grid.size();
        this.type = this.getPieceType(num);
    }

    public char getPieceType(int n){
        if(n==0)
            return ' ';
        else
            return n % 2 == 0 ? 'O' : 'X';
    }

    public char getTypeFromGrid(int x, int y){
        GomokuRule.Coordinate coordinate = new GomokuRule.Coordinate(x, y);
        for( GomokuRule.Cell cell : this.grid ){
            if( cell.coordinate().equals(coordinate) ){
                return this.getPieceType( cell.steps() );
            }
        }
        return ' ';
    }

    private char getOpponentType(char c){
        return c == 'X' ? 'O' : 'X' ;
    }

    public HashSet< HashSet<GomokuRule.Coordinate> > getArrayByInstance(String s){
        switch (s){
            case "Five":            return this.findInstance( Instance.Five );
            case "Four":            return this.findInstance( Instance.Four );
            case "BlockedFour":     return this.findInstance( Instance.BlockedFour );
            case "Three":           return this.findInstance( Instance.Three );
            case "BlockedThree":    return this.findInstance( Instance.BlockedThree );
            case "Two":             return this.findInstance( Instance.Two );
            case "BlockedTwo":      return this.findInstance( Instance.BlockedTwo );
        }
        return this.findInstance( Instance.Three );
    }

    public void setInstanceList(int count, int gapNum, int oppoCellNum, HashSet<GomokuRule.Coordinate> Result) {
        if( count < 5 && oppoCellNum == 2) return;
        switch ( count ) {
            case 5: {
                this.FiveList.add( Result );
            }
            case 4: {
                if( gapNum <= 1 ) { //   ---xoooo---- or --xo-ooox---
                    if( oppoCellNum == 1 ) {
                        this.BlockedFourList.add( Result );
                    } else if ( oppoCellNum == 0 ) {
                        this.AliveFourList.add( Result );
                    }
                }
            }
            case 3: {
                if( gapNum <= 1 ) {
                    if( oppoCellNum == 1 ) {
                        this.BlockedThreeList.add( Result );
                    } else if ( oppoCellNum == 0 ) {
                        this.AliveThreeList.add( Result );
                    }
                }
            }
            case 2: {
                if( gapNum <= 1 ) {
                    if( oppoCellNum == 1 ) {
                        this.BlockedTwoList.add( Result );
                    } else if ( oppoCellNum == 0 ) {
                        this.AliveTwoList.add( Result );
                    }
                }
            }
            case 1: {

            }
            case 6: {

            }
        }
    }

    // when the alive four are already existing, that means you could win.
    public HashSet< HashSet<GomokuRule.Coordinate> > findInstance(Instance instance){
        Integer n = 0;   // the amount of alive four
        char piece = this.type;

        for( GomokuRule.Cell c : this.grid ) {
            int count = -1;  // count the connected cell number
            int x = c.coordinate().x(), y = c.coordinate().y();
            if(getTypeFromGrid(x, y) == piece) {
                HashSet<GomokuRule.Coordinate> Result = new HashSet<GomokuRule.Coordinate>();
//                System.out.println("init fourArray: " + this.BlockedThreeList);

                // step 1: vertical estimate
                int gapNum = 0;   // the gap between a line
                int oppoCellNum = 0;  // the opponent cell number at two side
                for(int i = y; i < y+5 && i <= 15 && gapNum <= 1; i++ ){
                    if( getTypeFromGrid(x, i) == piece){
                        GomokuRule.Coordinate cell = new GomokuRule.Coordinate(x, i);
                        Result.add(cell);
                        count++;
                        // --oxx-x--
                    } else if (getTypeFromGrid(x, i) == ' ' && getTypeFromGrid(x, i+1) == piece) {
                        gapNum++;
                    } else if ( getTypeFromGrid(x, i) == getOpponentType(piece) || i > 15){
                        oppoCellNum++;
                        break;
                    } else
                        break;
                }
                for(int i = y; i > y-5 && i >= 1 && gapNum <= 1; i-- ){
                    if( getTypeFromGrid(x, i) == piece){
                        GomokuRule.Coordinate cell = new GomokuRule.Coordinate(x, i);
                        Result.add(cell);
                        count++;
                    } else if (getTypeFromGrid(x, i) == ' ' && getTypeFromGrid(x, i-1) == piece) {
                        gapNum++;
                    } else if ( getTypeFromGrid(x, i) == getOpponentType(piece) || i < 1){
                        oppoCellNum++;
                        break;
                    } else
                        break;
                }
                this.setInstanceList(count, gapNum, oppoCellNum, Result);


                // step 2: horizontal estimate
                count = -1;
                gapNum = 0;   // the gap between a line
                oppoCellNum = 0;  // the opponent cell number at two side
                Result = new HashSet<GomokuRule.Coordinate>();
                for( int i = x; i < x+5 && i <= 15 && gapNum <= 1; i++ ){
                    if( getTypeFromGrid(i, y) == piece){
                        GomokuRule.Coordinate cell = new GomokuRule.Coordinate(i, y);
                        Result.add(cell);
                        count++;
                    } else if (getTypeFromGrid(i, y) == ' ' && getTypeFromGrid(i+1, y) == piece) {
                        gapNum++;
                    } else if ( getTypeFromGrid(i, y) == getOpponentType(piece) || i > 15){
                        oppoCellNum++;
                        break;
                    } else
                        break;
                }
                for( int i = x; i > x-5 && i >= 1 && gapNum <= 1; i-- ){
                    if( getTypeFromGrid(i, y) == piece){
                        GomokuRule.Coordinate cell = new GomokuRule.Coordinate(i, y);
                        Result.add(cell);
                        count++;
                    } else if (getTypeFromGrid(i, y) == ' ' && getTypeFromGrid(i-1, y) == piece) {
                        gapNum++;
                    } else if ( getTypeFromGrid(i, y) == getOpponentType(piece) || i < 1){
                        oppoCellNum++;
                        break;
                    } else
                        break;
                }
                this.setInstanceList(count, gapNum, oppoCellNum, Result);

                // step 3:  diagonal estimate
                count = -1;
                gapNum = 0;   // the gap between a line
                oppoCellNum = 0;  // the opponent cell number at two side
                Result = new HashSet<GomokuRule.Coordinate>();
                for( int i = x, j = y; i < x+5 && i <= 15 && j < y+5 && j <= 15 && gapNum <= 1; i++, j++ ){
                    if( getTypeFromGrid(i, j) == piece){
                        GomokuRule.Coordinate cell = new GomokuRule.Coordinate(i, j);
                        Result.add(cell);
                        count++;
                    } else if (getTypeFromGrid(i, j) == ' ' && getTypeFromGrid(i+1, j+1) == piece) {
                        gapNum++;
                    } else if ( getTypeFromGrid(i, j) == getOpponentType(piece) || i > 15 || j > 15){
                        oppoCellNum++;
                        break;
                    } else
                        break;
                }
                for( int i = x, j = y; i > x-5 && i > 0 && j > y-5 && j > 0 && gapNum <= 1; i--, j-- ){
                    if( getTypeFromGrid(i, j) == piece){
                        GomokuRule.Coordinate cell = new GomokuRule.Coordinate(i, j);
                        Result.add(cell);
                        count++;
                    } else if (getTypeFromGrid(i, j) == ' ' && getTypeFromGrid(i-1, j-1) == piece) {
                        gapNum++;
                    } else if ( getTypeFromGrid(i, j) == getOpponentType(piece) || i < 1 || j < 1 ){
                        oppoCellNum++;
                        break;
                    } else
                        break;
                }
                this.setInstanceList(count, gapNum, oppoCellNum, Result);


                    //
                count = -1;
                gapNum = 0;   // the gap between a line
                oppoCellNum = 0;  // the opponent cell number at two side
                Result = new HashSet<GomokuRule.Coordinate>();
                for( int i = x, j = y; i < x+5 && i <= 15 && j > y-5 && j > 0 && gapNum <= 1; i++, j-- ){
                    if( getTypeFromGrid(i, j) == piece){
                        GomokuRule.Coordinate cell = new GomokuRule.Coordinate(i, j);
                        Result.add(cell);
                        count++;
                    } else if (getTypeFromGrid(i, j) == ' ' && getTypeFromGrid(i+1, j-1) == piece) {
                        gapNum++;
                    } else if ( getTypeFromGrid(i, j) == getOpponentType(piece) || i > 15 || j < 1 ){
                        oppoCellNum++;
                        break;
                    } else
                        break;
                }
                for( int i = x, j = y; i > x-5 && i > 0 && j < y+5 && j <= 15 && gapNum <= 11; i--, j++ ){
                    if( getTypeFromGrid(i, j) == piece){
                        GomokuRule.Coordinate cell = new GomokuRule.Coordinate(i, j);
                        Result.add(cell);
                        count++;
                    } else if (getTypeFromGrid(i, j) == ' ' && getTypeFromGrid(i-1, j+1) == piece) {
                        gapNum++;
                    } else if ( getTypeFromGrid(i, j) == getOpponentType(piece) || i < 1 || j > 15 ){
                        oppoCellNum++;
                        break;
                    } else
                        break;
                }
                this.setInstanceList(count, gapNum, oppoCellNum, Result);
            }
        };
        switch( instance ){
            case Five:  {}
            case Four:          return this.AliveFourList;
            case BlockedFour:   return this.BlockedFourList;
            case Three:         return this.AliveThreeList;
            case BlockedThree:  return this.BlockedThreeList;
            case Two:           return this.AliveTwoList;
            case BlockedTwo:      return this.BlockedTwoList;
            case One: {}
        }
        return this.AliveFourList;
    }

    public ArrayList<GomokuRule.Coordinate> resultTable(){

        return null;
    }
}
