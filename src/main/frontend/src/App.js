import './App.css';
import {Route, Routes} from "react-router-dom";
import Home from "./pages/Home";
import Header from "./components/Header";
import Tags from "./components/Tags";

function App() {
  return (
    <div className="container">
      <Header className="header"/>
      <div className="main">

        <div className="content">
          <Routes>
            <Route path="/" element={<Home />} />
          </Routes>
        </div>

        <Tags className="sidebar" />
      </div>
    </div>
  );
}

export default App;
