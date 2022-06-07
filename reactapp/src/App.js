import logo from './logo.svg';
import './App.css';
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://192.168.0.39:8080'
});

function App() {
  console.log('App started');
  api.get('/').then(response => {
    console.log(response.data);
  });

  return (
    <div className='App'>
      <header className='App-header'>
        <img src={logo} className='App-logo' alt='logo' />
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className='App-link'
          href='https://reactjs.org'
          target='_blank'
          rel='noopener noreferrer'
        >
          Moin
        </a>
      </header>
    </div>
  );
}

export default App;
