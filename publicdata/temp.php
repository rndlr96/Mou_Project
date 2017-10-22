<?php

$conn = mysqli_connect("localhost", "root", "gkgustn32", "public") or die;

$query = "select toiletID from toilet;";

$result = mysqli_query($conn, $query);

while($row = mysqli_fetch_array($result)){
	$num = $row[0];
	$query = "insert into toilet_url value ('$num' , 'http://imgur.com/dS0qS35');";
	mysqli_query($conn, $query);
}
mysqli_close($conn);

?>
