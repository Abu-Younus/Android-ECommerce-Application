import React from 'react';
import logo from './logo.svg';
import './App.css';
import { Redirect, Route, Switch } from 'react-router-dom';
import Login from './Pages/Login';
import Authenticate from './Components/Authenticate';
import Dashboard from './Pages/Dashboard';

function App() {
  return (
      <Switch>
        <Route exact path="/">
          <Authenticate>
            <Dashboard />
          </Authenticate>
        </Route>
        <Route exact path="/login">
          <Authenticate nonAuthenticate={true}>
            <Login />
          </Authenticate>
        </Route>
        <Route path="*" render={()=>"404 Not Found!"} />
      </Switch>
  );
}

export default App;
