console.log("스크립트 실행! :b");

var contextPath = document.querySelector('.dataCon').getAttribute('data-contextPath');
console.log("contextPath2",contextPath);
const dataCon = document.querySelector('.dataCon');
const errors = dataCon.getAttribute('data-errors');
const menu = dataCon.getAttribute('data-menu');
const link = dataCon.getAttribute('data-link');
const linkRead = dataCon.getAttribute('data-link-read');
const dto_id = dataCon.getAttribute('data-id');
const fileNames = document.querySelectorAll('.dataCon .fileName');
console.log("errors: " ,errors)
console.log("menu: " ,menu)
console.log("link: " ,link)
console.log("dto_id: " ,dto_id)
console.log("fileNames: " ,fileNames)

// 툴박스
const toolBox = document.querySelector('.toolBox .UIBox');
toolBox.addEventListener("click", (e)=> {
    e.preventDefault();  // 기본 이벤트 제거
    e.stopPropagation();  // 버블링(현재 이벤트가 발생한 요소의 상위 요소들에 대해서 이벤트 감지되는 현상) 방지
})

const content = document.querySelector('.content');
const rowCountDisplay = document.getElementById("rowCount");
const textCountDisplay = document.getElementById("textCount");

handleResizeHeight();

function handleResizeHeight() {
    content.style.height = 'auto';
    content.style.height = content.scrollHeight + 'px';

    const style = window.getComputedStyle(content);
    const lineHeight = parseFloat(style.lineHeight); // 줄 높이
    const scrollHeight = content.scrollHeight; // 실제 높이

    // 총 행 수 = 총 높이 / 줄 높이
    const lineCount = Math.floor(scrollHeight / lineHeight);
    rowCountDisplay.textContent = lineCount;

    // 글자수 표시
    textCountDisplay.textContent = content.value.length;

}

// 취소 버튼
document.querySelector('.cancelBtn').addEventListener('click', function(e) {
    e.preventDefault();  // 기본 이벤트 제거
    e.stopPropagation();  // 버블링(현재 이벤트가 발생한 요소의 상위 요소들에 대해서 이벤트 감지되는 현상) 방지

    removeFileList = uploadedFileList;  // 삭제할 리스트에 업로드된 파일 추가

    callRemoveFiles();  // 업로드된 파일 삭제

    // location.href =`/board/${menu}/read?id=${dto_id}&${link}`;
    location.href = linkRead;
})


// --------- //
// 업로드 모달
// --------- //

const modalUpload = new bootstrap.Modal(document.querySelector('.modalUpload'));
const addFilesBtn = document.querySelector('.addFilesBtn');
const uploadBtn = document.querySelector('.uploadBtn');
const closeModalUpload = document.querySelector('.closeModalUpload');

// 1. 업로드 모달창 띄우기
addFilesBtn.addEventListener('click', function(e) {
    e.stopPropagation();
    e.preventDefault();

    modalUpload.show();  // 모달창 보이기

})

// 2. 업로드 모달에서 닫기 버튼 클릭
closeModalUpload.addEventListener('click', function(e) {
    e.stopPropagation();
    e.preventDefault();

    modalUpload.hide();  // 모달창 숨기기
})

console.dir("input_files: "+input_files);
console.dir("input_files_result: "+input_files_result);
console.log("input_files: "+input_files.value);
console.log("input_files_result: "+input_files_result.value);

input_files.oninput = function() {
    const fReader = new FileReader();
    fReader.readAsDataURL(input_files.files[0]);


    fReader.onloadend = function(event){
        //const input_files_result = document.querySelector('.input_files_result');
        const imageElement = document.createElement('img');
        const path = event.target.result;

        console.log("PATH: ", path);

        imageElement.src = path;
        imageElement.style.width = '100%';
        imageElement.style.height = '100%';
        imageElement.id = 'input_files_result';

        input_files_result.replaceWith(imageElement);
    }
};

// 3. 업로드 모달에서 uploadBtn 버튼 클릭 : 선택한 파일을 서버에 업로드처리
let uploadedFileList = [];
uploadBtn.addEventListener('click', function(e) {
    e.stopPropagation();
    e.preventDefault();

    // 자바스크립트 FormData 객체를 이용해서 전송할 파일을 저장(보관)
    const formObj = new FormData();
    const fileInput = document.querySelector("input[name='files']");
    // console.log(fileInput.files)  // 선택된 파일들은 배열 구조

    const files = fileInput.files;
    for (let i=0; i<files.length; i++) {
        // 'files'키 이름은 서버에 받을 UploadFileDTO 객체의 files와 동일한 속성명이면 1:1 자동 맵핑
        formObj.append("files", files[i]);  // javascript FormData객체를 이용해서 정보를 추가
    }

    console.log('formObj: ', formObj);

    // 업로드 처리하는 함수 호출: axios요청  **
    uploadToServer(formObj)
        .then( result => {
            console.log("result: ",result);
            //console.log("업로드 정상처리 응답결과: ",result.data);
            uploadedFileList = result;

            // 업로드된 결과를 JSON파일로 view 함수 호출
            for (let data of result) {
                showUploadFile(data);
            }

            modalUpload.hide() // 모달창 숨기기

        }).catch( e => {
            alert("업로드 실패했습니다~");
            console.log("업로드 실패했습니다~ ",e);
            modalUpload.hide();
        });

}) // end function


