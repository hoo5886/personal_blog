import './App.css';
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import Header from "./components/Header";
import Tags from "./components/Tags";
import {useEffect, useState} from "react";
import {fetchHello, fetchArticles} from "./apis";
import Articles from "./pages/Articles";
import Hello from "./pages/Hello";
import Write from "./pages/Write";

function App() {

  const [message, setMessage] = useState('');
  const [list, setList] = useState([]);

  useEffect(() => {
    // /hello 엔드포인트 호출
    const getHelloMessage = async () => {
      try {
        const data = await fetchHello();
        setMessage(data);
      } catch (error) {
        console.error('Error fetching hello message:', error);
      }
    };

    // /list 엔드포인트 호출
    const getArticlesData = async () => {
      try {
        const data = await fetchArticles();
        setList(data);
      } catch (error) {
        console.error('Error fetching list data:', error);
      }
    };

    getHelloMessage();
    getArticlesData();
  }, []);

  return (
        <div className="container">
          <Header className="header" />

          <div className="main">
            <div className="content">
              <Routes>
                <Route path="/hello" element={<Hello />} />
                <Route path="/list" element={<Articles />} />
                <Route path="/edit" element={<Write />} />
              </Routes>
            </div>

            <Tags className="sidebar" />
          </div>
        </div>
  );
}

export default App;
