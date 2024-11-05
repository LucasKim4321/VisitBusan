
console.log("boardLike.js start");
console.log('board_id2',board_id);
const heart = document.querySelector('.boardLike .heart')
const heartCount = document.querySelector('.boardLike .heartCount')
console.log('heart',heart);


// 게시글에 대한 좋아요 조회
async function axiosGetBoardLike(params) {
    const response = await axios.get(`/boardLike/read?${params.toString()}`);

    console.log("getBoardLike response: ", response.data);
    return response.data;
}

// 게시글에 대한 좋아요 등록
async function axiosAddBoardLike(boardLikeObj) {
    const response = await axios.post(`/boardLike/create`, boardLikeObj);

    console.log("addBoardLike response: ", response.data);
    return response.data;
}

// 게시글에 대한 좋아요 삭제
async function axiosRemoveBoardLike(params) {
    const response = await axios.delete(`/boardLike/delete?${params.toString()}`);

    console.log("removeBoardLike response: ", response.data);
    return response.data;
}

// 게시글에 대한 좋아요 카운트
async function axiosCountBoardLike(params) {
    const response = await axios.get(`/boardLike/count?${params.toString()}`);

    console.log("countBoardLike response: ", response.data);
    return response.data;
}

// 좋아요 체크
getBoardLike()

// 좋아요 버튼 정의
async function boardLikeBtn(e) {
    e.preventDefault();  // 기본 이벤트 제거
    e.stopPropagation();  // 버블링(현재 이벤트가 발생한 요소의 상위 요소들에 대해서 이벤트 감지되는 현상) 방지

    // const heart = document.querySelector('.boardLike .heart')
    console.log('heart',heart);
    console.log('1')

    if (heart.innerText=='🩶') {
        console.log('2')
        addBoardLike()
    }
    else {
        console.log('3')
        removeBoardLike()
    }
    console.log('4')

}


// 좋아요 카운트
function countBoardLike() {
    // 필요한 데이터를 URL 쿼리 파라미터로 전달
    const params = new URLSearchParams({
        boardId: board_id
    });

    // 비동기 처리방식으로 data 서버에 보내기
    axiosCountBoardLike(params).then(result=> {
        console.log("result: ",result);
        heartCount.innerText = result

    }).catch(e=> alert("에러!\n\n"+e));

}


//-----------------------//
//      좋아요 등록
//-----------------------//
function addBoardLike() {

    if (currentUser==null || currentUser.length==0) {  // 로그인한 사람만 등록 가능. 값이 undefined일 경우 눌 관련 처리만 가능함. length 이런 작업 안됨.
        alert("로그인한 사람만 등록이 가능합니다~");
        return  // 함수 종료
    }

    // 전송할 data를 JSON객체로 선언
    const boardLikeObj = {
        boardId: board_id,                   // 좋아요 누른 게시글
        userId: currentUser,      // 좋아요 누른 사람
    }
    console.log("전송할 JSON객체: ", boardLikeObj);

    // 비동기 처리방식으로 data 서버에 보내기
    axiosAddBoardLike(boardLikeObj).then(result=> {
        console.log("result: ",result);
        console.log("result.userId: ",result.userId);
        heart.innerText = '❤️'
        countBoardLike()
        

    }).catch(e=> alert("에러!\n\n"+e));

}


//-----------------------//
//      좋아요 조회
//-----------------------//


function getBoardLike() {

    // <span> 태그일 경우만 처리
    // 현재 클릭한 태그 요소의 댓글 번호(rno) 추출
    console.log("currentUser1: ",currentUser);
    if(!currentUser) {  // 값이 없으면, 비어있으면
        console.log("currentUser2: ",currentUser);
        return;
    }

    // 필요한 데이터를 URL 쿼리 파라미터로 전달
    const params = new URLSearchParams({
        boardId: board_id,
        userId: currentUser
    });

    // 비동기 처리방식으로 data 서버에 보내기
    axiosGetBoardLike(params).then(result=> {
        console.log("result: ",result);
        console.log("result.id: ",result.id);

        if (result.id!=null) {
            heart.innerText = '❤️'
        }
        else {
            heart.innerText = '🩶'
        }

    }).catch(e=> alert("에러!\n\n"+e));


}

//-----------------------//
//      좋아요 삭제
//-----------------------//
function removeBoardLike() {

    // <span> 태그일 경우만 처리
    // 현재 클릭한 태그 요소의 댓글 번호(rno) 추출
    console.log("currentUser1: ",currentUser);
    if(!currentUser) {  // 값이 없으면, 비어있으면
        console.log("currentUser2: ",currentUser);
        return;
    }

    // 필요한 데이터를 URL 쿼리 파라미터로 전달
    const params = new URLSearchParams({
        boardId: board_id,
        userId: currentUser
    });

    // 비동기 처리방식으로 data 서버에 보내기
    axiosRemoveBoardLike(params).then(result=> {
        console.log("result: ",result);
        console.log("result.userId: ",result.userId);
        heart.innerText = '🩶'
        countBoardLike()

    }).catch(e=> alert("에러!\n\n"+e));


}