// 업로드된 결과값을 view해서 보여줄 태그 요소 추출
const uploadResult = document.querySelector('.uploadResult');

// 업로드된 결과 view 함수 정의
function showUploadFile({uuid, fileName, img, link}) {  // link는 UploadResultDTO의 getLink()를 실행하여 결과값을 받아옴
    // 서버에서 보내준 data => UploadResultDTO(UploadResultDTO.uuid, xxx.fileName, xxx.img, xxx.getLink())구조 객체 => JSON변환(객체.속성, ... 객체.메서드)
    // 서버로부터 넘겨받은 객체.속성 : uuid, fileName, img
    // 서버로부터 넘겨받은 객체.메서드 : 객체.Link() => Link 메서드 이름으로 데이터 보관된 상태이므로 함수명으로 데이터 읽기가능
    console.log("fileName: ",fileName);
    console.log("uuid: ",uuid);
    console.log("link: ",link);
    console.log("img: ",img);

    const str =
    `
    <div class="card" style="width: 18rem;">
        <div class="card-header new-card d-flex justify-content-center">
            <p>${fileName}</p>
            <button type="button" class="btn btn-outline-danger opacity-50" style="margin-left:auto"
                    onClick="javascript:removeFile('${uuid}', '${fileName}', this)" > x </button>
        </div>
        <div class="card-body">
            <img src="${contextPath}view/${link}" data-src="${uuid+"==vb=="+fileName}" class="w-100"/>
        </div>
    </div>
    `;

    console.log("str: ",str);
    // 문자열 => DOM으로 변환 => '.uploadResult'요소 하위요소 입력
    uploadResult.innerHTML += str;

} // end function

// ------------------------ //
// 첨부파일 서버 등록 함수 정의
// ------------------------ //

function appendFileData(){

    // 파일이 없으면 등록안하고 종료 (이미지가 안찍히면 오류나기 때문)
    console.log("uploadedFileList",uploadedFileList);
    if (uploadedFileList.length == 0) {  // 업로드된 파일이 없으면
            console.log("uploadedFileList.length == 0");
        if (fileNames.length == 0) {  // 기존 파일이 없으면 리턴
            console.log("fileNames.length == 0");
            return
        };
    }

    const target = document.querySelector('.uploadHidden');
    const uploadFiles = uploadResult.querySelectorAll('img');  // 불러온 업로드된 이미지들은 배열구조

    let str = "";
    for (let i=0; i<uploadFiles.length; i++) { // 이미지 첨부파일이 1개이상일 경우
        const uploadFile = uploadFiles[i];  // 배열에서 뽑은 img태그
        // const imgLink = uploadFile.getAttribute('src');  // 이미지 링크  제약조건 때문에 안됨
        const imgData = uploadFile.getAttribute('data-src');
        console.log(imgData);

        // 이미지 파일 이름을 hidden input 요소에 저장
        str += `<input type='hidden' name='fileNames' value="${imgData}">`;  // 전송할때 /(링크로 간주)가 들어가면 제약조건에 걸림(보안때문)

    } // end for

    target.innerHTML = str;

} // end function


// -------------------------------------------------- //
// 게시글 수정 작업에서 첨부파일 삭제:
// 1. 이미지를 화면에 보이지 않도록 제거
// 2. 수정하기 버튼을 누르면 업로드된 파일 삭제
// -------------------------------------------------- //

// 1. 이미지를 화면에 보이지 않도록 제거

// 실제 수정작업 진행시 살제될 파일 목록 저장
let removeFileList = [] // 배열 객체 선언

function removeFile(uuid, fileName, obj) {  // obj는 this(이벤트가 발생한 태그)
    console.log("uuid: ",uuid);
    console.log("fileName: ",fileName);
    console.log("obj: ",obj);  // 현재 버튼 요소를 지칭

    if(!confirm("파일을 삭제하겠나옹?")) {  // 취소를 누르면 return // 확인 누르면 삭제 진행
        return;
    }

    // 삭제할 파일목록 저장
    removeFileList.push({uuid, fileName});

    // 요소.closest('선택요소') : 요소기준으로 상위 요소중 가장 가까운 요소 선택
    // 현재 요소에서 상위(부모)요소 찾을 경우 적용

    // 자바스크립트 요소제거 : remove() removeChild()
    // '.card' 요소제거 : 이미지 화면에서 삭제
    const targetDiv = obj.closest('.card');
    targetDiv.remove()


} // end function


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


