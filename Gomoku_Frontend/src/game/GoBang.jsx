// import React, { Component } from 'react';
// import axios from 'axios'
// import GobangBoard from '../gobangBoard/GobangBoard';
// import './index.css';

// class GoBang extends Component {
//   constructor(props) {
//     super(props);
//     this.state = {
//       history: [{
//         squares: Array(225).fill(null),
//         coordinate: [],
//       }],
//       stepNumber: 0,
//       xIsNext: true,
//       lines: [], // 存储棋子的索引的数组
//     };
//   }
//   /**
//    * @function 方格对应的点击事件
//    * @description  游戏未结束，存档;游戏决出胜利者，三连高亮显示；游戏结束，弹出提示；
//    * @param {number} i 对应方格在数组中的索引index
//    * 
//    * */

//   handleClick(i) {
//     const history = this.state.history.slice(0, this.state.stepNumber + 1);
//     const current = history[this.state.stepNumber];

//     const squares = current.squares.slice();
//     // 如果游戏结束或者该位置上已经有棋子跳出函数
//     if (squares[i]) {
//       return;
//     }
//     squares[i] = this.state.xIsNext ? 'X' : 'O'; // 在数组对应位置填充棋子
//     // const coordinateArray = this.produceCoordinate();
//     // let coordinate = `(${coordinateArray[i].x},${coordinateArray[i].y})`;

//     this.setState({
//       history: history.concat([{
//         squares: squares,
//         // coordinate: coordinate
//         coordinate: i
//       }]),
//       stepNumber: history.length,
//       xIsNext: !this.state.xIsNext,
//     });
//     // onst {history} = this.state
//     console.log(this)
//     this.submit(i)
//   }

//   undo = () => {
//     // const undoCoordinate = this.state.history.coordinate.slice(-1);
//     const history = this.state.history.slice(0, this.state.stepNumber);
//     this.setState({
//       history: history,
//       stepNumber: history.length-1,
//       xIsNext: !this.state.xIsNext,
//     });
//     console.log(this)
//     axios.get('http://localhost:8080/gomoku/undo')
//     .then((response) => {
// 		  if (response.status === 200) {
//       console.log(response.data)
// 		}
// 	})
//     .catch(err => console.log(err))
//   }

//   submit = (id) => {
//     axios.get('http://localhost:8080/gomoku/set/'+id)
//     .then((response) => {
// 		  if (response.status === 200) {
//       console.log(response.data)
// 		}
// 	})
//     .catch(err => console.log(err))
// }



//   render() {
//     let { lines } = this.state;
//     const { history, stepNumber } = this.state;
//     const current = history[stepNumber];

//     return (
//       <div className="game">
//         <div className="game-board">
//           <GobangBoard
//             squares={current.squares}
//             onClick={(i) => this.handleClick(i)}
//             lines={lines}
//           />
//         </div>
//         <div>
//           <button onClick={() => this.undo()}>Undo</button>
//         </div>
//       </div>
//     );
//   }
// }

// export default GoBang;





// // /**
// //    * @function 根据棋子索引分配坐标
// //    * @returns {array} 
// //    * */
// //  produceCoordinate = () => {
// //   const size = 15; // board size
// //   const arrayLength = size * size;
// //   let coordinateArray = []; 
// //   let yInit = size; 
// //   for (let i = 0; i < arrayLength; i++) {
// //     let indexNumber = i + 1;
// //     if (indexNumber % size > 0) {
// //       coordinateArray.push({ x: indexNumber % size, y: yInit })
// //     } else {
// //       coordinateArray.push({ x: size, y: yInit })
// //       yInit = yInit - 1;
// //     }
// //   }
// //   return coordinateArray;
// // }
