import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';

const ArticleDetail = () => {
  const { id } = useParams(); // URL에서 id 파라미터를 가져옴
  const [article, setArticle] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editContent, setEditContent] = useState('');
  const [editTitle, setEditTitle] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchArticle = async () => {
      try {
        const response = await axios.get(`/articles/${id}`);
        setArticle(response.data);
        setEditTitle(response.data.title); // 제목을 수정할 수 있도록 수정
        setEditContent(response.data.content);
      } catch (error) {
        setError('Error fetching article data');
        console.error('Error fetching article data:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchArticle();
  }, [id]);

  const handleSave = async () => {
    try {
      const response = await axios.put(`/articles/${id}/update`, { title: editTitle, content: editContent });
      if (response.status === 200) {
        setArticle({ ...article, title: editTitle, content: editContent });
        setIsEditing(false);
        alert('내용이 성공적으로 수정되었습니다.');
      }
    } catch (error) {
      console.error('Error saving article data:', error);
      alert('내용을 저장하는 데 실패했습니다.');
    }
  };

  const handleDelete = async () => {
    const confirmDelete = window.confirm('정말로 글을 삭제하시겠습니까?');
    if (confirmDelete) {
      try {
        const response = await axios.put(`/articles/${id}/delete`,
            {isDeleted: true});
        if (response.status === 200) {
          alert('글이 성공적으로 삭제되었습니다.');
          navigate('/articles'); // 글 삭제 후 목록 페이지로 리디렉션
        }
      } catch (error) {
        console.error('Error deleting article:', error);
        alert('글을 삭제하는 데 실패했습니다.');
      }
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  return (
      <div>
        {isEditing ? (
            <ReactQuill value={editTitle} onChange={setEditTitle} />
        ) : (
          <h2>{article.title}</h2>
        )}
        {isEditing ? (
            <ReactQuill value={editContent} onChange={setEditContent} />
        ) : (
            <p>{article.content}</p>
        )}
        <p>Hits: {article.hits}</p>
        <p>Likes: {article.likes}</p>
        <p>Created At: {article.createdAt}</p>
        <p>Updated At: {article.updatedAt}</p>
        {isEditing ? (
            <button onClick={handleSave}>저장</button>
        ) : (
            <button onClick={() => setIsEditing(true)}>편집</button>
        )}
        <button onClick={() => navigate(-1)}>뒤로가기</button> {/* 뒤로가기 버튼 추가 */}
        <button onClick={handleDelete}>삭제</button>
      </div>
  );
}

export default ArticleDetail;