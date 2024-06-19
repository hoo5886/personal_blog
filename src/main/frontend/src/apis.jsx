// src/api/index.js
import axios from 'axios';

// 공통 설정을 위한 axios 인스턴스 생성
const api = axios.create({
  baseURL: 'http://localhost:8080', // 스프링 부트 서버의 기본 URL
});

// /hello 엔드포인트 호출 함수
export const fetchHello = async () => {
  const response = await api.get('/hello');
  return response.data;
};

// /list 엔드포인트 호출 함수
export const fetchArticles = async () => {
  const response = await api.get('/articles');
  return response.data;
};