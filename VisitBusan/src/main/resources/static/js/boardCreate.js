console.log("스크립트 실행! :b");

const dataCon = document.querySelector('.dataCon');
const errors = dataCon.getAttribute('data-errors');
const menu = dataCon.getAttribute('data-menu');
const link = dataCon.getAttribute('data-link');
const linkList = dataCon.getAttribute('data-listLink');

// 툴박스 
const toolBox = document.querySelector('.toolBox .UIBox');
toolBox.addEventListener("click", (e)=> {
    e.preventDefault();  // 기본 이벤트 제거
    e.stopPropagation();  // 버블링(현재 이벤트가 발생한 요소의 상위 요소들에 대해서 이벤트 감지되는 현상) 방지
})

const content = document.querySelector('.content');
const rowCountDisplay = document.getElementById("rowCount");
const textCountDisplay = document.getElementById("textCount");

content.addEventListener("input", function(e) {
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

})

// ----------- //
//  에러 메시지
// ----------- //

if(errors != null) {
    console.log('errors',errors);
    console.log('errors.length: ',errors.length);
}

let errorMsg="";

if(errors) {  // 서버로부터 응답받은 에러 메시지가 있으면
    for(let i=0; i<errors.length; i++) {
        errorMsg += `${errors[i].field}은(는) ${errors[i].code}\n`;
    }
        alert(errorMsg);
}


// ----------//
//   취소 버튼
// ----------//

document.querySelector('.cancelBtn').addEventListener('click', function(e) {
    e.preventDefault();  // 기본 이벤트 제거
    e.stopPropagation();  // 버블링(현재 이벤트가 발생한 요소의 상위 요소들에 대해서 이벤트 감지되는 현상) 방지

    // 삭제할 첨부파일 목록: [{xx.xx},...]
    uploadedFileList.forEach( ({uuid, fileName}) => {
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

    // location.href = `/board/${menu}/list?${link}`;
    location.href = linkList;

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
    // console.log("fileInput.files: "+fileInput.files)  // 선택된 파일들은 배열 구조

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
            console.log("업로드 정상처리 응답결과: ",result.data);
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
        <div class="card-header d-flex justify-content-center">
            <p>${fileName}</p>
            <button type="button" class="btn btn-outline-danger opacity-50" style="margin-left:auto"
                    onClick="javascript:removeFile('${uuid}', '${fileName}', this)" > x </button>
        </div>
        <div class="card-body">
            <img src="/view/${link}" data-src="${uuid+"==vb=="+fileName}" class="w-100"/>
        </div>
    </div>
    `;

    console.log("str: ",str);
    // 문자열 => DOM으로 변환 => '.uploadResult'요소 하위요소 입력
    uploadResult.innerHTML += str;

} // end function


// ----------- //
// 첨부파일 삭제
// ----------- //

function removeFile(uuid, fileName, obj) {
    console.log("uuid: ",uuid);
    console.log("fileName: ",fileName);
    console.log("obj: ",obj);  // 현재 버튼 요소를 지칭

    // 요소.closest('선택요소') : 요소기준으로 상위 요소중 가장 가까운 요소 선택
    // 현재 요소에서 상위(부모)요소 찾을 경우 적용
    const targetDiv = obj.closest('.card');

    // 파일 삭제 요청 : axios()
    removeFileToServer({uuid, fileName})
    .then(remove => {
        // 서버로부터 받은 객체정보: 정상적으로 삭제되었으면 HashMap 형식으로 객체 전달
        // HashMap('result': true)
        console.log("remove: ",remove);  // HashMap객체 => remove JSON객체로 받음
        console.log("remove.result: ",remove.result);

        // 자바스크립트 요소제서: remove() removeChild()
        // '.card' 요소제거: 썸네일 이미지 화면에서 삭제
        if(remove.result) targetDiv.remove();  // 삭제 성공하면 썸네일 이미지 화면에서 삭제

    }).catch( e => {
        alert("오류입니다~");
        console.log("오류입니다~ ",e);
    });

} // end function

console.log("test test 테스트입니다~");

// ----------- //
// 첨부파일 등록
// ----------- //

function addFiles () {

    // 파일이 없으면 등록안하고 종료 (이미지가 안찍히면 오류나기 때문)
    console.log("uploadedFileList",uploadedFileList);
    if (uploadedFileList.length == 0) return;

    const target = document.querySelector('.uploadHidden');
    const uploadFiles = uploadResult.querySelectorAll('img');  // 불러온 업로드된 이미지들은 배열구조

    let str = "";
    for (let i=0; i<uploadFiles.length; i++) {
        const uploadFile = uploadFiles[i];  // 배열에서 뽑은 img태그
        // const imgLink = uploadFile.getAttribute('src');  // 이미지 링크  제약조건 때문에 안됨
        const imgData = uploadFile.getAttribute('data-src');

        // 이미지 파일 이름을 hidden input 요소에 저장
        str += `<input type='hidden' name='fileNames' value="${imgData}">`;  // 전송할때 /(링크로 간주)가 들어가면 제약조건에 걸림(보안때문)

    } // end for

    target.innerHTML = str;

}

// ------------------------------------------------ //
// 게시물 등록 : 게시글 정보, 첨부파일이름 DB에 저장 요청
// ------------------------------------------------ //

function submitFun(e) {
    e.preventDefault();  // 기본 이벤트 제거
    e.stopPropagation();  // 버블링(현재 이벤트가 발생한 요소의 상위 요소들에 대해서 이벤트 감지되는 현상) 방지

    const formObj = document.querySelector('.createForm');
    const title = document.querySelector('.createForm input[name="title"]');
    const content = document.querySelector('.createForm textarea[name="content"]');
    console.log(content);
    console.log(title);

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
        alert("3자 이상 100자 이내에 제목을 써주세요~ :b");
        title.focus();
        return;

    }

    if (content.value.length < 3 || content.value.length > 2000) {
        alert("3자 이상 2000자 이내에 글을 써주세요~ :b");
        content.focus();
        return;

    }

    // 첨부파일 등록
    addFiles();


    // 값에 이상이 없으면 서버에 전송
    console.log(formObj);

    // html 태그의 id 이름을 submit, reset 이런 기능있는 애들로 주면 해당 기능이 동작 안함
    // id="submit"으로 버튼에 ID를 설정하면, 기본적으로 document 객체에서 submit() 메서드를 덮어쓰게 됩니다. 이 때문에 submit() 메서드를 호출해도 해당 메서드가 아니라 버튼 요소를 참조하게 되어 폼이 제출되지 않습니다.

    formObj.submit();

}