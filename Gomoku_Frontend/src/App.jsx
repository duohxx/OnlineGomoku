import React, { Component } from 'react';
import { Route, Routes, Link, BrowserRouter } from "react-router-dom";
import Gomoku from './game/Gomoku'; // Import Gomoku game


export default class App extends Component {

  render() {
    return (
      <BrowserRouter>
        <div style={{'backgroundColor':'black'}}>
          <ul>
            <li>
              <Link to="/Gomoku">Gomoku</Link>
            </li>
          </ul>
          <Routes>
            <Route path="/Gomoku" element={<Gomoku />} />
          </Routes>
        </div>
      </BrowserRouter>
    );
  }
}




