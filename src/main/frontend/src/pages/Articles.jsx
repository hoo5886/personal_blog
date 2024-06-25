import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {Link, useNavigate} from "react-router-dom";

const Articles = () => {
  const [list, setList] = useState([]);
  const [isDeleted, setIsDeleted] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchArticles = async () => {
      try {
        const response = await axios.get('/articles');
        setList(response.data);
      } catch (error) {
        console.error('Error fetching list data:', error);
      }
    };

    fetchArticles();
  }, []);

  const handleWriteClick = () => {
    navigate('/write');
  }

  return (
      <div>
        <h2>List from Backend</h2>
        <ul>
          {list.filter(item => !item.isDeleted).map((item) => (
              <li key={item.articleId}>
                <h3>
                  <Link to={`/articles/${item.articleId}`}>{item.title}</Link>
                </h3>
                <p>Likes: {item.likes}</p>
                <p>Created At: {item.createdAt}</p>
                <p>Updated At: {item.updatedAt}</p>
              </li>
          ))}
        </ul>
        <button onClick={handleWriteClick}>글쓰기</button>
      </div>
  );
}

export default Articles;