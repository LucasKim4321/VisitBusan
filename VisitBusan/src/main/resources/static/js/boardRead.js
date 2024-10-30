console.log("스크립트 실행! :b");

const dataCon = document.querySelector('.dataCon');
const errors = dataCon.getAttribute('data-errors');
const menu = dataCon.getAttribute('data-menu');
const readLink = dataCon.getAttribute('data-removeLink');
const link = dataCon.getAttribute('data-link');
const linkRemove = dataCon.getAttribute('data-link-remove');
const dto_id = dataCon.getAttribute('data-id');
const fileNames = document.querySelectorAll('.dataCon .fileName');
console.log("errors: " ,errors)
console.log("menu: " ,menu)
console.log("link: " ,link)
console.log("dto_id: " ,dto_id)
console.log("fileNames: " ,fileNames)

// textarea 줄 높이에 맞게 자동 변경
const content = document.querySelector('.content');
content.style.height = 'auto';  //height 초기화
content.style.height = content.scrollHeight+5+'px';

// ------------------------ //
// 서버 첨부파일 삭제 함수 정의
// ------------------------ //

function callRemoveFiles() {

    // 삭제할 첨부파일 목록이 없으면 삭제 작업 중단
    if (removeFileList.length == 0) return;

    // removeFileList 구조 [{uuid, fileName}, {uuid, fileName}, ...]
    console.log("removeFileList: ",removeFileList);
    console.log("removeFileList[0].uuid: ",removeFileList[0].uuid);
    console.log("removeFileList[0].fileName: ",removeFileList[0].fileName);

    // 삭제할 첨부파일 목록: [{xx.xx},...]
    removeFileList.forEach( ({uuid, fileName}) => {
        // 업로드파일 삭제 요청: axios
        removeFileToServer({uuid, fileName})
            .then(response => {
                // 삭제후 서버로부터 응답
                console.log(response)

            }).catch( e => {
                alert("오류입니다~");
                console.log("오류입니다~ ",e);
            });

    })

}


// ------------------------ //
//        삭제 버튼
// ------------------------ //

const removeFileList = [];
function removeFun(e) {
    e.preventDefault();  // 기본 이벤트 제거
    e.stopPropagation();  // 버블링(현재 이벤트가 발생한 요소의 상위 요소들에 대해서 이벤트 감지되는 현상) 방지

    let doubleChk = confirm('삭제하겠습니까?');
    if (doubleChk) {

        // 첨부파일 이미지 목록속성만 추출하여 삭제목록에 저장 => 전역배열 => removeFileList
        if (fileNames.length > 0) {
            console.log('fileNames', fileNames)

            for (fName of fileNames){
              const arr = fName.innerText.split("==vb==")
              const uuid = arr[0]
              const fileName = arr[1]

              //console.log(uuid, fileName)
              // 삭제할 첨부파일 목록에 저장된 => callRemoveFiles()메서드에서 삭제요청
              // 구조분해할당형식의 값전달 시: 보내쪽과 받는쪽의 매개변수가 동일해야 함.
              removeFileList.push({uuid,fileName})
            }

            //console.log("remove button :"+removeFileList)
            //console.log("remove button :"+removeFileList[0])
        }

        callRemoveFiles();

        const formObj = document.querySelector('.removeForm');
        // formObj.action = `/board/${menu}/remove?id=${dto_id}&${link}`;
        formObj.action = linkRemove;
        formObj.method = "post";
        formObj.submit();
    }
}

// 게시글 링크 복사
function copyLink() {
    // console.log(window.location.href)
    // console.log(window.location.protocol)
    // console.log(window.location.hostname)
    // console.log(window.location.port)
    const hostName = window.location.hostname;
    const port = window.location.port;
    navigator.clipboard.writeText(`${hostName}:${port}${readLink}?id=${dto_id}`).then(() => {
        alert("링크가 복사되었습니다!");
    }).catch(err => {
        alert("복사에 실패했습니다.");
        console.error("복사 오류:", err);
    });
}
