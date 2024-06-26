import ReactQuill from "react-quill";
import {useEffect, useState} from "react";
import 'react-quill/dist/quill.snow.css'; // Make sure to import the CSS
import "./Write.css";

function Write() {
  const [content, setContent] = useState("");
  const [files, setFiles] = useState([]); // [File, File, ...

  const modules = {
    toolbar: {
      container: [
        [{ 'header': '1'}, {'header': '2'}, { 'font': [] }],
        [{size: []}],
        ['bold', 'italic', 'underline', 'strike', 'blockquote'],
        [{'list': 'ordered'}, {'list': 'bullet'},
          {'indent': '-1'}, {'indent': '+1'}],
        ['link', 'image'],
        ['clean']
      ],
    }
  };

  const handleChange = (value) => {
    setContent(value);
  }

  const handlePaste = async (event) => {
    const clipboardItems = event.clipboardData.items;
    for (let i = 0; i < clipboardItems.length; i++) {
      const item = clipboardItems[i];
      if (item.type.indexOf("image") !== -1) {
        const blob = item.getAsFile();
        setFiles((prevFiles) => [...prevFiles, blob]);
      }
    }
  };

  useEffect(() => {
    document.addEventListener("paste", handlePaste);
    return () => {
      document.removeEventListener("paste", handlePaste);
    };
  }, []);

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

    const formData = new FormData();
    formData.append('article', new Blob([JSON.stringify(postData)], { type: 'application/json' }));

    for (let i = 0; i < files.length; i++) {
      formData.append("files", files[i]);
    }

    try {
      const response = await fetch("/write", {
        method: "POST",
        body: formData
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

  const handleFileChange = (e) => {
    setFiles(e.target.files);
  }

  return (
      <div className="container">
        <div className="title-section">
          <label>제목: </label>
          <input
              id="title"
              type="text"
              placeholder="제목을 입력해주세요."
          />
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
          <input
              type="file"
              multiple
              onChange={handleFileChange}
          /> {/* 파일 첨부 input 추가 */}
          <button onClick={handleClick} type="button">작성 완료</button>
        </div>
      </div>
  );
}

export default Write;