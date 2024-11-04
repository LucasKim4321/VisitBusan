
console.log("/js/reply.js.....")

// ------------------------------------------------------ //
// 특정 게시글에 대한 댓글 조회 : axios(비동기) 요청 테스트
// ------------------------------------------------------ //
 async function getReply(board_id){
  console.log("board_id:",board_id);

  const result = await axios.get(`/replies/list/${board_id}`)
//  console.log("getReply(): ",result)
//  console.log("getReply() data: ",result.data)
//  console.log("getReply() data.list: ",result.data.list)
  return result.data;
}

// ------------------------------------------------------------  //
// 1.게시글에 대한 댓글 List, 인자값이 여러개 전달 받을 경우=> {데이터1,..}
// ------------------------------------------------------------  //
 async function getList({board_id, page, size, goLast}){
  const result = await axios.get(
                                  `/replies/list/${board_id}`,
                                  { params: {page, size} })
  console.log("axios: getList() data: ",result)

  //onst total = result.data.total; // 댓글 총 개수
  // 댓글 마지막 페이지 계산 = 댓글 총개수/페이지 사이즈 => 자리올림
  //const lastPage = parseInt(Math.ceil(total/size));

//  console.log("total:"+total)
//  console.log("lastPage:"+lastPage)

  if (goLast) {// treu이면 가장 최근글 있는 페이지(막지막) 번호 재요청
    // 댓글 총 개수
    const total = result.data.total;
    // 댓글 마지막 페이지 계산 = 댓글 총개수/페이지 사이즈 => 자리올림
    const lastPage = parseInt(Math.ceil(total/size));


    return getList({board_id, page:lastPage, size:size});
  }

  return result.data;
}

// ------------------------------------------------------------  //
// 2.게시글에 대한 댓글 등록
// ------------------------------------------------------------  //
async function addReply(replyObj){
  const response = await axios.post(`/replies/`, replyObj);

   console.log("addReply response:", response.data);
  return response.data
}
// ------------------------------------------------------------  //
// 3.게시글에 대한 댓글 조회
// ------------------------------------------------------------  //
async function getReply(id){
  const response = await axios.get(`/replies/${id}`);
  //console.log("addReply response:", response.data);
  return response.data
}

// ------------------------------------------------------------  //
// 4.게시글에 대한 댓글 수정
// ------------------------------------------------------------  //
async function modifyReply(replyObj){
  const response = await axios.put(`/replies/${replyObj.id}`, replyObj);
  //console.log("addReply response:", response.data);
  return response.data
}
// ------------------------------------------------------------  //
// 5.게시글에 대한 댓글 삭제
// ------------------------------------------------------------  //
async function removeReply(id){
  const response = await axios.delete(`/replies/${id}`);
  //console.log("addReply response:", response.data);
  return response.data
}


// ------------------------ //
//        reply
// ------------------------ //

const replyBIdList = document.querySelector('.replies'); // 댓글 목록 DOM
const replyPaging = document.querySelector('.replyPaging');// 페이지 목록 DOM
const board_id = document.querySelector('.dataCon').getAttribute('data-id');
const currentUserTag = document.querySelector('.dataCon .currentUser');
let currentUser;
if (currentUserTag!=null) {
  currentUser = currentUserTag.getAttribute('data-currentUser')
}
console.log('currentUser',currentUser)

//------------------------------------------------------------------------ //
// 4. 페이징 처리
//------------------------------------------------------------------------ //
function printPages(data){
    console.log("paging:", data);


    // pagination
    let pageStr = ''

    if (data && data.dtoList.length > 0 ){  // 댓글이 있을 경우만  페이징 처리

      // 이전 버튼
      if (data.prev) {
        pageStr += `<li class="page-item"><a class="page-link" data-page="${data.start-1}">이전</a></li>`
      }

      // 페이지번호 버튼
      for (let i=data.start; i<=data.end; i++){
        pageStr += `<li class="page-item ${i==data.page?"active":""}"><a class="page-link" data-page="${i}">${i}</a></li>`
      }
      // 다음 버튼
      if (data.next) {
      pageStr += `<li class="page-item"><a class="page-link" data-page="${data.end+1}">다음</a></li>`
      }

    } // end if

    replyPaging.innerHTML = pageStr;

}

// 페이지 번호 클릭 이벤트
replyPaging.addEventListener('click', function(e) {
    e.preventDefault();
    const target = e.target;
    if (target.tagName === 'A') {
        const pageNum = target.getAttribute('data-page');
        printReplies(pageNum, size, false); // 새로운 페이지로 댓글을 불러옵니다.
    }
});


