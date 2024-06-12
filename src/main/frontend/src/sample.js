import axios from 'axios';

const list = 'http://localhost:8080/list';
const write = 'http://localhost:8080/write';

export const getPosts = async () => {
  const response = await axios.get(list);
  return response.data;
};

export const createPost = async (post) => {
  const response = await axios.post(write, post);
  return response.data;
};
