
<?php
 $host = 'localhost';
 $user = 'root';
 $pw = 'gkgustn32';
 $dbName = 'people';
 $mysqli = new mysqli($host, $user, $pw, $dbName);
 $input = json_decode(file_get_contents("php://input"));

 
 $id=$input->id;
 $password=$input->pwd;
 $name=$input->name;
 $sex=$input->sex;
 $birthDay=$input->birthDay;
 $email=$input->email;
 $phonenum=$input->PhoneNumber;
 $query = "select count(*) from account_info where id = '$id'";
 $res = $mysqli->query($query);
 $rs = mysqli_fetch_row($res);
 $already_registered_count = $rs[0];
 if($already_registered_count!= 0)
 {
	echo '[아이디중복]회원가입 실패';
	http_response_code(203);
	die;
}

else
{

 $sql2 = "insert into account_info (id, pwd, name, sex, birthDay, email,PhoneNumber)";
 $sql2 = $sql2. "values('$id','$password','$name','$sex','$birthDay','$email','$phonenum')";
 if($mysqli->query($sql2)){
  echo 'success inserting';
  http_response_code(200);
 }else{
  echo 'fail to insert sql';
  http_response_code(204);
 }
}
?>
