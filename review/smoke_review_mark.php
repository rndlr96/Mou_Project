<?php

$connect = mysqli_connect('localhost','root','gkgustn32','DIY') or die('MYSQL 연결에 실패했습니다.');

$input = json_decode(file_get_contents("php://input"));

$smokeID=$input->smokeid;

$query = "select AVG(GPA), name from DIY_smoke where smokeID = '$smokeID';";
$result = mysqli_query($connect, $query);
$resultArray = array();
while($row = mysqli_fetch_assoc($result)){
	$arrayMiddle = array (
			"GPA" => urlencode($row['GPA']),
			"name" => urlencode($row['name']));
	array_push($resultArray, $arrayMiddle);
}

print_r(urldecode(json_encode($resultArray)));
mysqli_close($connect);

?>

