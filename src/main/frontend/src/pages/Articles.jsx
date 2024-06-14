import React, { useEffect, useState } from 'react';
import axios from 'axios';

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
          {list.map((item, index) => (
              <li key={index}>{item}</li>
          ))}
        </ul>
      </div>
  );
}

export default Articles;