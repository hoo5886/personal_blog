import ReactQuill from "react-quill";
import { useState } from "react";
import 'react-quill/dist/quill.snow.css'; // Make sure to import the CSS
import "./Write.css";

function Write() {
  const [content, setContent] = useState("");

  const modules = {
    toolbar: {
      container: [
        ["image"],
        [{ header: [1, 2, 3, 4, 5, false] }],
        ["bold", "underline"],
      ],
    },
  };

  const handleChange = (value) => {
    setContent(value);
  }

  const handleClick = async () => {
    const title = document.getElementById("title").value;

    if (!title || !content) {
      alert("제목 또는 내용을 입력해주세요.");
      return;
    }

    const postData = {
      title: title,
      content: content
    };

    try {
      const response = await fetch("/write", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(postData),
      });

      if (response.ok) {
        alert("작성이 완료되었습니다.");
        setContent("");
        document.getElementById("title").value = "";
        window.location.href = "/articles";
      } else {
        alert("작성에 실패했습니다.");
      }
    } catch (error) {
      console.error("Error adding post:", error);
      alert("작성에 실패했습니다.");
    }
  }

  return (
      <div className="container">
        <div className="title-section">
          <label>제목: </label>
          <input id="title" type="text" placeholder="제목을 입력해주세요."/>
        </div>
        <div className="editor-container">
          <ReactQuill
              style={{width: "100%", height: "400px"}}
              modules={modules}
              value={content}
              onChange={handleChange}
          />
        </div>
        <div className="button-container">
          <button onClick={handleClick} type="button">작성 완료</button>
        </div>
      </div>
  );
}

export default Write;