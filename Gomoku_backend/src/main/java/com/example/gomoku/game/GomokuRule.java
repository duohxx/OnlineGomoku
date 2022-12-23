package com.example.gomoku.game;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class GomokuRule {
    private Integer step = 0;   // How many steps
    private int size = 15;   // the size of broad
    // private int arrayLength = size * size;
    public record Coordinate(int x, int y){ }

    public record Cell(Coordinate coordinate, int steps) { } ;

//    private HashMap<Coordinate, Integer> grid = new HashMap<>();
    private HashSet<Cell> broad = new HashSet<>();

    private Cell curCell;
    public GomokuRule(){

    }

    public HashSet< HashSet<Coordinate>> getArrayByInstance(String s){
        GomokuAI g = new GomokuAI( this.broad );
        return g.getArrayByInstance(s);
    }

    public HashSet<Cell> getAllStep(){
//        ArrayList<Cell> allStep = new ArrayList<>();
//        this.grid.forEach((coordinate, step) -> {
//            allStep.add(coordinate);
//        });
        System.out.println(this.broad);
        return this.broad;
    }
    public HashSet<Coordinate> setCoordinate(int n){
        int indexNumber = n + 1;
        Coordinate coordinate = new Coordinate(indexNumber % size, indexNumber / size + 1);
        Cell cell = new Cell(coordinate, ++this.step);
        this.broad.add(cell);
        this.curCell = cell;
        return GomokuEstimate( coordinate.x(), coordinate.y() );
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

    public HashSet<GomokuRule.Cell> undo(){
        if( this.broad.isEmpty() ){
            return this.broad;
        } else {
//            Collection<Integer> steps = this.broad.values();   // according shallow clone to remove the cell of this.grid
//            while ( steps.contains( this.step) ){
//                steps.remove(this.step);
//            }
            this.broad.remove( this.curCell );
            this.step--;
            for( Cell c : this.broad ){
                if( c.steps() == this.step ) {
                    this.curCell = c;
                    break;
                }
            }
            return this.broad;
        }
    }

    public void reset(){
        this.step = 0;
        this.broad.clear();
    }
    public String produceCoordinate(int n){
        int x, y;
        int indexNumber = n + 1;
        x = indexNumber % size;
        y = indexNumber / size + 1;
        return "(" + x + "," + y +")";
    }

    public char getTypeFromGrid(int x, int y){
        Coordinate coordinate = new Coordinate(x, y);
        for( Cell cell : this.broad ){
            if( cell.coordinate().equals(coordinate) ){
                return this.getPieceType( cell.steps() );
            }
        }
        return ' ';
    }

    public void gameStop(){
        System.out.println("Win");
    }

    private HashSet<Coordinate> GomokuEstimate(int x, int y) {
        int count = -1;
        char piece = getTypeFromGrid(x, y);
        HashSet<Coordinate> Result = new HashSet<>();
        // step 1: vertical estimate
        for(int i = y; i < y+5 && i <= 16 && getTypeFromGrid(x, i) == piece; i++, count++ ){
            Coordinate coordinate = new Coordinate(x, i);
            Result.add(coordinate);
        }
        for(int i = y; i > y-5 && i >= 1 && getTypeFromGrid(x, i) == piece; i--, count++ ){
            Coordinate coordinate = new Coordinate(x, i);
            Result.add(coordinate);
        }
        if( count >= 5 ) {
            gameStop();
            return Result;
        }
        // step 2: horizontal estimate
        count = -1;
        Result.clear();
        for( int i = x; i < x+5 && i <= 15 && getTypeFromGrid(i, y) == piece; i++, count++ ){
//            System.out.println("x: " + x + " y: " + y + " i: "+i+" j: "+ " count: "+count);
            Coordinate coordinate = new Coordinate(i, y);
            Result.add(coordinate);
        }
        for( int i = x; i > x-5 && i >= 1 && getTypeFromGrid(i, y) == piece; i--, count++ ){
//            System.out.println("x: " + x + " y: " + y + " i: "+i+" j: "+ " count: "+count);
            Coordinate coordinate = new Coordinate(i, y);
            Result.add(coordinate);
        }
        if( count >= 5 ) {
            gameStop();
            return Result;
        }
        // step 3:  diagonal estimate
        count = -1;
        Result.clear();
        for( int i = x, j = y; i < x+5 && i <= 15 && j < y+5 && j <= 15 && getTypeFromGrid(i, j) == piece; i++, j++, count++ ){
            Coordinate coordinate = new Coordinate(i, j);
            Result.add(coordinate);
        }
        for( int i = x, j = y; i > x-5 && i >= 1 && j > y-5 && j >= 1 && getTypeFromGrid(i, j) == piece; i--, j--, count++ ){
            Coordinate coordinate = new Coordinate(i, j);
            Result.add(coordinate);
        }
        if( count >= 5 ) {
            gameStop();
            return Result;
        }
        //
        count = -1;
        Result.clear();
        for( int i = x, j = y; i < x+5 && i <= 15 && j > y-5 && j >= 1 && getTypeFromGrid(i, j) == piece; i++, j--, count++ ) {
            Coordinate coordinate = new Coordinate(i, j);
            Result.add(coordinate);
        }
        for( int i = x, j = y; i > x-5 && i > 1 && j < y+5 && j <= 15 && getTypeFromGrid(i, j) == piece; i--, j++, count++ ) {
            Coordinate coordinate = new Coordinate(i, j);
            Result.add(coordinate);
        }
        if( count >= 5 ) {
            gameStop();
            return Result;
        }
        System.out.println("Not winning");
        return null;
    }

}
