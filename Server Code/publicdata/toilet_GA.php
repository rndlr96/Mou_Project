<?php
$db_host = "localhost";
$db_id = "root";
$db_password = "gkgustn32";
$db_dbname = "public";

$db_conn = mysqli_connect( $db_host, $db_id, $db_password, $db_dbname ) or die ( "Fail to connect database!!");

$query = "select longi,lati from toilet_GA";

$result = mysqli_query($db_conn, $query);
$resultArray = array();
while($row = mysqli_fetch_assoc($result)) {
	$arrayMiddle = array (
			"longitude"=> urlencode( $row ['longi'] ),
			"latitude"=> urlencode($row ['lati'])
				);
	array_push($resultArray, $arrayMiddle);
}
print_r( urldecode( json_encode($resultArray) ) );
mysqli_close($db_conn);
?>



