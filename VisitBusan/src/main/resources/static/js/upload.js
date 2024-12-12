console.log("/js/upload.js.....")

var contextPath = document.querySelector('.dataCon').getAttribute('data-contextPath');
console.log("contextPath1",contextPath);

// ------------------------------------------------------ //
// Server에 파일 upload 처리 : axios(비동기) 요청 테스트
// ------------------------------------------------------ //
 async function uploadToServer(formObj){
  console.log("uploadToServer axios: ",formObj);

  // 서버쪽에 "/upload" 요청시
  // UpDownController컨트롤러에서  RestAPI형식으로 요청한 url분석하여 실제 업로드 작업처리
  const response = await axios({
    method: 'post',
    url: `${contextPath}upload`,
    data: formObj,
    header: {
      'Content-Type': 'multipart/form-data'
    },
  }) // end axios()

  return response.data
}

// ------------------------------------------------------ //
// Server에  upload 파일 삭제 처리 : axios(비동기) 요청 테스트
// ------------------------------------------------------ //
 async function removeFileToServer({uuid, fileName}){
  const response = await axios.delete(`${contextPath}remove/${uuid}==vb==${fileName}`)

  return response.data
 }

