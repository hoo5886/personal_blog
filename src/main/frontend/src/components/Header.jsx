import "./Header.css";

const Header = () => {
  return (
      <header className="Header">
        <div className="header_left">개인 블로그</div>
        <div className="header_right">
          <div className="img_section">
            <img src="../resources/mhface.jpg"/>
          </div>
          <div className="info_section">
            김명후
          </div>
        </div>
      </header>
  );
};

export default Header;