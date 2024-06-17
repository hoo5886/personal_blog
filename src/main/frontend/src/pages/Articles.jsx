import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {Link} from "react-router-dom";

const Articles = () => {
  const [list, setList] = useState([]);

  useEffect(() => {
    const fetchArticles = async () => {
      try {
        const response = await axios.get('/list');
        setList(response.data);
      } catch (error) {
        console.error('Error fetching list data:', error);
      }
    };

    fetchArticles();
  }, []);

  return (
      <div>
        <h2>List from Backend</h2>
        <ul>
          {list.map((item) => (
              <li key={item.id}>
                <h3>
                  <Link to={`/list/${item.id}`}>{item.title}</Link>
                </h3>
                <p>Likes: {item.likes}</p>
                <p>Created At: {item.createdAt}</p>
                <p>Updated At: {item.updatedAt}</p>
              </li>
          ))}
        </ul>
      </div>
  );
}

export default Articles;