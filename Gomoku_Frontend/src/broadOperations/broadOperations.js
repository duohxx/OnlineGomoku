// import{
//     undo,
//     setCoordinate,
//     getAllSteps,
//     reset,
//     findAliveFour
//   } from '../api/index'

// export async function undoPiece () {
// // const undoCoordinate = this.state.history.coordinate.slice(-1);
//     const history = this.state.history.slice(0, this.state.stepNumber);
//     this.setState({
//       history: history,
//       stepNumber: history.length-1,
//       xIsNext: !this.state.xIsNext,
//       end: false
//     });
//     const response = await undo();
//     if ( response.status === 200 ){
//       console.log(response.data)
//     } else {
//       console( response.err )
//     }
// }

// export async function findFour( ) {
//     const history = this.state.history.slice(0, this.state.stepNumber + 1);
//     const current = history[this.state.stepNumber];
//     const color = Array(225).fill('green');
//     const response = await findAliveFour();
//     if( response.status === 200 ){
//       console.log("the findfour is: " + response.data.x)
//       response.data.forEach(line => {
//         line.forEach(cell =>{
//           color[cell.x - 1 + 15 * (cell.y - 1)] = "yellow"
//         })
//       });
//     } else {
//       console.log(response.err)
//     }
//     this.setState({
//       history: history.concat([{
//         squares: current.squares,
//         coordinate: current.coordinate,
//         color: color,
//       }]),
//       stepNumber: history.length,
//       xIsNext: this.state.xIsNext,
//       end: this.state.end
//     });
//   }

