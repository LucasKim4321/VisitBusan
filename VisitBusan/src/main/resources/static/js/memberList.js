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

/* 모달창 */
const informationModal = document.querySelector('.informationModal')
const modal = new bootstrap.Modal(informationModal);
const buttonBox1 = informationModal.querySelector('.buttonBox1')
const buttonBox2 = informationModal.querySelector('.buttonBox2')
const dateBox = informationModal.querySelector('.dateBox')
const passwordBox = informationModal.querySelector('.passwordBox')

/* 등록 버튼 동작 */
document.querySelector('.createBtn1').addEventListener('click', function(e) {
    e.preventDefault();  // 기본 이벤트 제거
    e.stopPropagation();  // 버블링(현재 이벤트가 발생한 요소의 상위 요소들에 대해서 이벤트 감지되는 현상) 방지

    // 태그 수정
    buttonBox1.hidden= false;
    buttonBox2.hidden= true;
    dateBox.hidden=true;
    passwordBox.hidden=false;
    document.getElementById('inputUserId').disabled=false;
    document.getElementById('inputRole').disabled = false;

    // 모달 데이터 초기화
    const modalInputs = informationModal.getElementsByTagName('input')
    for (const input of modalInputs) {
        input.value='';
    }
    document.getElementById('inputRole').value = 'USER';

    modal.show();
    
})

// 모달 회원 등록 버튼 동작
informationModal.querySelector('.createBtn2').addEventListener('click', function(e) {
    console.log("click");
    
    const memberData = {
        userId: document.getElementById('inputUserId').value,
        password: document.getElementById('inputPassword').value,
        roleSet: [document.getElementById('inputRole').value],
        name: document.getElementById('inputName').value,
        email: document.getElementById('inputEmail').value,
        address: document.getElementById('inputAddress').value
    };

    // 서버에 POST 요청을 보냅니다.
    fetch(`/admin/member/create`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(memberData)
    })
    .then(response => {
        if (response.ok) {
            window.location.href = "/admin/member/list";
        } else {
            throw new Error("서버 응답에 문제가 있습니다.");
            // throw new Error(response.body);
        }
    })
    .then(data => {
        console.log("회원이 등록되었습니다");
        modal.hide(); // 모달 닫기
    })
    .catch(error => {
        console.error("Error: ", error);
    });
})

/* 멤버 링크 동작 */
    const memberTable = document.querySelector('.cate_1 .table');
    const memberRows = memberTable.getElementsByTagName('tr');

// 각 행에 클릭 이벤트 리스너 추가
for (let i = 1; i < memberRows.length; i++) { // 헤더를 제외하기 위해 i를 1로 시작
    memberRows[i].addEventListener('click', function(e) {
        console.log("list click")
        const userId = this.getAttribute('data-userId');

        // 태그 수정
        buttonBox1.hidden= true;
        buttonBox2.hidden= false;
        dateBox.hidden=false;
        passwordBox.hidden=true;
        document.getElementById('inputUserId').disabled=true;

        // 유저 데이터 불러오기
        console.log("userId",userId)
        if (userId!=null||userId.length>0) {
            fetch(`/admin/member/read/${userId}?`, {
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
                document.getElementById('inputUserId').value = data.userId;
                if (data.roleSet.includes('ROOT')){
                    document.getElementById('inputRole').value = 'ROOT';
                    document.getElementById('inputRole').disabled = true;
                }
                else {
                    document.getElementById('inputRole').value = data.roleSet[0];
                    document.getElementById('inputRole').disabled = false;
                }
                document.getElementById('inputName').value = data.name;
                document.getElementById('inputEmail').value = data.email;
                document.getElementById('inputAddress').value = data.address;
                document.getElementById('inputDate').value = data.regDate;

                modal.show();
            })
            .catch(error => {
                console.error('Error fetching member data:', error);
                 alert('회원 정보를 불러오는데 문제가 발생했습니다.');
            });
        }

    });
    
};

// 모달 회원 수정 버튼 동작
document.querySelector('.modBtn').addEventListener('click', function(e) {
    e.preventDefault();  // 기본 이벤트 제거
    e.stopPropagation();  // 버블링(현재 이벤트가 발생한 요소의 상위 요소들에 대해서 이벤트 감지되는 현상) 방지
    console.log("modBtn click")

    const memberData = {
        userId: document.getElementById('inputUserId').value,
        roleSet: [document.getElementById('inputRole').value],
        name: document.getElementById('inputName').value,
        email: document.getElementById('inputEmail').value,
        address: document.getElementById('inputAddress').value
    };
    console.log(memberData)

    // 서버에 POST 요청을 보냅니다.

    // 필요한 데이터를 URL 쿼리 파라미터로 전달
    // const params = new URLSearchParams({
    //     userId: document.getElementById('inputUserId').value,
    //     roleSet: document.getElementById('inputRole').value,
    //     name: document.getElementById('inputName').value,
    //     email: document.getElementById('inputEmail').value,
    //     address: document.getElementById('inputAddress').value
    // });
    // fetch(`/admin/member/modify?${params.toString()}`, {
    fetch('/admin/member/modify', {
        method: 'POST',
        headers: {
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
        modal.hide(); // 모달 닫기
    })
    .catch(error => {
        console.error("Error: ", error);
    });
});

// 모달 회원 삭제 버튼 동작
document.querySelector('.removeBtn').addEventListener('click', function(e) {
    e.preventDefault();  // 기본 이벤트 제거
    e.stopPropagation();  // 버블링(현재 이벤트가 발생한 요소의 상위 요소들에 대해서 이벤트 감지되는 현상) 방지

    const userId = document.getElementById('inputUserId').value;
    // 서버에 POST 요청을 보냅니다.
    if(document.getElementById('inputRole').value!='ROOT') {
        fetch(`/admin/member/delete?userId=${userId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then(response => {
            if (response.ok) {
                window.location.href = "/admin/member/list";
            } else {
                throw new Error("서버 응답에 문제가 있습니다.");
                // throw new Error(response.body);
            }
        })
        .then(data => {
            console.log("회원이 삭제되었습니다");
            modal.hide(); // 모달 닫기
        })
        .catch(error => {
            console.error("Error: ", error);
        });
    }
})