// -------------------------------------------------------- //
// 이게 첨부파일 등록할때 하는거 아닌가?
function appendNotShownData(){    // [remove] 버튼 클릭시 첨부파일 관련 정보 삭제

    // 삭제할 첨부파일 목록 => [X]버튼 클릭시 선택된 첨부파일이름
    if (removeFileList.length == 0){ return }

    const target = document.querySelector('.uploadHidden')
    let str = ''
    for (let i=0; i<removeFileList.length; i++){
      // const {a,b} = {10,20} => 구조분할할당
      const {uuid, fileName} = removeFileList[i]  // removeFileList.push({uuid, fileName})
      str += `<input type='hidden', name='fileNames' value="${uuid}==vb==${fileName}">`
    }
    target.innerHTML += str;

}


// -------- //
// 수정 버튼
// -------- //

function confirmFun(e) {
    e.preventDefault();  // 기본 이벤트 제거
    e.stopPropagation();  // 버블링(현재 이벤트가 발생한 요소의 상위 요소들에 대해서 이벤트 감지되는 현상) 방지
    console.log("click")

    // const formObj = document.querySelector('form');  // form 이런식으로 태그를 직접 찍으면 나중에 추가, 수정 작업을 하면서 중복되서 오류날 가능성 높음. 비추천
    const formObj = document.querySelector('.modifyForm');
    const title = document.querySelector('.modifyForm input[name="title"]');
    const content = document.querySelector('.modifyForm textarea[name="content"]');
    console.log("formObj", formObj);
    console.log(title.value);

    // 서버로 부터 응답받은 메시지: db처리한 후 결과 값 전송한 객체
    console.log('errors',errors);

    let errMessage="";
    if (errors) {  // 서버로부터 응답 에러 메시지 처리
        for (let i=0; i<errors.length; i++) {
            errMessage += `${errors[i].field}은 ${errors[i].code}\n`;

        }
        // 웹브라우저의 세션 기록을 관리하는 JavaScript의 History API메서드
        // 브라우저의 세션 기록을 수정하거 대체하는 역할
        // history.replaceState({},null, null);

        alert(errMessage);
    }

    // 에러 체크

    if ((title.value.length < 1 || title.value == "")&&(content.value.length < 1 || content.value == "")) {
        alert("제목과 내용이 비어있습니다!! 😡😡");
        return;

    } else if (title.value.length < 1 || title.value == "") {
        alert("제목이 비어있습니다! 😡");
        return;

    } else if (content.value.length < 1 || content.value == "") {
        alert("내용이 비어있습니다! 😡");
        return;

    }

    if (title.value.length < 3 || title.value.length > 100) {
        alert("3자 이상 100자 이내에 제목을 써주세요 bitch :b");
        title.focus();
        return;

    }

    if (content.value.length < 3 || content.value.length > 2000) {
        alert("3자 이상 2000자 이내에 글을 써주세요 bitch :b");
        content.focus();
        return;

    }

    // 2. 수정하기 버튼을 누르면 업로드된 파일 삭제 및 등록

    // 첨부파일 삭제 함수 호출
    callRemoveFiles();

    // 첨부파일 등록 함수 호출 => 이미지첨부파일 form에 추가
    appendFileData();

    // View 표시되지 않은 첨부파일(x버튼클릭시 화면에서 숨기기한 첨부파일 목록) form에 추가 => boardImage에 등록된 자료 삭제 요청
    //appendNotShownData();


    // 에러 체크 후 정상일때 처리

    // self .location= "/board/"+menu+"/modify?"+link;  // Get방식(수정페이지 요청)

    // formObj.action = `/board/${menu}/modify?${link}`;  // 전송할 링크 설정
    // formObj.method = 'post';  // 전송 방식 설정  // 설정 안하면 기본값 Get

    //const modDateBox = document.querySelector('#modDateBox');
    //modDateBox.innerHTML = '<>';

    // submit() : 폼 전송 기능   reset() : 폼 리셋 기능
    // html 태그의 id 이름을 submit, reset 이런 기능있는 애들로 주면 해당 기능이 동작 안함
    // id="submit"으로 버튼에 ID를 설정하면, 기본적으로 document 객체에서 submit() 메서드를 덮어쓰게 됩니다. 이 때문에 submit() 메서드를 호출해도 해당 메서드가 아니라 버튼 요소를 참조하게 되어 폼이 제출되지 않습니다.

    formObj.submit();  // 전송

}