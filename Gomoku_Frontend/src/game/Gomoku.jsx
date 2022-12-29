import React, { Component } from 'react';
import GobangBoard from '../gobangBoard/GobangBoard';
import './index.css';
import{
  undo,
  setCoordinate,
  getAllSteps,
  reset,
  findInstance,
  getScore,
  AIplay
} from '../api/index'
class Gomoku extends Component {

    state = {
      coordinate: Array(225).fill(null),
      color: Array(225).fill('green'),
      xIsNext: true,  
      end: false   // indicate the game state
    };

  async handleClick(i) {
    const cell = this.state.coordinate;
    const color = Array(225).fill('green');
    color[i] = 'blue'
    let end = false
    // if the cell are already full or the game is finished
    if (cell[i] || this.state.end) {
      return;
    }
    // send the current step (coordinate) to the backend
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

    const [coordinate] = await this.getAllPieces();

    this.setState({
      coordinate: coordinate, //////////////////////////////////////////////////////////
       color: color,
      // stepNumber: this.length,
      xIsNext: !this.state.xIsNext,
      end: end
    });
    // console.log(this)
 
  }

  async undoPiece () {
    var color = Array(225).fill('green')
    const response = await undo();
    if ( response.status === 200 ){
      console.log(response.data)
    } else {
      console( response.err )
    }
    const [coordinate, lastStep] = await this.getAllPieces();
    color[lastStep] = 'blue'
    console.log(lastStep)
    this.setState({
      coordinate: coordinate, //////////////////////////////////////////////////////////
      color: color,
      // stepNumber: this.length,
      xIsNext: !this.state.xIsNext,
      end: false
    });
    
  }

  getCellType = (step) => {
    if( step === 0) {   // step === 0 means there is no stone
      return null
    } else 
    return step % 2 === 1 ? 'O' : 'X'
  }

  async AIplay(){
    var color = Array(225).fill('green')
    const response = await AIplay();
    if ( response.status === 200 ){
      console.log("AI think the next best coordinate is: ( " + response.data.x +", " + response.data.y + " )")
      const coordinate = response.data
      color[coordinate.x - 1 + 15 * (coordinate.y - 1)] = "White"
    } else {
      console( response.err )
    }
    this.setState({
      color: color,
    });
    
  }

  async getAllPieces() {
    let coordinate = Array(225).fill(null)
    const response = await getAllSteps();
    let lastStep;
    console.log(response)
    if(response.status === 200) {
      console.log("history is: ")
      response.data.forEach(cell => {
        console.log("Step " + cell.steps + " (" + cell.coordinate.x + ", " + cell.coordinate.y + ")" )
        coordinate[cell.coordinate.x - 1 + 15 * (cell.coordinate.y - 1)] = this.getCellType( cell.steps )
        if( cell.steps === response.data.length ){
          lastStep = cell.coordinate.x - 1 + 15 * (cell.coordinate.y - 1)
        }
      });
    } else {
      console.log( response.err )
    }
    return [ coordinate, lastStep ]
  }

  async resetBroad () {
    const response = await reset()
    if(response.status === 200) {
      console.log("Reset Success")
    } else {
      console.log( response.err )
    }
    this.setState(
      {
      coordinate: Array(225).fill(null),
      color: Array(225).fill('green'),
      stepNumber: 0,
      xIsNext: true,  
      end: false   // indicate the game state
    }) 
  }

  async getScore () {
    const response = await getScore()
    if( response.status === 200 ){
      console.log(response.data)
    } else {
      console.log(response.err)
    }
  }

  async findInstance() {
    const type = document.getElementById("InstanceType").value
    console.log(type)
    let color = Array(255).fill('green')
    const response = await findInstance(type)
    if( response.status === 200 ){
      response.data.forEach(line => {
        line.forEach(cell =>{
          color[cell.x - 1 + 15 * (cell.y - 1)] = "yellow"
          console.log("the BlockedThree is: " + cell.x +", " + cell.y)
        })
      });
    } else {
      console.log( response.err )
    }
    this.setState({ color: color })
  }

  render() {
    // console.log("broad: ")
    // console.log(this.state)

    return (
      <div className="game">
        <div className="game-board">
          <GobangBoard
            squares={ this.state.coordinate }
            color={ this.state.color }
            onClick={(i) => this.handleClick(i)}
          />
        </div>
        <div>
          <div>
            <button onClick={() => this.undoPiece()}>Undo</button>
          </div>
          <div>
            <button onClick={() => this.getAllPieces()}>get history</button>
          </div>
          <div>
            <button onClick={() => this.resetBroad()}>Reset</button>
          </div>
          <div>
            <button onClick={() => this.findInstance()}>Find Instance</button>
            <select id="InstanceType">
              <option value="Four">Four</option>
              <option value="GapFour">GapFour</option>
              <option value="BlockedFour">BlockedFour</option>
              <option value="Three">Three</option>
              <option value="BlockedThree">BlockedThree</option>
              <option value="Two">Two</option>
              <option value="BlockedTwo">BlockedTwo</option>
            </select>
          </div>
          <div>
            <button onClick={() => this.getScore()}>getScore</button>
          </div>
          <div>
            <button onClick={() => this.AIplay()}>AIplay</button>
          </div>
        </div>
      </div>
    );
  }
}

export default Gomoku;