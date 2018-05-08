<?php

$connect = mysqli_connect('localhost','root','gkgustn32','DIY') or die('MYSQL 연결에 실패했습니다.');

$input = json_decode(file_get_contents("php://input"));

$smokeID=$input->smokeid;

$query = "select user_ID, GPA, review from DIY_smoke_review where smokeID = '$smokeID';";
$result = mysqli_query($connect, $query);
$resultArray = array();
while($row = mysqli_fetch_assoc($result)){
	$arrayMiddle = array (
			"user_ID" => urlencode($row['user_ID']),
			"GPA" => urlencode($row['GPA']),
			"review" => urlencode($row['review']));
	array_push($resultArray, $arrayMiddle);
}
print_r(urldecode(json_encode($resultArray)));
mysqli_close($connect);


?>


