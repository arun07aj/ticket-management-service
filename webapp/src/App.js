import './App.css';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './components/Home';
import TicketForm from './components/TicketForm';
import TicketList from './components/TicketList';
import ViewTicket from "./components/ViewTicket";

function App() {
  return (
      <Router>
          <Routes>
              <Route path="/" exact element={<Home />} />
              <Route path="/create-ticket" element={<TicketForm />} />
              <Route path="/view-tickets" element={<TicketList />} />
              <Route path="/tickets/:id" element={<ViewTicket />} />
          </Routes>
      </Router>
  );
}

export default App;
