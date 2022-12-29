package com.example.gomoku.game;

import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

import java.util.*;

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
    private final HashSet<GomokuRule.Cell> grid = new  HashSet<GomokuRule.Cell>();
    private int score;
    private Integer num;
    private char type;
    private HashMap<GomokuRule.Coordinate, Integer> nextStepsScores = new HashMap<GomokuRule.Coordinate, Integer>();
    // find instances and amount
    private HashSet< HashSet<GomokuRule.Coordinate> > FiveList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > AliveFourList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > GapFourList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > BlockedFourList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > AliveThreeList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > BlockedThreeList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > AliveTwoList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > BlockedTwoList = new HashSet< HashSet<GomokuRule.Coordinate> >();
    private HashSet< HashSet<GomokuRule.Coordinate> > OneList = new HashSet< HashSet<GomokuRule.Coordinate> >();

    public enum Instance {
        Five, Four, Three, Two, One,
        BlockedFour, BlockedThree, BlockedTwo,
        GapFour
    }

    GomokuAI(HashSet<GomokuRule.Cell> Grid){
        this.grid.addAll( Grid );
        this.num = this.grid.size();
        this.type = this.getPieceType(num);
    }

    public int scoreTable (Instance instance) {
        switch (instance) {
            case Five:          return 1000000;
            case Four:          return 100000;
            case GapFour:       return 210;
            case BlockedFour:   return 180;
            case Three:         return 200;
            case BlockedThree:  return 15;
            case Two:           return 10;
            case BlockedTwo:    return 4;
            case One:           return 1;
        }
        return 1;
    }
    /*
     * alpha : Best AI Move (Max)
     * beta : Best Player Move (Min)
     * type : next player's type
     * returns: {score, Cell}
     * */
    public Object[] minimaxAlphaBeta(int depth, boolean max, int alpha, int beta){
        // generate all potential move for next step
        HashSet<GomokuRule.Coordinate> potentialList = this.getReasonableCoordinate(this.grid);
        if( depth == 0 || potentialList.size() == 0 ) {
            System.out.println("cur step is: " + getLastSteps());
            System.out.println("cur hypothesis score is: " + getScore() + "\n");
//            Object[] n = { getScore(), getLastSteps() } ;
            Object[] n = { getScore(), null } ;
            return n;
        }
        // bestPlace : { int score ( the best score ), GomokuRule.Coordinate coordinate }
        Object[] bestPlace = new Object[2];
        // generate Minimax tree
        // if current step is from the AI not from the player, maximum the score
        if( max ) {
            // initialize the starting best cell
            bestPlace[0] = -1;
            // traverse each possible coordinate
            for ( GomokuRule.Coordinate coordinate : potentialList ){
                // create a new GomokuAI object
                GomokuAI newBroad = new GomokuAI(this.grid);
                // add the potential coordinate to the new GomokuAI object
                newBroad.addCell( coordinate );
                Object[] tempPlace = newBroad.minimaxAlphaBeta(depth-1, false, alpha, beta);
                // Updating alpha (alpha value holds the maximum score)
                if( (Integer)tempPlace[0] > alpha ){
                    alpha = (Integer) tempPlace[0];
                }
                // Pruning the subtree that bigger that beta
                // If the father node value are higher than beta, then discarding all subtree from this node
                // since Beta value holds the minimum score of their previous step
                if( (Integer)tempPlace[0] > beta ){
                    return tempPlace;
                }
                // find the place with best score
                if((Integer)tempPlace[0] >= (Integer)bestPlace[0] ){
                    bestPlace = tempPlace;
                    bestPlace[1] = coordinate;
                }
            }
        } else {
            // minimum the player score
            bestPlace[0] = 10000000;
            // traverse each possible coordinate
            for ( GomokuRule.Coordinate coordinate : potentialList ){
                // create a new GomokuAI object
                GomokuAI newBroad = new GomokuAI(this.grid);
                // add the potential coordinate to the new GomokuAI object
                newBroad.addCell( coordinate );
                Object[] tempPlace = newBroad.minimaxAlphaBeta(depth-1, true, alpha, beta);
                // Updating alpha (alpha value holds the minimum score)
                if( (Integer)tempPlace[0] < beta ){
                    beta = (Integer) tempPlace[0];
                }
                // Pruning the subtree that bigger that beta
                // If the father node value are higher than beta, then discarding all subtree from this node
                // since Beta value holds the maximum score of their previous step
                if( (Integer)tempPlace[0] <= alpha ){
                    return tempPlace;
                }
                // find the place with best score
                if((Integer)tempPlace[0] < (Integer)bestPlace[0] ){
                    bestPlace = tempPlace;
                    bestPlace[1] = coordinate;
                }
            }
        }
        return bestPlace;
    }

    public HashSet<GomokuRule.Coordinate> getReasonableCoordinate(HashSet<GomokuRule.Cell> G) {
        List<Integer> hor = new ArrayList<>();
        List<Integer> ver = new ArrayList<>();
        HashSet<GomokuRule.Coordinate> coordinatesCollection = new HashSet<>();
        HashSet<GomokuRule.Coordinate> PotentionalCoordinatesCollection = new HashSet<>();
        for(GomokuRule.Cell g : G){
            hor.add(g.x());
            ver.add(g.y());
            coordinatesCollection.add( g.coordinate() );
        }
        int xMax = Collections.max( hor );
        int yMax = Collections.max( ver );
        int xMin = Collections.min( hor );
        int yMin = Collections.min( ver );
        if( (xMax+2) > 15) xMax = 15;
        if( (yMax+2) > 15) yMax = 15;
        if( (xMin-2) < 1) xMin = 1;
        if( (yMin-2) < 1) yMin = 1;
//        System.out.println("xMax: "+xMax+" yMax: "+yMax+" xMin: "+xMin+" yMin: "+yMin);
        for(int i = xMin-1; i <= xMax+1; i++){
            for(int j = yMin-1; j <= yMax+1; j++){
                GomokuRule.Coordinate coordinate = new GomokuRule.Coordinate(i,j);
                if( !coordinatesCollection.contains(coordinate) ){
                    PotentionalCoordinatesCollection.add(coordinate);
                }
            }
        }
//        System.out.println("All reasonable coordinates number is: " + PotentionalCoordinatesCollection.size());
        return PotentionalCoordinatesCollection;
    }

    public GomokuRule.Coordinate getLastSteps(){
        GomokuRule.Coordinate coordinate = new GomokuRule.Coordinate(7,7);
        for(GomokuRule.Cell c : this.grid){
            if( c.steps() == this.num ){
                coordinate = c.coordinate();
            }
        }
        System.out.println("the last step is: " + coordinate);
        return coordinate;
    }
    public int getScore () {
        int score = 0;
        for( Instance instance : Instance.values() ){
            score += this.getArrayByInstance( instance.toString() ).size() * this.scoreTable( instance );
        }
        return score;
    }

    public char getPieceType(int n){
        if(n==0)
            return ' ';
        else
            return n % 2 == 0 ? 'X' : 'O';
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

    public char getCurrentType(){
        return this.type;
    }

    public char getOpponentType(char c){
        return c == 'X' ? 'O' : 'X' ;
    }

    public HashSet< HashSet<GomokuRule.Coordinate> > getArrayByInstance(String s){
        switch (s){
            case "Five":            return this.findInstance( Instance.Five );
            case "Four":            return this.findInstance( Instance.Four );
            case "GapFour":         return this.findInstance( Instance.GapFour );
            case "BlockedFour":     return this.findInstance( Instance.BlockedFour );
            case "Three":           return this.findInstance( Instance.Three );
            case "BlockedThree":    return this.findInstance( Instance.BlockedThree );
            case "Two":             return this.findInstance( Instance.Two );
            case "BlockedTwo":      return this.findInstance( Instance.BlockedTwo );
        }
        return this.findInstance( Instance.One );
    }

    public void addCell(GomokuRule.Coordinate coordinate){
        GomokuRule.Cell cell = new GomokuRule.Cell(coordinate, ++this.num);
        this.type = this.getPieceType(this.num);
        this.grid.add(cell);
    }

    public void setInstanceList(int count, int gapNum, int oppoCellNum, HashSet<GomokuRule.Coordinate> Result) {
        if( count < 5 && oppoCellNum == 2) return;
        switch ( count ) {
            case 5: {
                if( gapNum == 0 )
                    this.FiveList.add( Result );
                else
                    this.AliveFourList.add( Result );
            }
            case 4: {
                if( gapNum == 0 && oppoCellNum == 0 ){
                    this.AliveFourList.add( Result );
                } else if( oppoCellNum == 0 && gapNum == 1 ) { //   ---xoooo---- or --xo-ooox---
                    this.GapFourList.add( Result );
                } else if( oppoCellNum == 1 ){
                    this.BlockedFourList.add( Result );
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
                    if( oppoCellNum == 1 ) {
                        this.BlockedTwoList.add( Result );
                    } else if ( oppoCellNum == 0 ) {
                        this.AliveTwoList.add( Result );
                    }
            }
            case 1: {
                this.OneList.add( Result );
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
                    } else if ( getTypeFromGrid(x, i) == getOpponentType(piece) ){
                        //  ||    getTypeFromGrid(x, i) == ' ' && getTypeFromGrid(x, i+1) == getOpponentType(piece) || i > 15){
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
                    } else if ( getTypeFromGrid(x, i) == getOpponentType(piece) ){
                        //  ||    getTypeFromGrid(x, i) == ' ' && getTypeFromGrid(x, i-1) == getOpponentType(piece) || i < 1){
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
                    } else if ( getTypeFromGrid(i, y) == getOpponentType(piece) ){
                        //  ||    getTypeFromGrid(i, y) == ' ' && getTypeFromGrid(i+1, y) == getOpponentType(piece) || i > 15){
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
                    } else if ( getTypeFromGrid(i, y) == getOpponentType(piece) ){
                        //  ||    getTypeFromGrid(i, y) == ' ' && getTypeFromGrid(i-1, y) == getOpponentType(piece) || i < 1){
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
                    } else if ( getTypeFromGrid(i, j) == getOpponentType(piece)  ){
                        //  ||    getTypeFromGrid(i, j) == ' ' && getTypeFromGrid(i+1, j+1) == getOpponentType(piece) || i > 15 || j > 15){
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
                    } else if ( getTypeFromGrid(i, j) == getOpponentType(piece) ){
                            //  ||    getTypeFromGrid(i, j) == ' ' && getTypeFromGrid(i-1, j-1) == getOpponentType(piece) || i < 1 || j < 1 ){
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
                    } else if ( getTypeFromGrid(i, j) == getOpponentType(piece) ){
                          //  || getTypeFromGrid(i, j) == ' ' && getTypeFromGrid(i+1, j-1) == getOpponentType(piece)  || i > 15 || j < 1 ){
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
                    } else if ( getTypeFromGrid(i, j) == getOpponentType(piece)  ){
                     //      || getTypeFromGrid(i, j) == ' ' && getTypeFromGrid(i-1, j+1) == getOpponentType(piece)  || i < 1 || j > 15 ){
                        oppoCellNum++;
                        break;
                    } else
                        break;
                }
                this.setInstanceList(count, gapNum, oppoCellNum, Result);
            }
        };
        switch( instance ){
            case Five:          return this.FiveList;
            case Four:          return this.AliveFourList;
            case GapFour:       return this.GapFourList;
            case BlockedFour:   return this.BlockedFourList;
            case Three:         return this.AliveThreeList;
            case BlockedThree:  return this.BlockedThreeList;
            case Two:           return this.AliveTwoList;
            case BlockedTwo:    return this.BlockedTwoList;
            case One:           return this.OneList;
        }

        return this.OneList;
    }
}
