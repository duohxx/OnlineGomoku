import React, { Component } from 'react';
import GobangBoard from '../gobangBoard/GobangBoard';
import './index.css';
import{
  undo,
  setCoordinate,
  getAllSteps,
  reset,
  findAliveFour
} from '../api/index'
class Gomoku extends Component {

    state = {
      history: [{
        squares: Array(225).fill(null),
        color: Array(225).fill('green'),
        coordinate: [],
      }],
      stepNumber: 0,
      xIsNext: true,
      end: false
    };
  /**
   * @function Click the broad
   * @description  
   * @param {number} i is the step index
   * 
   * */

  async handleClick(i) {
    const history = this.state.history.slice(0, this.state.stepNumber + 1);
    const current = history[this.state.stepNumber];

    const squares = current.squares.slice();
    const color = Array(225).fill('green');
    let end = false
    // if the cell are already full
    if (squares[i] || this.state.end) {
      return;
    }

    const steps = await setCoordinate(i)
    if( steps.status === 200 ){
      if(steps.data){
          console.log(steps)
          end = true
          steps.data.forEach(element => {
            color[element.x - 1 + 15 * (element.y - 1)] = "red"
          });
      }
    } else {
      console.log(steps.err)
    }

    // const promise = setCoordinate(i)
    // promise.then((response) => {
    //   console.log(response)
    // })
    // .catch(err => console.log(err))

    squares[i] = this.state.xIsNext ? 'X' : 'O'; // fill the broad
    this.setState({
      history: history.concat([{
        squares: squares,
        coordinate: i,
        color: color,
      }]),
      stepNumber: history.length,
      xIsNext: !this.state.xIsNext,
      end: end
    });
    // console.log(this)
 
  }

  async undoPiece () {
    // const undoCoordinate = this.state.history.coordinate.slice(-1);
    const history = this.state.history.slice(0, this.state.stepNumber);
    this.setState({
      history: history,
      stepNumber: history.length-1,
      xIsNext: !this.state.xIsNext,
      end: false
    });
    const response = await undo();
    if ( response.status === 200 ){
      console.log(response.data)
    } else {
      console( response.err )
    }
  }

  async findFour( ) {
    const history = this.state.history.slice(0, this.state.stepNumber + 1);
    const current = history[this.state.stepNumber];

    const color = Array(225).fill('green');


    const response = await findAliveFour();
    if( response.status === 200 ){
      console.log("the findfour is: " + response.data.x)
      response.data.forEach(line => {
        line.forEach(cell =>{
          color[cell.x - 1 + 15 * (cell.y - 1)] = "yellow"
        })
      });
    } else {
      console.log(response.err)
    }


    
    this.setState({
      history: history.concat([{
        squares: current.squares,
        coordinate: current.coordinate,
        color: color,
      }]),
      stepNumber: history.length,
      xIsNext: this.state.xIsNext,
      end: this.state.end
    });
    
  }

  resetBroad =() => {
    const history = [{
      squares: Array(225).fill(null),
      coordinate: [],
      color: Array(225).fill('green'),
    }];
    this.setState({
      history: history,
      stepNumber: 0,
      xIsNext: true,
      end: false
    });
    reset()
  }

  render() {
    const { history, stepNumber } = this.state;
    const current = history[stepNumber];
    // console.log(current)

    return (
      <div className="game">
        <div className="game-board">
          <GobangBoard
            squares={current.squares}
            color={current.color}
            onClick={(i) => this.handleClick(i)}
          />
        </div>
        <div>
          <button onClick={() => this.undoPiece()}>Undo</button>
        </div>
        <div>
          <button onClick={() => getAllSteps()}>get history</button>
        </div>
        <div>
          <button onClick={() => this.resetBroad()}>Reset</button>
        </div>
        <div>
          <button onClick={() => this.findFour()}>findAliveFour</button>
        </div>
      </div>
    );
  }
}

export default Gomoku;