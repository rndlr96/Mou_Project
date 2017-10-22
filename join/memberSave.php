
<?php
 $host = 'localhost';
 $user = 'root';
 $pw = 'gkgustn32';
 $dbName = 'people';
 $mysqli = new mysqli($host, $user, $pw, $dbName);
 
 $id=$_POST['id'];
 $password=$_POST['pwd'];
 $password2=$_POST['pwd2'];
 if($password != $password2)
{
 echo '비밀번호 확인이 일치하지 않습니다!';
 die;
}
 $name=$_POST['name'];
 $address=$_POST['addr'];
 $sex=$_POST['sex'];
 $birthDay=$_POST['birthDay'];
 $email=$_POST['email'];
 $phonenum=$_POST['PhoneNumber'];
 $query = "select count(*) from account_info where id = '$id'";
 $res = $mysqli->query($query);
 $rs = mysqli_fetch_row($res);
 $already_registered_count = $rs[0];
 if($already_registered_count!= 0)
 {
	echo '[아이디중복]회원가입 실패';
	die;
}

else
{

 $sql2 = "insert into account_info (id, pwd, name, addr, sex, birthDay, email,PhoneNumber)";
 $sql2 = $sql2. "values('$id','$password','$name','$address','$sex','$birthDay','$email','$PhoneNumber')";
 if($mysqli->query($sql2)){
  echo 'success inserting';
 }else{
  echo 'fail to insert sql';
 }
}
?>
