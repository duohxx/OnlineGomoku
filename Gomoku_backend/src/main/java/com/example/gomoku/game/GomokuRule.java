package com.example.gomoku.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GomokuRule {
    private Integer step = 0;   // How many steps
    private int size = 15;   // the size of broad
    // private int arrayLength = size * size;
    public record Cell(int x, int y){ }
    private HashMap<Cell, Integer> grid = new HashMap<>();
    public GomokuRule(){

    }

    public HashSet< HashSet<Cell> > getAliveFour(){
        GomokuAI g = new GomokuAI( this.grid );
        return g.findAliveFour();
    }

    public ArrayList<Cell> getAllStep(){
        ArrayList<Cell> allStep = new ArrayList<>();
        this.grid.forEach((coordinate, step) -> {
            allStep.add(coordinate);
        });
        return allStep;
    }
    public HashSet<Cell> setCoordinate(int n){
        int indexNumber = n + 1;
        Cell cell = new Cell(indexNumber % size, indexNumber / size + 1);
        this.step++;
        this.grid.put(cell, step);
        return GomokuEstimate(cell.x, cell.y);
    }

    public char getPieceType(int n){
        if(n==0)
            return ' ';
        else
            return n % 2 == 0 ? 'O' : 'X';
    }

    public char getCurType(){
        return this.getPieceType( this.step );
    }

    public String undo(){
        if( this.grid.isEmpty() ){
            return "The broad already empty";
        } else {
            this.grid.remove( this.step );
            this.step--;
            return "Undo Successfully" ;
        }
    }

    public void reset(){
        this.step = 0;
        this.grid.clear();
    }
    public String produceCoordinate(int n){
        int x, y;
        int indexNumber = n + 1;
        x = indexNumber % size;
        y = indexNumber / size + 1;
        return "(" + x + "," + y +")";
    }

    public char getTypeFromGrid(int x, int y){
        Cell cell = new Cell(x, y);
        if( this.grid.containsKey(cell))
            return this.getPieceType( this.grid.get(cell) );
        else return ' ';
    }

    public void gameStop(){
        System.out.println("Win");
    }

    private HashSet<Cell> GomokuEstimate(int x, int y) {
        int count = -1;
        char piece = getTypeFromGrid(x, y);
        HashSet<Cell> Result = new HashSet<>();
        // step 1: vertical estimate
        for(int i = y; i < y+5 && i <= 15 && getTypeFromGrid(x, i) == piece; i++, count++ ){
            Cell cell = new Cell(x, i);
            Result.add(cell);
        }
        for(int i = y; i > y-5 && i >= 0 && getTypeFromGrid(x, i) == piece; i--, count++ ){
            Cell cell = new Cell(x, i);
            Result.add(cell);
        }
        if( count >= 5 ) {
            gameStop();
            return Result;
        }
        // step 2: horizontal estimate
        count = -1;
        Result.clear();
        for( int i = x; i < x+5 && i <= 15 && getTypeFromGrid(i, y) == piece; i++, count++ ){
            Cell cell = new Cell(i, y);
            Result.add(cell);
        }
        for( int i = x; i > x-5 && i >= 0 && getTypeFromGrid(i, y) == piece; i--, count++ ){
            Cell cell = new Cell(i, y);
            Result.add(cell);
        }
        if( count >= 5 ) {
            gameStop();
            return Result;
        }
        // step 3:  diagonal estimate
        count = -1;
        Result.clear();
        for( int i = x, j = y; i < x+5 && i <= 15 && j < y+5 && j <= 15 && getTypeFromGrid(i, j) == piece; i++, j++, count++ ){
            Cell cell = new Cell(i, j);
            Result.add(cell);
        }
        for( int i = x, j = y; i > x-5 && i > 0 && j > y-5 && j >= 0 && getTypeFromGrid(i, j) == piece; i--, j--, count++ ){
            Cell cell = new Cell(i, j);
            Result.add(cell);
        }
        if( count >= 5 ) {
            gameStop();
            return Result;
        }
        //
        count = -1;
        Result.clear();
        for( int i = x, j = y; i < x+5 && i <= 15 && j > y-5 && j >= 0 && getTypeFromGrid(i, j) == piece; i++, j--, count++ ) {
            Cell cell = new Cell(i, j);
            Result.add(cell);
        }
        for( int i = x, j = y; i > x-5 && i > 0 && j < y+5 && j <= 15 && getTypeFromGrid(i, j) == piece; i--, j++, count++ ) {
            Cell cell = new Cell(i, j);
            Result.add(cell);
        }
        if( count >= 5 ) {
            gameStop();
            return Result;
        }
        System.out.println("Not winning");
        return null;
    }

}
