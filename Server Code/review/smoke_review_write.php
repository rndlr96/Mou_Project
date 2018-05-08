<?php

$connect = mysqli_connect('localhost','root','gkgustn32','DIY') or die('MYSQL 연결에 실패했습니다.');

$input = json_decode(file_get_contents("php://input"));

$smokeID=$input->smokeid;
$userID=$input->userID;
$GPA=$input->GPA;
$review=$input->review;

$query = "insert into DIY_toilet_review values('$smokeID','$userID','$GPA','$review');";

mysqli_query($connect,$query);

http_response_code(200);

mysqli_close($connect);

?>


