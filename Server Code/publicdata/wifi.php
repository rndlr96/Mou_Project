<?php
error_reporting(E_ALL);
ini_set("display_errors",1);
$db_host = "localhost";
$db_id = "root";
$db_password = "gkgustn32";
$db_dbname = "public";

$db_conn = mysqli_connect( $db_host, $db_id, $db_password, $db_dbname ) or die ( "Fail to connect database!!");

$query = "select name,longi,lati,url from wifi,wifi_url where wifi.wifiid = wifi_url.wifiid";

$result = mysqli_query($db_conn, $query);
$resultArray = array();
while($row = mysqli_fetch_assoc($result)) {
	$arrayMiddle = array (
			"name"=> urlencode( $row ['name'] ),
			"longitude"=> urlencode( $row ['longi'] ),
			"latitude"=> urlencode( $row ['lati'] ),
			"url"=> urlencode( $row ['url'] )
				);
	array_push($resultArray, $arrayMiddle);
}
print_r( urldecode( json_encode($resultArray) ) );
mysqli_close($db_conn);
?>



