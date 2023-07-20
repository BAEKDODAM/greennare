import { styled } from 'styled-components';
import { useEffect, useState } from 'react';
import { UploadReview } from './UploadReview';
import moment from 'moment';
import 'moment/locale/ko';
import API from '../api/index';
import { ReviewModal } from './ReviewModal';

interface ReviewProps {
  id: number;

  context: string;
  createdAt: string;
  image?: string[];
  name: string;
  point: number;
}

export const Review = ({
  id,
  context,
  createdAt,
  image,
  name,
  point,
}: ReviewProps) => {
  const [isEdit, setIsEdit] = useState(false);
  //모달
  const [isOpen, setIsOpen] = useState(false);
  const [isAlert, setIsAlert] = useState(true);
  const [modalContent, setModalContent] = useState('');
  // 임시
  const username = 'name1';

  const onDeleteReview = () => {
    setModalContent('정말 삭제하시겠습니까?');
    setIsAlert(false);
    setIsOpen(true);
  };

  const deleteReview = async () => {
    try {
      const res = await API.DELETE({
        url: `http://greennarealb-281283380.ap-northeast-2.elb.amazonaws.com/green/review/${id}`,
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      console.log('delete review');
      console.log(res.data);

      setModalContent('리뷰가 삭제되었습니다.');
      setIsAlert(true);
      setIsOpen(true);
    } catch (err) {
      console.log('delete review err');
      console.log(err);

      setModalContent('리뷰 삭제에 실패하였습니다.');
      setIsAlert(true);
      setIsOpen(true);
    }
  };

  const handleConfirm = () => {
    setIsOpen(false);
    deleteReview();
    location.reload();
  };
  const handleClose = () => {
    setIsOpen(false);
  };

  return (
    <ReviewWrapper>
      {isOpen ? (
        <ReviewModal
          isAlert={isAlert}
          content={modalContent}
          onConfirm={handleConfirm}
          onClose={handleClose}
        />
      ) : null}
      <div className="userInfoWrapper">
        <UserInfo>
          <div className="userName">{`🐥 ${name}`}</div>
          <div className="point">{`🏆 ${point}P`}</div>
          <div className="reviewDate">{`⏱️ ${moment(
            createdAt,
          ).fromNow()}`}</div>
        </UserInfo>
        {name === username && !isEdit ? (
          <div>
            <Button onClick={() => setIsEdit(true)}>수정</Button>
            <Button onClick={() => onDeleteReview()}>삭제</Button>
          </div>
        ) : null}
      </div>
      {isEdit ? (
        <ContentWrapper>
          <UploadReview
            id={id}
            isEdit={isEdit}
            setIsEdit={setIsEdit}
            memberId={name}
            content={context}
          />
        </ContentWrapper>
      ) : (
        <ContentWrapper>
          <div className="content">{context}</div>
        </ContentWrapper>
      )}
    </ReviewWrapper>
  );
};

const ReviewWrapper = styled.li`
  list-style: none;

  border: none;
  box-shadow: rgba(0, 0, 0, 0.3) 1px 1px 4px;
  border-radius: 0.5rem;

  padding: 1rem;
  margin: 1rem 0;

  .userInfoWrapper {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
`;

const UserInfo = styled.div`
  display: flex;
  align-items: center;

  & > * {
    margin-right: 1rem;
  }

  .userName {
    font-weight: bold;
  }

  .point {
    font-weight: bold;
    color: var(--green-200);
  }

  .reviewDate {
    color: var(--gray);
    margin-right: 1rem;
  }
`;

const ContentWrapper = styled.div`
  display: flex;
  align-items: center;
  margin: 0.5rem 0;

  .content {
    width: 100%;
  }
`;

const Button = styled.button`
  cursor: pointer;

  border: none;

  background-color: transparent;
  color: var(--gray);

  width: 3rem;
  padding: 0.5rem;
  margin-left: 0.3rem;

  &:hover {
    color: var(--green-200);
  }
`;