// 3. 서버로 부터 받아온 댓글을 DOM을 구성할 수 있는 문자열 생성 및 DOM생성
function printList(replies){
    let str ='';

    // 댓글이 1개 이상이면 처리 댓글 목록 가져와서 View처리
    if (replies && replies.length > 0 ){

      for (const dto of replies){
        str +=
        `
          <li class="reply d-flex mt-3 gap-3">

                    <div class="imgBox p-2">
                        <div class="imgdiv rounded-circle mx-auto"><img th:src="|@{/images/profileImage/}gwanganli2.jpg|"></div>
                    </div>
                    <div class="p-2">

                            <div>작성자</div>
                            <div class="reply_id" hidden>${dto.id}</div>
                            <div>${dto.replyText}</div>
                            <div class="d-flex gap-3">
                                <div>${dto.regDate}</div>
                                <div>답글쓰기</div>
                                <div>좋아요</div>
                            </div>
                    </div>
                    <div class="ms-auto p-2 d-flex gap-3">
                        <button type="button" class="btn btn-outline-secondary modBtn" onClick="modifyReply(${dto.id})">수정</button>
                        <button type="button" class="btn btn-outline-secondary delBtn" onClick="removeReply(${dto.id})">삭제</button>
                    </div>



          </li>
        `
      }// end for
    }// end if
    replyBIdList.innerHTML = str;
}

// 2. 게시글에 대한 댓글 서버에 요청해서 data 받아오기
function printReplies(page, size, goLast) {
    getList({ board_id, page, size, goLast })
        .then(data => {
            printList(data.dtoList); // 댓글 목록을 렌더링합니다.
            printPages(data); // 페이지 번호를 렌더링합니다.
        })
        .catch(e => console.error(e));
}


let page = 1; // 현재 페이지 번호
let size = 5; // 페이지해당 되는 레코드(행)의 개수
printReplies(page,size,true);// 현재 페이지번호, 페이지 사이즈, 마지막 페이지 사용여부

//--------------------------------------------- //
// 5-1. 페이지 번호 클릭
//--------------------------------------------- //
replyPaging.addEventListener('click', function(e){
    e.preventDefault();
    e.stopPropagation();

    const target = e.target;
    if (!target || target.tagName != 'A'){
      return;
    }

    // <a>태그일 경우만 처리
    // 현재 클릭한 태그 요소의 페이지 번호 추출
    const pageNum = target.getAttribute("data-page");
    page = pageNum;
    console.log("page number:",page)
    printReplies(page, size)

});

// 댓글 등록 버튼
const replyText = document.querySelector('.replyText')
const regDate = document.querySelector('.regDate')

document.querySelector('.regBtn').addEventListener('click', function(e) {
    console.log("regBtn click");

    // data -> JSON
    const replyObj = {
        board_id: board_id,
        replier: "test",   <!--/*[[${user.name}]]*/-->
        replierId: "test",   <!--/*[[${user.username}]]*/-->
        replyText: replyText.value
    }

    console.log("전송할 JSON:", replyObj);

    // data 서버에 보내기
    addReply(replyObj)
            .then(result => {
                console.log("response data:", result)


                replyText.value='';

                printReplies(1,5,true)
            })
            .catch(e => alert(e))
});

//--------------------------------------------- //
// 5-3. 댓글 조회 및 수정
//--------------------------------------------- //



// 댓글 수정
function modifyReply(id) {
    const replyText = prompt("수정할 댓글을 입력하세요:");

    if (replyText.trim() === "") {
        alert("댓글 내용을 입력해주세요.");
        return;
    }

    const replyObj = {
        replyText: replyText
    };

    // 서버로 PUT 요청을 보냄
    axios.put(`/replies/${id}`, replyObj)
        .then(response => {
            alert("댓글이 수정되었습니다.");
            // 댓글 목록 다시 불러오기
            printReplies(page, size, false);
        })

        .catch(error => {
            alert("댓글 수정에 실패했습니다.");
            console.error("Error updating reply:", error);
        });
}

// 댓글 삭제
function removeReply(id) {
    if (!confirm("댓글을 삭제하시겠습니까?")) {
        return;
}

// 서버로 DELETE 요청을 보냄
axios.delete(`/replies/${id}`)
    .then(response => {
        alert("댓글이 삭제되었습니다.");
        // 댓글 목록 다시 불러오기
        printReplies(page, size, false);
    })

    .catch(error => {
        alert("댓글 삭제에 실패했습니다.");
        console.error("Error deleting reply:", error);
    });
}