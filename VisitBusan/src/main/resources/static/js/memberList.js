console.log("스크립트 실행2! :b");

// 페이징 번호 클릭시 처리하는 함수
document.querySelector('.my_pagination').addEventListener('click',function(e) {
    e.preventDefault();  // 기본 이벤트 제거
    e.stopPropagation();  // 버블링(현재 이벤트가 발생한 요소의 상위 요소들에 대해서 이벤트 감지되는 현상) 방지

    console.log('e: ',e);

    const target = e.target;
    if (target.tagName != 'A') {
        console.log("<a>태그가 아니라고!!");
        return;  // <a>태그가 아니면 종료
    }

    const num = target.getAttribute('data-num');  // 현재 클릭된 요소의 data-num을 읽어옴

    // 검색 기능 폼(form)에서 전송

    // document.querySelector('form');  // 폼이 하나면 이렇게 해도 상관없음 // 나중을 위해 비추(까먹고있다가 한참 찾야함)
    const formObj = document.querySelector('.searchForm');

    formObj.innerHTML += `<input type='hidden' name='page' value='${num}'>`
    formObj.submit();  // 전송

    // location.href="/board/list?page="+num  // 클릭한 페이지 번호

}) /* end pagination listener */

/* 게시글 링크 동작 */
const table = document.querySelector('.table');
const rows = table.getElementsByTagName('tr');

const informationModal = new bootstrap.Modal(document.querySelector('.informationModal'));

// 각 행에 클릭 이벤트 리스너 추가
for (let i = 1; i < rows.length; i++) { // 헤더를 제외하기 위해 i를 1로 시작
    rows[i].addEventListener('click', function(e) {
        console.log("list click")
        const userId = this.getAttribute('data-userId');
        if (userId) {
            // 필요한 데이터를 URL 쿼리 파라미터로 전달
            const params = new URLSearchParams({
                userId: document.getElementById('inputUserId').value,
                name: document.getElementById('inputName').value,
                email: document.getElementById('inputEmail').value,
                address: document.getElementById('inputAddress').value
            });
            fetch(`/admin/member/read/${userId}?${params.toString()}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                }
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();  // 응답 데이터를 JSON으로 변환
            })
            .then(data => {
                // 성공적으로 데이터를 받았을 때 처리
                console.log("데이터 : ", data);
                console.log("roleSet : ", data.roleSet);
                console.log("roleSet.indexOf : ", data.roleSet.indexOf('ROOT'));
                document.getElementById('inputUserId').value = data.userId;
                if (data.roleSet.includes('ROOT')){
                    document.getElementById('inputRole').value = 'ROOT';
                }
                else {
                    document.getElementById('inputRole').value = data.roleSet[0];
                }
                document.getElementById('inputName').value = data.name;
                document.getElementById('inputEmail').value = data.email;
                document.getElementById('inputAddress').value = data.address;
                document.getElementById('inputDate').value = data.regDate;

                informationModal.show();
            })
            .catch(error => {
                console.error('Error fetching member data:', error);
                 alert('회원 정보를 불러오는데 문제가 발생했습니다.');
            });
        }
    });
};

// 수정
document.querySelector('.modBtn').addEventListener('click', function(e) {
    e.stopPropagation();
    console.log("modBtn click")
    let roleSet;
    if (document.getElementById('inputRole').value.includes('ROOT')) {
        roleSet=['ROOT','ADMIN'];
    }
    else{
        roleSet=[document.getElementById('inputRole').value];
    }
    const memberData = {
        userId: document.getElementById('inputUserId').value,
        roleSet: roleSet,
        name: document.getElementById('inputName').value,
        email: document.getElementById('inputEmail').value,
        address: document.getElementById('inputAddress').value
    };
    console.log(memberData)

    // 서버에 POST 요청을 보냅니다.
    fetch('/admin/member/list/modify', {
        method: 'POST',
        headers: {
            //'Content-Type': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(memberData)
    })
    .then(response => {
        if (response.ok) {
            window.location.href = "/admin/member/list"; // 응답 텍스트를 반환
        } else {
            throw new Error("서버 응답에 문제가 있습니다.");
        }
    })
    .then(data => {
        console.log("회원 정보가 수정되었습니다");
        informationModal.hide(); // 모달 닫기
    })
    .catch(error => {
        console.error("Error: ", error);
    });
});

