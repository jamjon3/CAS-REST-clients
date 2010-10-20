<?php


//CAS protected resource that we want to access
$CAS_service = 'https://epierce.dev.acomp.usf.edu/cas-test.php';
//CAS Server URL
$CAS_server = 'https://authtest.acomp.usf.edu:443';
//Valid Username & Password
$CAS_user = "";
$CAS_pass = "";

//Include the RESTcas Client
require_once 'RESTcas.php';

$RESTcas = new RESTcas;

//Uncomment for debug output
//$RESTcas->setDebug();

if ($RESTcas->CAS_LOGIN($CAS_server,$CAS_user,$CAS_pass)) {
	$response =  $RESTcas->CAS_GET($CAS_service);
	echo "$CAS_service response: $response \n";
	$RESTcas->CAS_LOGOUT();
} else {
	echo "CAS login Failed!\n";
}


?>
