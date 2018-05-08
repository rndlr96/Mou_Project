<?php


$connect = mysqli_connect("localhost", "root", "gkgustn32","people") or die("MYSQL Server 연결에 실패했습니다");

$json_obj = json_decode(file_get_contents("php://input"));

$id = $json_obj->ID; // 받아온 json 형식의 데이터에서 ID 태그의 데이터
$pw = $json_obj->PW; // 받아온 json 형식의 데이터에서 PW 태그의 데이터

$query = "insert into test values('$id','$pw')"; // 데이터 삽입을 위한 SQL

mysqli_query($connect, $query); // 쿼리 실행

$query = "select id from account_info where id='$id' and pwd='$pw'"; // 조회를 위한 SQL

$result = mysqli_query($connect, $query);

$count = mysqli_num_rows($result);

if($count >= 1){
	http_response_code(200);
}
else{
	http_response_code(204);
}

mysqli_close($connect);

?>
