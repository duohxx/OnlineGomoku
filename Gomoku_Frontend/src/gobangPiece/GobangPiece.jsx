import React from 'react';

function GobangPiece(props) {
  return (
    <button className={props.className} onClick={props.onClick} style={{'backgroundColor':props.color}} >
      {props.value}
    </button>
  );
}

export default GobangPiece;
