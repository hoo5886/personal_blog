import ReactQuill from "react-quill";
function Write() {
  const modules = {
    toolbar: {
      container: [
        ["image"],
        [{ header: [1, 2, 3, 4, 5, false] }],
        ["bold", "underline"],
      ],
    },
  };
  return (
      <>
        <div>
          <label>제목: </label>
          <input id="title" type="text" placeholder="제목을 입력해주세요."/>
          <ReactQuill
              style={{ width: "1400px", height: "400px" }}
              modules={modules}
          />
        </div>
      </>
  );
}
export default Write;