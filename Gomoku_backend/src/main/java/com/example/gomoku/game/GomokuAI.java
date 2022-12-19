package com.example.gomoku.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
    private HashMap<GomokuRule.Cell, Integer> grid = new HashMap<GomokuRule.Cell, Integer>();
    private HashMap<GomokuRule.Cell, Integer> nextStepsScores = new HashMap<GomokuRule.Cell, Integer>();
    // find instances and amount
    private HashSet< HashSet<GomokuRule.Cell> > AliveFourList = new HashSet< HashSet<GomokuRule.Cell> >();
    private Integer round;
    char type;

    GomokuAI(HashMap<GomokuRule.Cell, Integer> curGrid){
        this.grid.putAll( curGrid );
        this.round = this.grid.size();
        this.type = this.getPieceType( this.round );
    }

    public char getPieceType(int n){
        if(n==0)
            return ' ';
        else
            return n % 2 == 0 ? 'O' : 'X';
    }

    public char getTypeFromGrid(int x, int y){
        GomokuRule.Cell cell = new GomokuRule.Cell(x, y);
        if( this.grid.containsKey(cell))
            return this.getPieceType( this.grid.get(cell) );
        else return ' ';
    }

    // when the alive four are already existing, that means you could win.
    public HashSet< HashSet<GomokuRule.Cell> > findAliveFour(){
        Integer n = 0;   // the amount of alive four
        char piece = this.type;

        this.grid.forEach((C, value) -> {
            int count = -1;  // count the connected cell number
            int x = C.x(), y = C.y();
            HashSet<GomokuRule.Cell> Result = new HashSet<>();
            System.out.println("init fourArray: " + this.AliveFourList);
            // step 1: vertical estimate
            for(int i = y; i < y+4 && i <= 15 && getTypeFromGrid(x, i) == piece; i++, count++ ){
                GomokuRule.Cell cell = new GomokuRule.Cell(x, i);
                Result.add(cell);
            }
            for(int i = y; i > y-4 && i >= 0 && getTypeFromGrid(x, i) == piece; i--, count++ ){
                GomokuRule.Cell cell = new GomokuRule.Cell(x, i);
                Result.add(cell);
            }
            if( count == 4 ) {

                this.AliveFourList.add( Result );
                System.out.println("vertical: " + Result);
            } else if ( count == 3 ) {

            }
            // step 2: horizontal estimate
            count = -1;
            Result.clear();
            for( int i = x; i < x+4 && i <= 15 && getTypeFromGrid(i, y) == piece; i++, count++ ){
                GomokuRule.Cell cell = new GomokuRule.Cell(i, y);
                Result.add(cell);
            }
            for( int i = x; i > x-4 && i >= 0 && getTypeFromGrid(i, y) == piece; i--, count++ ){
                GomokuRule.Cell cell = new GomokuRule.Cell(i, y);
                Result.add(cell);
            }
            if( count == 4 ) {
                this.AliveFourList.add( Result );
                System.out.println("horizontal: " + Result);
            } else if ( count == 3 ) {

            }
            // step 3:  diagonal estimate
            count = -1;
            Result.clear();
            for( int i = x, j = y; i < x+4 && i <= 15 && j < y+4 && j <= 15 && getTypeFromGrid(i, j) == piece; i++, j++, count++ ){
                GomokuRule.Cell cell = new GomokuRule.Cell(i, j);
                Result.add(cell);
            }
            for( int i = x, j = y; i > x-4 && i > 0 && j > y-4 && j > 0 && getTypeFromGrid(i, j) == piece; i--, j--, count++ ){
                GomokuRule.Cell cell = new GomokuRule.Cell(i, j);
                Result.add(cell);
            }
            if( count == 4 ) {
                this.AliveFourList.add( Result );
                System.out.println("dio1: " + Result);
            } else if ( count == 3 ) {

            }
            //
            count = -1;
            Result.clear();
            for( int i = x, j = y; i < x+4 && i <= 15 && j > y-4 && j >= 0 && getTypeFromGrid(i, j) == piece; i++, j--, count++ ) {
                GomokuRule.Cell cell = new GomokuRule.Cell(i, j);
                Result.add(cell);
            }
            for( int i = x, j = y; i > x-4 && i > 0 && j < y+4 && j <= 15 && getTypeFromGrid(i, j) == piece; i--, j++, count++ ) {
                GomokuRule.Cell cell = new GomokuRule.Cell(i, j);
                Result.add(cell);
            }
            if( count == 4 ) {
                this.AliveFourList.add( Result );
                System.out.println("vertical: " + Result);
            } else if ( count == 3 ) {

            }
        });
        System.out.println();
        this.AliveFourList.forEach((e) -> {
            System.out.println(e);
        });
        System.out.println();
        return this.AliveFourList;
    }

    public ArrayList<GomokuRule.Cell> resultTable(){

        return null;
    }
}
