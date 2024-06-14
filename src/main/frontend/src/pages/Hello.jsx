import React, { useEffect, useState } from 'react';
import axios from 'axios';

const Hello = () => {
  const [message, setMessage] = useState('');

  useEffect(() => {
    const fetchHello = async () => {
      try {
        const response = await axios.get('/hello');
        setMessage(response.data);
      } catch (error) {
        console.error('Error fetching hello message:', error);
      }
    };

    fetchHello();
  }, []);

  return (
      <div>
        <h1>Message from Backend</h1>
        <p>{message}</p>
      </div>
  );
}

export default Hello;