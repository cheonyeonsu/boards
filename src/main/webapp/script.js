function chkForm() {
	var f = document.frm; //form태그
	if (frm.title.value == '') { //name이 title인 인풋테그의 입력된 값을 가져온다. 
		alert("제목을 입력해주세요.")
		return false;
	}

	if (frm.user_id.value == '') {
		alert("아이디 입력해주세요.")
		return false;
	}

	if (frm.content.value == '') {
		alert("글 내용을 입력해주세요.")
		return false;
	}
	f.submit(); //폼태그 전송
}

function chkDelete(boards_no) {
	const result = confirm("삭제하시겠습니까?"); ㅣ

	if (result) {
		const url = location.origin;
		location.href = url + "/boards/delete?boards_no=" + boards_no;
	} else {
		false;
	}
}
