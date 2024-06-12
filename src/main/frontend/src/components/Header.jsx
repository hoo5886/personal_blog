import "./Header.css";

const Header = () => {
  return (
      <header className="Header">
        <div className="header_left">명후세계</div>
        <div className="header_right">
          <div className={`img_section img_section_${emotionId}`}>
            <img src="../resources/mhface.jpg"/>
          </div>
          <div className="info_section">
            <div className="created_date">
              {new Date().toLocaleDateString()}
            </div>
          </div>
        </div>
      </header>
  );
};

export default Header;