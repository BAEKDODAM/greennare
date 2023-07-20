import { styled } from 'styled-components';
import { Link, useNavigate } from 'react-router-dom';
import logo from '../../assets/img/logo.png';
// import { SearchBar } from '../../feature/SearchBar';
import { useSetAtom } from 'jotai';
import { menuAtom } from 'jotai/atom';
import { AfterLogin } from './AfterLogin';
import { BeforeLogin } from './BeforeLogin';

const StyledHeaderContainer = styled.nav`
  display: flex;
  background-color: var(--white);
`;
const StyledLogo = styled.img`
  margin: 0.625rem;
`;

const StyledChoicePage = styled.div`
  margin: 0.625rem;
  display: flex;
  justify-content: center;
  align-items: center;
`;
const StyledGreen = styled(Link)`
  color: var(--black);
  text-decoration: none;
  font-size: 1.3rem;
`;
const StyledNare = styled(Link)`
  color: var(--black);
  text-decoration: none;
  font-size: 1.3rem;
`;
const StyledNavContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex: 1;
`;

const StyledSpan = styled.span`
  &:hover {
    cursor: pointer;
    font-weight: bold;
  }

  &.focused {
    font-weight: bold;
  }
`;

export const Header = () => {
  const setCurrentMenu = useSetAtom(menuAtom);
  const accessToken = localStorage.getItem('accessToken');
  return (
    <div>
      <StyledHeaderContainer>
        <StyledLogo src={logo}></StyledLogo>
        <StyledChoicePage>
          <StyledGreen to={'/'}>
            <StyledSpan onClick={() => setCurrentMenu('')}>그린</StyledSpan>
          </StyledGreen>
          ㅣ{/* 그린 라우팅 주소 입력*/}
          <StyledNare to={'/challenge'}>
            <StyledSpan onClick={() => setCurrentMenu('챌린지')}>
              나래
            </StyledSpan>
          </StyledNare>
          {/* 나래 라우팅 주소 입력*/}
        </StyledChoicePage>
        <StyledNavContainer>
          {/* <SearchBar></SearchBar> */}
          {accessToken ? <AfterLogin /> : <BeforeLogin />}
        </StyledNavContainer>
        {/* 로그인, 회원가입 버튼 자리*/}
      </StyledHeaderContainer>
    </div>
  );
};
