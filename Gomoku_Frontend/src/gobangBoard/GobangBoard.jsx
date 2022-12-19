import React, { Component } from 'react';
import GobangPiece from '../gobangPiece/GobangPiece';


class GobangBoard extends Component {
  renderSquare(i) {
    let className = 'square'
    return (
      <GobangPiece
        key={i}
        className={className}
        value={this.props.squares[i]}
        onClick={() => this.props.onClick(i)}
        color={this.props.color[i]}
      />
    );
  }

  render() {

    /**
     *@function render the board
     *@return react element
     * */
    const renderBoard = () => {
      let n = 0;
      let board = [];
      for (let i = 0; i < 15; i++) {
        const boardRow = [];
        for (let j = 0; j < 15; j++, n++) {
          boardRow.push(this.renderSquare(n));
        }
        board.push(<div className="board-row" key={i}>{boardRow}</div>);
      }
      return board;
    }

    return (
      <div>
        {renderBoard()}
      </div>
    );
  }
}
export default GobangBoard;
